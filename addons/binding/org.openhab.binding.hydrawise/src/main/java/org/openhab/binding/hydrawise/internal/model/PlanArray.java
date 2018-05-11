package org.openhab.binding.hydrawise.internal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlanArray {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("planType")
    @Expose
    private String planType;
    @SerializedName("planType_key")
    @Expose
    private String planTypeKey;
    @SerializedName("sku")
    @Expose
    private Object sku;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("cost")
    @Expose
    private String cost;
    @SerializedName("cost_us")
    @Expose
    private String costUs;
    @SerializedName("cost_au")
    @Expose
    private String costAu;
    @SerializedName("cost_eu")
    @Expose
    private String costEu;
    @SerializedName("cost_ca")
    @Expose
    private String costCa;
    @SerializedName("cost_uk")
    @Expose
    private String costUk;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("controller_qty")
    @Expose
    private String controllerQty;
    @SerializedName("rainfall")
    @Expose
    private String rainfall;
    @SerializedName("sms_qty")
    @Expose
    private String smsQty;
    @SerializedName("scheduled_reports")
    @Expose
    private String scheduledReports;
    @SerializedName("email_alerts")
    @Expose
    private String emailAlerts;
    @SerializedName("define_sensor")
    @Expose
    private String defineSensor;
    @SerializedName("add_user")
    @Expose
    private String addUser;
    @SerializedName("contractor")
    @Expose
    private String contractor;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("sensor_pack")
    @Expose
    private String sensorPack;
    @SerializedName("filelimit")
    @Expose
    private String filelimit;
    @SerializedName("filetypeall")
    @Expose
    private String filetypeall;
    @SerializedName("plan_type")
    @Expose
    private String plan_type;
    @SerializedName("push_notification")
    @Expose
    private String pushNotification;
    @SerializedName("weather_qty")
    @Expose
    private String weatherQty;
    @SerializedName("weather_free_qty")
    @Expose
    private String weatherFreeQty;
    @SerializedName("reporting_days")
    @Expose
    private String reportingDays;
    @SerializedName("weather_hourly_updates")
    @Expose
    private String weatherHourlyUpdates;
    @SerializedName("free_enthusiast_plans")
    @Expose
    private String freeEnthusiastPlans;
    @SerializedName("visible")
    @Expose
    private String visible;
    @SerializedName("contractor_purchasable")
    @Expose
    private String contractorPurchasable;
    @SerializedName("boc")
    @Expose
    private String boc;
    @SerializedName("expiry")
    @Expose
    private String expiry;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("customerplan_id")
    @Expose
    private String customerplanId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getPlanTypeKey() {
        return planTypeKey;
    }

    public void setPlanType1Key(String planTypeKey) {
        this.planTypeKey = planTypeKey;
    }

    public Object getSku() {
        return sku;
    }

    public void setSku(Object sku) {
        this.sku = sku;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCostUs() {
        return costUs;
    }

    public void setCostUs(String costUs) {
        this.costUs = costUs;
    }

    public String getCostAu() {
        return costAu;
    }

    public void setCostAu(String costAu) {
        this.costAu = costAu;
    }

    public String getCostEu() {
        return costEu;
    }

    public void setCostEu(String costEu) {
        this.costEu = costEu;
    }

    public String getCostCa() {
        return costCa;
    }

    public void setCostCa(String costCa) {
        this.costCa = costCa;
    }

    public String getCostUk() {
        return costUk;
    }

    public void setCostUk(String costUk) {
        this.costUk = costUk;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getControllerQty() {
        return controllerQty;
    }

    public void setControllerQty(String controllerQty) {
        this.controllerQty = controllerQty;
    }

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }

    public String getSmsQty() {
        return smsQty;
    }

    public void setSmsQty(String smsQty) {
        this.smsQty = smsQty;
    }

    public String getScheduledReports() {
        return scheduledReports;
    }

    public void setScheduledReports(String scheduledReports) {
        this.scheduledReports = scheduledReports;
    }

    public String getEmailAlerts() {
        return emailAlerts;
    }

    public void setEmailAlerts(String emailAlerts) {
        this.emailAlerts = emailAlerts;
    }

    public String getDefineSensor() {
        return defineSensor;
    }

    public void setDefineSensor(String defineSensor) {
        this.defineSensor = defineSensor;
    }

    public String getAddUser() {
        return addUser;
    }

    public void setAddUser(String addUser) {
        this.addUser = addUser;
    }

    public String getContractor() {
        return contractor;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSensorPack() {
        return sensorPack;
    }

    public void setSensorPack(String sensorPack) {
        this.sensorPack = sensorPack;
    }

    public String getFilelimit() {
        return filelimit;
    }

    public void setFilelimit(String filelimit) {
        this.filelimit = filelimit;
    }

    public String getFiletypeall() {
        return filetypeall;
    }

    public void setFiletypeall(String filetypeall) {
        this.filetypeall = filetypeall;
    }

    public String getPlan_Type() {
        return plan_type;
    }

    public void setPlan_Type(String plan_type) {
        this.plan_type = plan_type;
    }

    public String getPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(String pushNotification) {
        this.pushNotification = pushNotification;
    }

    public String getWeatherQty() {
        return weatherQty;
    }

    public void setWeatherQty(String weatherQty) {
        this.weatherQty = weatherQty;
    }

    public String getWeatherFreeQty() {
        return weatherFreeQty;
    }

    public void setWeatherFreeQty(String weatherFreeQty) {
        this.weatherFreeQty = weatherFreeQty;
    }

    public String getReportingDays() {
        return reportingDays;
    }

    public void setReportingDays(String reportingDays) {
        this.reportingDays = reportingDays;
    }

    public String getWeatherHourlyUpdates() {
        return weatherHourlyUpdates;
    }

    public void setWeatherHourlyUpdates(String weatherHourlyUpdates) {
        this.weatherHourlyUpdates = weatherHourlyUpdates;
    }

    public String getFreeEnthusiastPlans() {
        return freeEnthusiastPlans;
    }

    public void setFreeEnthusiastPlans(String freeEnthusiastPlans) {
        this.freeEnthusiastPlans = freeEnthusiastPlans;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getContractorPurchasable() {
        return contractorPurchasable;
    }

    public void setContractorPurchasable(String contractorPurchasable) {
        this.contractorPurchasable = contractorPurchasable;
    }

    public String getBoc() {
        return boc;
    }

    public void setBoc(String boc) {
        this.boc = boc;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getCustomerplanId() {
        return customerplanId;
    }

    public void setCustomerplanId(String customerplanId) {
        this.customerplanId = customerplanId;
    }

}