package moe.alisalab.nibiruconnector.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import moe.alisalab.nibiruconnector.utils.LuckPermsApi;
import net.luckperms.api.model.group.Group;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

public final class FetchCommand {
    public static int getAllGroups(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();

        var groups = LuckPermsApi.getAllGroups()
                .stream()
                .map(Group::getName)
                .toList();


        return response(source, groups, "Groups");
    }

    public static int getAllWhitelistedPlayers(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();

        var players = List.of(source.getServer().getPlayerManager().getWhitelistedNames());

        return response(source, players, "Whitelisted players");
    }

    private static int response(ServerCommandSource source, List<String> groups, String title) {
        var groupedString = String.join(", ", groups);
        source.sendFeedback(() -> Text.literal(String.format("%s: %s", title, groupedString)), false);

        return Command.SINGLE_SUCCESS;
    }
}
