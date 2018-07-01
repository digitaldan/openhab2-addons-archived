package org.openhab.binding.hydrawise.api;

public class HydrawiseZoneCommandBuilder {

    private StringBuilder builder;

    protected HydrawiseZoneCommandBuilder(String baseURL) {
        builder = new StringBuilder(baseURL);
    }

    protected HydrawiseZoneCommandBuilder(String baseURL, String apiKey) {
        builder = new StringBuilder(baseURL);
        builder.append("&api_key=" + apiKey);
    }

    protected HydrawiseZoneCommandBuilder action(String action) {
        builder.append("&action=" + action);
        return this;
    }

    protected HydrawiseZoneCommandBuilder relayId(int relayId) {
        builder.append("&relay_id=" + relayId);
        return this;
    }

    protected HydrawiseZoneCommandBuilder relayNumber(int number) {
        builder.append("&relay=" + number);
        return this;
    }

    protected HydrawiseZoneCommandBuilder duration(int seconds) {
        builder.append("&custom=" + seconds);
        return this;
    }

    protected HydrawiseZoneCommandBuilder controllerId(int controllerId) {
        builder.append("&controller_id=" + controllerId);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
