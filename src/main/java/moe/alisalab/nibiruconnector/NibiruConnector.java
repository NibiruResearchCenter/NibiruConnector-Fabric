package moe.alisalab.nibiruconnector;

import net.fabricmc.api.ModInitializer;

public class NibiruConnector implements ModInitializer {

    public static String MOD_ID = "nibiru-connector";

    @Override
    public void onInitialize() {
        NibiruLogger.info("Hello from Nibiru Connector!");
        NibiruCommands.init();
        NibiruEvents.init();
    }
}
