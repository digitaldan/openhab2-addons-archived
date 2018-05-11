package org.openhab.binding.hydrawise.internal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Running {

    @SerializedName("relay")
    @Expose
    private String relay;
    @SerializedName("relay_id")
    @Expose
    private String relayId;
    @SerializedName("time_left")
    @Expose
    private Integer timeLeft;
    @SerializedName("run")
    @Expose
    private String run;

    public String getRelay() {
        return relay;
    }

    public void setRelay(String relay) {
        this.relay = relay;
    }

    public String getRelayId() {
        return relayId;
    }

    public void setRelayId(String relayId) {
        this.relayId = relayId;
    }

    public Integer getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(Integer timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

}