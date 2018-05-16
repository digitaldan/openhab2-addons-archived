package org.openhab.binding.hydrawise.handler;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.hydrawise.config.HydrawiseRelayConfiguration;

public interface HydrawiseRelayControl {
    public void runRelay(@NonNull HydrawiseRelayConfiguration config);

    public void runRelay(int duration, @NonNull HydrawiseRelayConfiguration config);

    public void stopRelay(@NonNull HydrawiseRelayConfiguration config);
}
