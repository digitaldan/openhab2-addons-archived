/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.matter.internal.bridge;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link SetupPayload}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class SetupPayload {
    public enum CommissioningFlow {
        Standard(0),
        UserIntent(1),
        Custom(2);

        private final int value;

        CommissioningFlow(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static CommissioningFlow fromInt(int value) {
            for (CommissioningFlow flow : CommissioningFlow.values()) {
                if (flow.value == value) {
                    return flow;
                }
            }
            throw new IllegalArgumentException("Invalid flow value");
        }
    }

    private final int longDiscriminator;
    private final int shortDiscriminator;
    private final int pincode;
    private final int discovery;
    private final CommissioningFlow flow;
    private final int vid;
    private final int pid;

    public SetupPayload(int discriminator, int pincode, int discovery, CommissioningFlow flow, int vid, int pid) {
        this.longDiscriminator = discriminator;
        this.shortDiscriminator = discriminator >> 8;
        this.pincode = pincode;
        this.discovery = discovery;
        this.flow = flow;
        this.vid = vid;
        this.pid = pid;
    }

    public static String generateQRCode(int discriminator, int pincode) {
        return generateQRCode(discriminator, pincode, 4, CommissioningFlow.Standard, 0, 0);
    }

    public static String generateQRCode(int discriminator, int pincode, int discovery, CommissioningFlow flow, int vid,
            int pid) {
        SetupPayload payload = new SetupPayload(discriminator, pincode, discovery, flow, vid, pid);
        BitSet bits = new BitSet();
        int offset = 0;

        offset = encodeBits(bits, offset, 4, 0); // padding
        offset = encodeBits(bits, offset, 27, payload.pincode);
        offset = encodeBits(bits, offset, 12, payload.longDiscriminator);
        offset = encodeBits(bits, offset, 8, payload.discovery);
        offset = encodeBits(bits, offset, 2, payload.flow.getValue());
        offset = encodeBits(bits, offset, 16, payload.pid);
        offset = encodeBits(bits, offset, 16, payload.vid);
        offset = encodeBits(bits, offset, 3, 0); // version

        byte[] bytes = bits.toByteArray();
        reverseBytes(bytes);
        return "MT:" + Base38.encode(bytes);
    }

    public static String generateManualCode(int discriminator, int pincode) {
        return generateManualCode(discriminator, pincode, CommissioningFlow.Standard, 0, 0);
    }

    public static String generateManualCode(int discriminator, int pincode, CommissioningFlow flow, int vid, int pid) {
        SetupPayload payload = new SetupPayload(discriminator, pincode, 4, flow, vid, pid);
        BitSet bits = new BitSet();
        int offset = 0;

        offset = encodeBits(bits, offset, 1, 0); // version
        offset = encodeBits(bits, offset, 1, payload.flow == CommissioningFlow.Standard ? 0 : 1);
        offset = encodeBits(bits, offset, 4, payload.shortDiscriminator);
        offset = encodeBits(bits, offset, 14, payload.pincode & 0x3FFF); // pincode_lsb
        offset = encodeBits(bits, offset, 13, payload.pincode >> 14); // pincode_msb
        offset = encodeBits(bits, offset, 16, payload.flow == CommissioningFlow.Standard ? 0 : payload.vid);
        offset = encodeBits(bits, offset, 16, payload.flow == CommissioningFlow.Standard ? 0 : payload.pid);
        offset = encodeBits(bits, offset, 7, 0); // padding

        StringBuilder payloadBuilder = new StringBuilder();

        payloadBuilder.append(getChunk(bits, 0, 4));
        payloadBuilder.append(getChunk(bits, 4, 16));
        payloadBuilder.append(getChunk(bits, 20, 13));
        if (payload.flow != CommissioningFlow.Standard) {
            payloadBuilder.append(String.format("%05d", payload.vid));
            payloadBuilder.append(String.format("%05d", payload.pid));
        }
        String manualCode = payloadBuilder.toString();
        return manualCode + Verhoeff.generateVerhoeff(manualCode);
    }

    private static int encodeBits(BitSet bits, int offset, int length, int value) {
        for (int i = 0; i < length; i++) {
            bits.set(offset + i, (value & (1 << i)) != 0);
        }
        return offset + length;
    }

    private static String getChunk(BitSet bits, int start, int length) {
        int value = 0;
        for (int i = 0; i < length; i++) {
            if (bits.get(start + i)) {
                value |= (1 << i);
            }
        }
        return String.format("%0" + (length / 4) + "d", value);
    }

    private static void reverseBytes(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    private static class Base38 {
        private static final char[] CODES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                '-', '.' };
        private static final int RADIX = CODES.length;
        private static final int[] BASE38_CHARS_NEEDED_IN_CHUNK = { 2, 4, 5 };
        private static final int MAX_BYTES_IN_CHUNK = 3;
        private static final int MAX_ENCODED_BYTES_IN_CHUNK = 5;

        public static String encode(byte[] bytes) {
            int totalBytes = bytes.length;
            StringBuilder qrcode = new StringBuilder();

            for (int i = 0; i < totalBytes; i += MAX_BYTES_IN_CHUNK) {
                int bytesInChunk = (i + MAX_BYTES_IN_CHUNK) > totalBytes ? totalBytes - i : MAX_BYTES_IN_CHUNK;

                int value = 0;
                for (int j = 0; j < bytesInChunk; j++) {
                    value += (bytes[i + j] & 0xFF) << (8 * j);
                }

                int base38CharsNeeded = BASE38_CHARS_NEEDED_IN_CHUNK[bytesInChunk - 1];
                while (base38CharsNeeded > 0) {
                    qrcode.append(CODES[value % RADIX]);
                    value /= RADIX;
                    base38CharsNeeded--;
                }
            }

            return qrcode.toString();
        }

        public static byte[] decode(String qrcode) {
            int totalChars = qrcode.length();
            List<Byte> decodedBytes = new ArrayList<>();

            for (int i = 0; i < totalChars; i += MAX_ENCODED_BYTES_IN_CHUNK) {
                int charsInChunk = (i + MAX_ENCODED_BYTES_IN_CHUNK) > totalChars ? totalChars - i
                        : MAX_ENCODED_BYTES_IN_CHUNK;

                int value = 0;
                for (int j = i + charsInChunk - 1; j >= i; j--) {
                    value = value * RADIX + indexOf(qrcode.charAt(j));
                }

                int bytesInChunk = BASE38_CHARS_NEEDED_IN_CHUNK[charsInChunk - 1];
                for (int k = 0; k < bytesInChunk; k++) {
                    decodedBytes.add((byte) (value & 0xFF));
                    value >>= 8;
                }
            }

            byte[] byteArray = new byte[decodedBytes.size()];
            for (int i = 0; i < decodedBytes.size(); i++) {
                byteArray[i] = decodedBytes.get(i);
            }
            return byteArray;
        }

        private static int indexOf(char c) {
            for (int i = 0; i < CODES.length; i++) {
                if (CODES[i] == c) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Invalid Base38 character: " + c);
        }
    }

    /**
     * The Verhoeff algorithm, a checksum formula for error detection first
     * published in 1969, was developed by Dutch mathematician Jacobus Verhoeff.
     * Like the more widely known Luhn algorithm, it works with strings of decimal
     * digits of any length. It detects all single-digit errors and all
     * transposition errors involving two adjacent digits.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Verhoeff_algorithm/">More Info</a>
     * @see <a href="http://en.wikipedia.org/wiki/Dihedral_group">Dihedral Group</a>
     * @see <a href="http://mathworld.wolfram.com/DihedralGroupD5.html">Dihedral Group Order 10</a>
     * @author Colm Rice
     */
    public static class Verhoeff {
        /**
         * The multiplication table.
         */
        private static int[][] d = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
                { 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 }, { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 }, { 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
                { 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 }, { 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 }, { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
                { 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 }, { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };

        /**
         * The inverse table.
         */
        private static int[] inv = new int[] { 0, 4, 3, 2, 1, 5, 6, 7, 8, 9 };

        /**
         * The permutation table.
         */
        private static int[][] p = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
                { 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 }, { 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 }, { 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
                { 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 }, { 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 }, { 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 } };

        /**
         * Generates the Verhoeff digit for the provided numeric string.
         *
         * @param number The numeric string data for Verhoeff compliance check.
         * @return The generated Verhoeff digit for the provided numeric string.
         */
        public static String generateVerhoeff(String number) {
            int checksum = 0;
            int[] reversedIntArray = stringToReversedIntArray(number);
            for (int i = 0; i < reversedIntArray.length; i++) {
                checksum = d[checksum][p[(i + 1) % 8][reversedIntArray[i]]];
            }
            return Integer.toString(inv[checksum]);
        }

        /**
         * Validates that an entered number is Verhoeff compliant.
         *
         * @param number The numeric string data for Verhoeff compliance check.
         * @return TRUE if the provided number is Verhoeff compliant.
         */
        public static boolean validateVerhoeff(String number) {
            int checksum = 0;
            int[] reversedIntArray = stringToReversedIntArray(number);
            for (int i = 0; i < reversedIntArray.length; i++) {
                checksum = d[i][p[i % 8][reversedIntArray[i]]];
            }
            return checksum == 0;
        }

        /**
         * Converts a string to a reversed integer array.
         *
         * @param number The numeric string data converted to reversed int array.
         * @return Integer array containing the digits in the numeric string
         *         provided in reverse.
         */
        private static int[] stringToReversedIntArray(String number) {
            int[] myArray = new int[number.length()];
            for (int i = 0; i < number.length(); i++) {
                myArray[i] = Integer.parseInt(number.substring(i, i + 1));
            }
            return reverse(myArray);
        }

        /**
         * Reverses an int array.
         *
         * @param myArray The input array which needs to be reversed
         * @return The array provided in reverse order.
         */
        private static int[] reverse(int[] myArray) {
            int[] reversed = new int[myArray.length];
            for (int i = 0; i < myArray.length; i++) {
                reversed[i] = myArray[myArray.length - (i + 1)];
            }
            return reversed;
        }
    }
}
