package org.openhab.binding.hydrawise.internal.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Controller {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("last_contact")
    @Expose
    private Integer lastContact;
    @SerializedName("serial_number")
    @Expose
    private String serialNumber;
    @SerializedName("controller_id")
    @Expose
    private Integer controllerId;
    @SerializedName("sw_version")
    @Expose
    private String swVersion;
    @SerializedName("hardware")
    @Expose
    private String hardware;
    @SerializedName("is_boc")
    @Expose
    private Boolean isBoc;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("device_id")
    @Expose
    private Integer deviceId;
    @SerializedName("parent_device_id")
    @Expose
    private Object parentDeviceId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("customer_id")
    @Expose
    private Integer customerId;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("last_contact_readable")
    @Expose
    private String lastContactReadable;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("status_icon")
    @Expose
    private String statusIcon;
    @SerializedName("online")
    @Expose
    private Boolean online;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLastContact() {
        return lastContact;
    }

    public void setLastContact(Integer lastContact) {
        this.lastContact = lastContact;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public Boolean getIsBoc() {
        return isBoc;
    }

    public void setIsBoc(Boolean isBoc) {
        this.isBoc = isBoc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Object getParentDeviceId() {
        return parentDeviceId;
    }

    public void setParentDeviceId(Object parentDeviceId) {
        this.parentDeviceId = parentDeviceId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLastContactReadable() {
        return lastContactReadable;
    }

    public void setLastContactReadable(String lastContactReadable) {
        this.lastContactReadable = lastContactReadable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(String statusIcon) {
        this.statusIcon = statusIcon;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}