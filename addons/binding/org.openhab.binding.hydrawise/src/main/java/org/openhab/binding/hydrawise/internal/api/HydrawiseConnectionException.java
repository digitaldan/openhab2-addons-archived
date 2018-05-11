package org.openhab.binding.hydrawise.internal.api;

public class HydrawiseConnectionException extends Exception {

    public HydrawiseConnectionException(Exception e) {
        super(e);
    }

    public HydrawiseConnectionException(String message) {
        super(message);
    }
}
