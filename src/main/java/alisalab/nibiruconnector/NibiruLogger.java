package alisalab.nibiruconnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NibiruLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(NibiruConnector.MOD_ID);
    public static void debug(String m, Object... objects) {
        LOGGER.debug(String.format("[NBR|DBG] " + m, objects));
    }
    public static void info(String m, Object... objects) {
        LOGGER.info(String.format("[NBR|INF] " + m, objects));
    }
    public static void warn(String m, Object... objects) {
        LOGGER.warn(String.format("[NBR|WRN] " + m, objects));
    }
}
