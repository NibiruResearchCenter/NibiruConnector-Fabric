package moe.alisalab.nibiruconnector.commands;

import com.alibaba.fastjson2.JSON;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import moe.alisalab.nibiruconnector.models.GetGroupResponse;
import moe.alisalab.nibiruconnector.utils.LuckPermsApi;
import net.luckperms.api.model.group.Group;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static moe.alisalab.nibiruconnector.utils.CommandUtils.isFromConsole;

public final class FetchCommand {
    public static int getAllGroups(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        var isConsole = isFromConsole(ctx);

        var groups = LuckPermsApi.getAllGroups()
                .stream()
                .map(Group::getName)
                .toList();


        if (isConsole) {
            var response = new GetGroupResponse(groups);
            var responseJson = JSON.toJSONString(response);
            source.sendFeedback(Text.literal(responseJson), true);
        }
        else {
            var groupString = String.join(", ", groups);
            source.sendFeedback(Text.literal(String.format("Groups: %s", groupString)), false);
        }

        return Command.SINGLE_SUCCESS;
    }
}
