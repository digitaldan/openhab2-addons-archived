package org.openhab.binding.hydrawise.internal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetZoneResponse extends Response {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("message_type")
    @Expose
    private String messageType;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

}
