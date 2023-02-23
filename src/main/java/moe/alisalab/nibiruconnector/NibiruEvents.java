package moe.alisalab.nibiruconnector;

import moe.alisalab.nibiruconnector.events.PlayerJoinCallback;
import moe.alisalab.nibiruconnector.handlers.PlayerJoinEventHandler;

public final class NibiruEvents {

    public static void init() {
        PlayerJoinCallback.EVENT.register((player, server) -> {
            PlayerJoinEventHandler.sendPlayerMessageAfterJoin(player);
        });
    }

}
