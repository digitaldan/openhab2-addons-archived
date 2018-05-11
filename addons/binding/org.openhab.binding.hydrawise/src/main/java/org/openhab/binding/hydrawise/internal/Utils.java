package org.openhab.binding.hydrawise.internal;

import java.lang.reflect.Method;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.types.State;

public class Utils {

    public static State channelStateFromModel(Object model, Channel channel) {
        try {
            Method method = model.getClass().getMethod("get" + capitalize(channel.getUID().getIdWithoutGroup()));
            String s = method.invoke(model, null).toString();
            return toState(channel.getAcceptedItemType(), s);
        } catch (Exception e) {
            return null;
        }
    }

    private static String capitalize(String str) {
        String first = str.substring(0, 1).toUpperCase();
        String rest = str.substring(1);
        return first + rest;
    }

    private static State toState(String type, String value) throws NumberFormatException {
        if (type.equals("Number")) {
            return new DecimalType(value);
        } else if (type.equals("Switch")) {
            return Integer.parseInt(value) > 0 ? OnOffType.ON : OnOffType.OFF;
        } else {
            return StringType.valueOf(value);
        }
    }
}
