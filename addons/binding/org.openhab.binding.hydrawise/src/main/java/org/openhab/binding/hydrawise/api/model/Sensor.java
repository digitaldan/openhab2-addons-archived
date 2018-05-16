package org.openhab.binding.hydrawise.api.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sensor {

    @SerializedName("input")
    @Expose
    private Integer input;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("mode")
    @Expose
    private Integer mode;
    @SerializedName("timer")
    @Expose
    private Integer timer;
    @SerializedName("offtimer")
    @Expose
    private Integer offtimer;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("offlevel")
    @Expose
    private Integer offlevel;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("relays")
    @Expose
    private List<Object> relays = null;

    public Integer getInput() {
        return input;
    }

    public void setInput(Integer input) {
        this.input = input;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    public Integer getOfftimer() {
        return offtimer;
    }

    public void setOfftimer(Integer offtimer) {
        this.offtimer = offtimer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOfflevel() {
        return offlevel;
    }

    public void setOfflevel(Integer offlevel) {
        this.offlevel = offlevel;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public List<Object> getRelays() {
        return relays;
    }

    public void setRelays(List<Object> relays) {
        this.relays = relays;
    }

}