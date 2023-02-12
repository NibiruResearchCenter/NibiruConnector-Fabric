package moe.alisalab.nibiruconnector.utils;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public final class CommandUtils {
    public static boolean isFromConsole(CommandContext<ServerCommandSource> ctx) {
        return ctx.getInput().split(" ")[0].equals("nibiruc");
    }
}
