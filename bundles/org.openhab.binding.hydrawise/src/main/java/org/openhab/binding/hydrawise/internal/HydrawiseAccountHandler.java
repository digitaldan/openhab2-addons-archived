package org.openhab.binding.hydrawise.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.hydrawise.internal.api.HydrawiseAuthenticationException;
import org.openhab.binding.hydrawise.internal.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.internal.api.graphql.HydrawiseAuthTokenProvider;
import org.openhab.binding.hydrawise.internal.api.graphql.HydrawiseGraphQLClient;
import org.openhab.binding.hydrawise.internal.api.graphql.schema.AuthToken;
import org.openhab.binding.hydrawise.internal.api.graphql.schema.Customer;
import org.openhab.binding.hydrawise.internal.api.graphql.schema.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class HydrawiseAccountHandler extends BaseBridgeHandler implements HydrawiseAuthTokenProvider {
    /**
     * Minimum amount of time we can poll for updates
     */
    protected static final int MIN_REFRESH_SECONDS = 30;
    protected static final int DEFAULT_REFRESH_SECONDS = 60;
    private final Logger logger = LoggerFactory.getLogger(HydrawiseAccountHandler.class);
    private HydrawiseGraphQLClient apiClient;
    private @Nullable AuthToken token;
    private @Nullable ScheduledFuture<?> pollFuture;
    private int refresh;
    private @Nullable Customer lastData;
    private final List<HydrawiseControllerListener> controllerListeners = new ArrayList<HydrawiseControllerListener>();

    public HydrawiseAccountHandler(Bridge bridge, HttpClient httpClient) {
        super(bridge);
        this.apiClient = new HydrawiseGraphQLClient(httpClient, this);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        logger.debug("Handler initialized.");
        scheduler.schedule(this::configure, 0, TimeUnit.SECONDS);
    }

    @Override
    public void dispose() {
        logger.debug("Handler disposed.");
        clearPolling();
    }

    public void addControllerListeners(HydrawiseControllerListener listener) {
        this.controllerListeners.add(listener);
        Customer data = lastData;
        if (data != null) {
            listener.onData(data.controllers);
        }
    }

    public void removeControllerListeners(HydrawiseControllerListener listener) {
        this.controllerListeners.remove(listener);
    }

    public @Nullable HydrawiseGraphQLClient graphQLClient() {
        return apiClient;
    }

    public @Nullable Customer lastData() {
        return lastData;
    }

    public void refreshData(int delaySeconds) {
        initPolling(delaySeconds, this.refresh);
    }

    private void configure() {
        HydrawiseAccountConfiguration config = getConfig().as(HydrawiseAccountConfiguration.class);
        // TODO switch to Java 11 String.isBlank
        if (StringUtils.isNotBlank(config.userName) && StringUtils.isNotBlank(config.password)) {
            if (!config.savePassword) {
                Configuration editedConfig = editConfiguration();
                editedConfig.remove("password");
                updateConfiguration(editedConfig);
            }
            try {
                authTokenUpdated(apiClient.login(config.userName, config.password));
            } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
                return;
            }
        } else if (StringUtils.isNotBlank(config.refreshToken)) {
            authTokenUpdated(new AuthToken(config.refreshToken));
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Login credentials required.");
            return;
        }
        this.refresh = Math.max(config.refreshInterval != null ? config.refreshInterval : DEFAULT_REFRESH_SECONDS,
                MIN_REFRESH_SECONDS);
        initPolling(0, refresh);
    }

    /**
     * Starts/Restarts polling with an initial delay. This allows changes in the poll cycle for when commands are sent
     * and we need to poll sooner then the next refresh cycle.
     */
    private synchronized void initPolling(int initalDelay, int refresh) {
        clearPolling();
        pollFuture = scheduler.scheduleWithFixedDelay(this::poll, initalDelay, refresh, TimeUnit.SECONDS);
    }

    /**
     * Stops/clears this thing's polling future
     */
    private void clearPolling() {
        ScheduledFuture<?> localFuture = pollFuture;
        if (isFutureValid(localFuture)) {
            if (localFuture != null) {
                localFuture.cancel(false);
            }
        }
    }

    private boolean isFutureValid(@Nullable ScheduledFuture<?> future) {
        return future != null && !future.isCancelled();
    }

    private void poll() {
        poll(true);
    }

    private void poll(boolean retry) {
        try {
            QueryResponse response = apiClient.queryControllers();
            if (getThing().getStatus() != ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
            }
            lastData = response.data.me;
            controllerListeners.forEach(listener -> {
                listener.onData(response.data.me.controllers);
            });
        } catch (HydrawiseConnectionException e) {
            if (retry) {
                logger.debug("Retrying failed poll", e);
                poll(false);
            } else {
                logger.debug("Will try again during next poll period", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            }
        } catch (HydrawiseAuthenticationException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
            clearPolling();
        }
    }

    @Override
    public @Nullable AuthToken getAuthToken() {
        return token;
    }

    @Override
    public void authTokenUpdated(AuthToken token) {
        this.token = token;
        Configuration editedConfig = editConfiguration();
        editedConfig.put(HydrawiseBindingConstants.CONFIG_REFRESHTOKEN, token.refreshToken);
        updateConfiguration(editedConfig);
    }
}
