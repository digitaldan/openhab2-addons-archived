package org.openhab.io.hueemulation.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HueEmulationUpnpServer extends Thread {
    private Logger logger = LoggerFactory.getLogger(HueEmulationUpnpServer.class);

    static final private int UPNP_PORT = 1900;
    static final private String MULTI_ADDR = "239.255.255.250";
    private MulticastSocket socket;
    private boolean running;
    private String discoPath;

    // TODO replace 00000000-722C-4995-89F2-ABCDEF000000
    private String discoString = "HTTP/1.1 200 OK\r\n" + "CACHE-CONTROL: max-age=86400\r\n" + "EXT:\r\n"
            + "LOCATION: %s\r\n" + "OPT: \"http://schemas.upnp.org/upnp/1/0/\"; ns=01\r\n"
            + "01-NLS: 00000000-722C-4995-89F2-ABCDEF000000\r\n" + "ST: urn:schemas-upnp-org:device:basic:1\r\n"
            + "USN: uuid:00000000-722C-4995-89F2-ABCDEF000000\r\n\r\n";

    public HueEmulationUpnpServer(String discoPath) {
        this.running = true;
        this.discoPath = discoPath;
    }

    public void shutdown() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                InetSocketAddress socketAddr = new InetSocketAddress(MULTI_ADDR, UPNP_PORT);
                socket = new MulticastSocket(UPNP_PORT);
                socket.joinGroup(socketAddr, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                while (running) {
                    socket.receive(recv);
                    if (recv.getLength() > 0) {
                        String data = new String(recv.getData());
                        if (data.startsWith("M-SEARCH")) {
                            String msg = String.format(discoString,
                                    "http://" + InetAddress.getLocalHost().getHostAddress().toString() + ":"
                                            + System.getProperty("org.osgi.service.http.port") + discoPath);
                            DatagramPacket response = new DatagramPacket(msg.getBytes(), msg.length(),
                                    recv.getAddress(), recv.getPort());
                            try {
                                socket.send(response);
                            } catch (IOException e) {
                                logger.error("Could not send UPNP response", e);
                            }
                        }
                    }
                }
            } catch (SocketException e) {
                logger.error("Socket error with UPNP server", e);
            } catch (IOException e) {
                logger.error("IO Error with UPNP server", e);
            } finally {
                if (running) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
