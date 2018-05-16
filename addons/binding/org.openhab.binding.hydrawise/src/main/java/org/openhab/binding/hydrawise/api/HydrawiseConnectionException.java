package org.openhab.binding.hydrawise.api;

public class HydrawiseConnectionException extends Exception {

    public HydrawiseConnectionException(Exception e) {
        super(e);
    }

    public HydrawiseConnectionException(String message) {
        super(message);
    }
}
