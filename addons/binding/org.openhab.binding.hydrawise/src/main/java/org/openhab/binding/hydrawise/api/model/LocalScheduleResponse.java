package org.openhab.binding.hydrawise.api.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocalScheduleResponse {

    @SerializedName("running")
    @Expose
    private List<Running> running = null;
    @SerializedName("relays")
    @Expose
    private List<Relay> relays = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("time")
    @Expose
    private Integer time;

    public List<Running> getRunning() {
        return running;
    }

    public void setRunning(List<Running> running) {
        this.running = running;
    }

    public List<Relay> getRelays() {
        return relays;
    }

    public void setRelays(List<Relay> relays) {
        this.relays = relays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
