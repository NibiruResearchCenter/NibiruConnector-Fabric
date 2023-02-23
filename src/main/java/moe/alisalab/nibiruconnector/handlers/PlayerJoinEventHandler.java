package moe.alisalab.nibiruconnector.handlers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class PlayerJoinEventHandler {

    public static void sendPlayerMessageAfterJoin(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("Hi!"));
    }

}
