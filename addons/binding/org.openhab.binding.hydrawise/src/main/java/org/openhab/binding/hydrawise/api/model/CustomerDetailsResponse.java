package org.openhab.binding.hydrawise.api.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerDetailsResponse extends Response {

    @SerializedName("boc_topology_desired")
    @Expose
    private BocTopologyDesired bocTopologyDesired;
    @SerializedName("boc_topology_actual")
    @Expose
    private BocTopologyActual bocTopologyActual;
    @SerializedName("controllers")
    @Expose
    private List<Controller> controllers = null;
    @SerializedName("current_controller")
    @Expose
    private String currentController;
    @SerializedName("is_boc")
    @Expose
    private Boolean isBoc;
    @SerializedName("tandc")
    @Expose
    private Integer tandc;
    @SerializedName("controller_id")
    @Expose
    private Integer controllerId;
    @SerializedName("customer_id")
    @Expose
    private Integer customerId;
    @SerializedName("session_id")
    @Expose
    private String sessionId;
    @SerializedName("hardwareVersion")
    @Expose
    private String hardwareVersion;
    @SerializedName("device_id")
    @Expose
    private Integer deviceId;
    @SerializedName("tandc_version")
    @Expose
    private Integer tandcVersion;
    @SerializedName("features")
    @Expose
    private Features features;

    public BocTopologyDesired getBocTopologyDesired() {
        return bocTopologyDesired;
    }

    public void setBocTopologyDesired(BocTopologyDesired bocTopologyDesired) {
        this.bocTopologyDesired = bocTopologyDesired;
    }

    public BocTopologyActual getBocTopologyActual() {
        return bocTopologyActual;
    }

    public void setBocTopologyActual(BocTopologyActual bocTopologyActual) {
        this.bocTopologyActual = bocTopologyActual;
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    public void setControllers(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public String getCurrentController() {
        return currentController;
    }

    public void setCurrentController(String currentController) {
        this.currentController = currentController;
    }

    public Boolean getIsBoc() {
        return isBoc;
    }

    public void setIsBoc(Boolean isBoc) {
        this.isBoc = isBoc;
    }

    public Integer getTandc() {
        return tandc;
    }

    public void setTandc(Integer tandc) {
        this.tandc = tandc;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getTandcVersion() {
        return tandcVersion;
    }

    public void setTandcVersion(Integer tandcVersion) {
        this.tandcVersion = tandcVersion;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

}