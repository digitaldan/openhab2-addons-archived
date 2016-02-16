package org.openhab.io.hueemulation.internal.api;

public class HueErrorResponse {
    public final static int UNAUTHORIZED = 1;
    public final static int NOT_AVAILABLE = 3;
    public final static int METHOD_NOT_AVAILABLE = 4;
    public final static int LINK_BUTTON_NOT_PRESSED = 101;
    public final static int INTERNAL_ERROR = 901;

    public HueErrorMessage error;

    public HueErrorResponse(int type, String address, String description) {
        super();
        this.error = new HueErrorMessage(type, address, description);
    }

    public class HueErrorMessage {
        public int type;
        public String address;
        public String description;

        public HueErrorMessage(int type, String address, String description) {
            super();
            this.type = type;
            this.address = address;
            this.description = description;
        }

    }
}
