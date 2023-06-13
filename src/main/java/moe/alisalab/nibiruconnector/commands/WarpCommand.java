package moe.alisalab.nibiruconnector.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import moe.alisalab.nibiruconnector.NibiruLogger;
import moe.alisalab.nibiruconnector.config.WarpPointsConfigManager;
import moe.alisalab.nibiruconnector.exceptions.LuckpermApiException;
import moe.alisalab.nibiruconnector.utils.LuckPermsApi;
import moe.alisalab.nibiruconnector.utils.WarpCoolDownApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class WarpCommand {

    public static WarpCoolDownApi coolDownApi = new WarpCoolDownApi(600);

    public static int warpTo(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var isPlayer = ctx.getSource().isExecutedByPlayer();
        if (!isPlayer) {
            throw new SimpleCommandExceptionType(Text.literal("Warp command can only be executed by player.")).create();
        }

        var player = ctx.getSource().getPlayer();
        var pointName = StringArgumentType.getString(ctx, "name");

        var warpPoint = WarpPointsConfigManager.getInstance().getWarpPoint(pointName);
        if (warpPoint == null) {
            throw new SimpleCommandExceptionType(Text.literal("Warp point not found.")).create();
        }

        assert player != null;
        var world = getServerWorld(warpPoint.world, player.getServer());

        if (world == null) {
            throw new SimpleCommandExceptionType(Text.literal("Warp point world not found.")).create();
        }

        var bypassCoolDown = false;
        try {
            bypassCoolDown = LuckPermsApi.isHasNode(player.getUuid(), "nibiru-connector.settings.warp.no-cool-down");
        }
        catch (LuckpermApiException e) {
            NibiruLogger.warn("LuckPermsApi error: " + e.getMessage());
        }

        if (!bypassCoolDown) {
            if (coolDownApi.isCoolDown(player.getUuid())) {
                var coolDownTime = coolDownApi.getCoolDownTime(player.getUuid());
                throw new SimpleCommandExceptionType(Text.literal(String.format("You can warp after %s seconds.", coolDownTime))).create();
            }
        }

        player.teleport(world, warpPoint.x, warpPoint.y, warpPoint.z, warpPoint.yaw, warpPoint.pitch);
        if (!bypassCoolDown) {
            coolDownApi.setCoolDown(player.getUuid());
        }

        ctx.getSource().sendFeedback(() -> Text.of("Warping... Destination: §3" + pointName), false);

        return Command.SINGLE_SUCCESS;
    }

    public static int warpList(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var warpPoints = WarpPointsConfigManager.getInstance().getWarpPoints();

        if (warpPoints.size() == 0) {
            throw new SimpleCommandExceptionType(Text.literal("No warp points!")).create();
        }

        var message = new StringBuilder();
        for (var warpPoint : warpPoints) {
            message
                    .append("§3")
                    .append(warpPoint.name)
                    .append("§r in ")
                    .append(getWorldName(warpPoint.world))
                    .append("§r at ")
                    .append(String.format("[%.1f, %.1f, %.1f]§r", warpPoint.x, warpPoint.y, warpPoint.z))
                    .append('\n');
        }

        ctx.getSource().sendFeedback(() -> Text.of(message.toString()), false);

        return Command.SINGLE_SUCCESS;
    }

    public static int warpAdd(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var isPlayer = ctx.getSource().isExecutedByPlayer();
        if (!isPlayer) {
            throw new SimpleCommandExceptionType(Text.literal("Warp command can only be executed by player.")).create();
        }

        var player = ctx.getSource().getPlayer();
        var pointName = StringArgumentType.getString(ctx, "name");

        var warpPoint = WarpPointsConfigManager.getInstance().getWarpPoint(pointName);
        if (warpPoint != null) {
            throw new SimpleCommandExceptionType(Text.literal("Warp point already exists.")).create();
        }

        assert player != null;
        var world = player.getServerWorld().getRegistryKey().getValue().toString();
        var x = player.getX();
        var y = player.getY();
        var z = player.getZ();
        var yaw = player.getYaw();
        var pitch = player.getPitch();

        var result = WarpPointsConfigManager.getInstance().addWarpPoint(pointName, world, x, y, z, yaw, pitch);
        if (!result) {
            throw new SimpleCommandExceptionType(Text.literal("Warp point add failed.")).create();
        }

        ctx.getSource().sendFeedback(() -> Text.of("Warp point added."), false);

        return Command.SINGLE_SUCCESS;
    }

    public static int warpRemove(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var pointName = StringArgumentType.getString(ctx, "name");

        var warpPoint = WarpPointsConfigManager.getInstance().getWarpPoint(pointName);
        if (warpPoint == null) {
            throw new SimpleCommandExceptionType(Text.literal("Warp point not found.")).create();
        }

        var result = WarpPointsConfigManager.getInstance().removeWarpPoint(pointName);
        if (!result) {
            throw new SimpleCommandExceptionType(Text.literal("Warp point remove failed.")).create();
        }

        ctx.getSource().sendFeedback(() -> Text.of("Warp point removed."), false);

        return Command.SINGLE_SUCCESS;
    }

    private static String getWorldName(String world) {
        if (world.equals(ServerWorld.OVERWORLD.getValue().toString())) {
            return "§a Overworld";
        } else if (world.equals(ServerWorld.NETHER.getValue().toString())) {
            return "§c The Nether";
        } else if (world.equals(ServerWorld.END.getValue().toString())) {
            return "§d The End";
        } else {
            return "§7";
        }
    }

    private static ServerWorld getServerWorld(String world, MinecraftServer server) {
        net.minecraft.registry.RegistryKey<net.minecraft.world.World> key;
        if (world.equals(ServerWorld.OVERWORLD.getValue().toString())) {
            key = ServerWorld.OVERWORLD;
        } else if (world.equals(ServerWorld.NETHER.getValue().toString())) {
            key = ServerWorld.NETHER;
        } else if (world.equals(ServerWorld.END.getValue().toString())) {
            key = ServerWorld.END;
        } else {
            return null;
        }

        return server.getWorld(key);
    }
}
