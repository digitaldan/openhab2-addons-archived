package org.openhab.binding.hydrawise.internal.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BocTopologyActual {

    @SerializedName("boc_gateways")
    @Expose
    private List<Object> bocGateways = null;

    public List<Object> getBocGateways() {
        return bocGateways;
    }

    public void setBocGateways(List<Object> bocGateways) {
        this.bocGateways = bocGateways;
    }

}