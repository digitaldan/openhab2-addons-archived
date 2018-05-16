package org.openhab.binding.hydrawise.api;

public class HydrawiseCommandBuilder {

    StringBuilder builder;

    public HydrawiseCommandBuilder(String baseURL) {
        builder = new StringBuilder(baseURL);
    }

    public HydrawiseCommandBuilder action(String action) {
        builder.append("&action=" + action);
        return this;
    }

    public HydrawiseCommandBuilder relayId(int relayId) {
        builder.append("&relayId=" + relayId);
        return this;
    }

    public HydrawiseCommandBuilder relayNumber(int number) {
        builder.append("&relay=" + number);
        return this;
    }

    public HydrawiseCommandBuilder duration(int seconds) {
        builder.append("&custom=" + seconds);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
