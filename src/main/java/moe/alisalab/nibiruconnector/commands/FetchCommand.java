package moe.alisalab.nibiruconnector.commands;

import com.alibaba.fastjson2.JSON;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import moe.alisalab.nibiruconnector.models.GroupedStringResponse;
import moe.alisalab.nibiruconnector.utils.LuckPermsApi;
import net.luckperms.api.model.group.Group;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static moe.alisalab.nibiruconnector.utils.CommandUtils.isFromConsole;

public final class FetchCommand {
    public static int getAllGroups(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        var isConsole = isFromConsole(ctx);

        var groups = LuckPermsApi.getAllGroups()
                .stream()
                .map(Group::getName)
                .toList();


        return response(source, isConsole, groups, "Groups");
    }

    public static int getAllWhitelistedPlayers(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        var isConsole = isFromConsole(ctx);

        var players = List.of(source.getServer().getPlayerManager().getWhitelistedNames());

        return response(source, isConsole, players, "Whitelisted players");
    }

    private static int response(ServerCommandSource source, boolean isConsole, List<String> groups, String title) {
        if (isConsole) {
            var response = new GroupedStringResponse(groups);
            var responseJson = JSON.toJSONString(response);
            source.sendFeedback(() -> Text.literal(responseJson), false);
        }
        else {
            var groupedString = String.join(", ", groups);
            source.sendFeedback(() -> Text.literal(String.format("%s: %s", title, groupedString)), false);
        }

        return Command.SINGLE_SUCCESS;
    }
}
