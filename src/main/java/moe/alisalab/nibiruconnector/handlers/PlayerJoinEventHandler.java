package moe.alisalab.nibiruconnector.handlers;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerJoinEventHandler {

    public static void sendPlayerMessageAfterJoin(ServerPlayerEntity player) {
        var message = Placeholders.parseText(
                TextParserUtils.formatText("> Welcome to <gr:#f959ff:#5be3e9><bold>Nibiru SMP</bold></gr>, %player:displayname%!"),
                PlaceholderContext.of(player));
        player.sendMessage(message);
    }
}
