package moe.alisalab.nibiruconnector.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class, (listeners) -> (player, server, clientData) -> {
        for (PlayerJoinCallback listener : listeners) {
            listener.joinServer(player, server, clientData);
        }
    });

    void joinServer(ServerPlayerEntity player, MinecraftServer server, ConnectedClientData clientData);
}
