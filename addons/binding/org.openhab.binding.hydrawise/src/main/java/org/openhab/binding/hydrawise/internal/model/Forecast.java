package org.openhab.binding.hydrawise.internal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Forecast {

    @SerializedName("temp_hi")
    @Expose
    private String tempHi;

    @SerializedName("temp_lo")
    @Expose
    private String tempLo;

    @SerializedName("conditions")
    @Expose
    private String conditions;

    @SerializedName("day")
    @Expose
    private String day;

    @SerializedName("pop")
    @Expose
    private Integer pop;

    @SerializedName("humidity")
    @Expose
    private Integer humidity;

    @SerializedName("wind")
    @Expose
    private String wind;

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("icon_local")
    @Expose
    private String iconLocal;

    public String getTempHi() {
        return tempHi;
    }

    public void setTempHi(String tempHi) {
        this.tempHi = tempHi;
    }

    public String getTempLo() {
        return tempLo;
    }

    public void setTempLo(String tempLo) {
        this.tempLo = tempLo;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getPop() {
        return pop;
    }

    public void setPop(Integer pop) {
        this.pop = pop;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconLocal() {
        return iconLocal;
    }

    public void setIconLocal(String iconLocal) {
        this.iconLocal = iconLocal;
    }

}