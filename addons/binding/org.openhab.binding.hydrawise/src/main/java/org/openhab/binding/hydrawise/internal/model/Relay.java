package org.openhab.binding.hydrawise.internal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Relay {
    @SerializedName("relay_id")
    @Expose
    private Integer relayId;
    @SerializedName("relay")
    @Expose
    private Integer relay;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("lastwater")
    @Expose
    private String lastwater;
    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("run")
    @Expose
    private String runTime;
    @SerializedName("run_seconds")
    @Expose
    private Integer runTimeSeconds;
    @SerializedName("nicetime")
    @Expose
    private String nicetime;
    @SerializedName("id")
    @Expose
    private String id;

    public Integer getRelayId() {
        return relayId;
    }

    public void setRelayId(Integer relayId) {
        this.relayId = relayId;
    }

    public Integer getRelay() {
        return relay;
    }

    public void setRelay(Integer relay) {
        this.relay = relay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLastwater() {
        return lastwater;
    }

    public void setLastwater(String lastwater) {
        this.lastwater = lastwater;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public Integer getRunTimeSeconds() {
        return runTimeSeconds;
    }

    public void setRunTimeSeconds(Integer runTimeSeconds) {
        this.runTimeSeconds = runTimeSeconds;
    }

    public String getNicetime() {
        return nicetime;
    }

    public void setNicetime(String nicetime) {
        this.nicetime = nicetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}