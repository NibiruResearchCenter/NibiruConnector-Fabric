package alisalab.nibiruconnector.commands;

import alisalab.nibiruconnector.NibiruConnector;
import alisalab.nibiruconnector.utils.LuckPermsApi;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import alisalab.nibiruconnector.utils.PlayerInfoUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static net.minecraft.server.dedicated.command.WhitelistCommand.executeAdd;
import static net.minecraft.server.dedicated.command.WhitelistCommand.executeRemove;

public final class WhitelistCommand {
    public static int addPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();
        var player = StringArgumentType.getString(ctx, "player");
        var group = StringArgumentType.getString(ctx, "group");

        if (!LuckPermsApi.isGroupExist(group)) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Group %s does not exist.", group))).create();
        }
        var node = InheritanceNode.builder(LuckPermsApi.getGroup(group)).build();
        NibiruConnector.LOGGER.info(String.format("[NBR|DBG] Group inheritance node: %s", node.getKey()));

        var profile = PlayerInfoUtils.getGameProfile(player);

        var isWhitelisted = source.getServer().getPlayerManager().isWhitelisted(profile);
        if (isWhitelisted) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Player %s is already in the whitelist.", player))).create();
        }

        var lpUser = (User) null;
        try {
            lpUser = LuckPermsApi.getUser(profile.getId()).get();
        } catch (InterruptedException e) {
            throw new SimpleCommandExceptionType(Text.literal("Luckperm user load has been interrupted.")).create();
        } catch (ExecutionException e) {
            throw new SimpleCommandExceptionType(Text.literal("An exception has been threw in luckperm user load process.")).create();
        }
        if (lpUser == null) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Luckperm could not find user metadata of username %s.", player))).create();
        }

        executeAdd(source, Collections.singletonList(profile));

        var nodes = lpUser.getNodes();
        NibiruConnector.LOGGER.info("[NBR|DBG] Whitelist ADD - LP USER - " + lpUser.getUsername());
        for (var n : nodes) {
            NibiruConnector.LOGGER.info("[NBR|DBG] Whitelist ADD - LP USER NODES - " + n.getKey());
        }

        if (!lpUser.getNodes().contains(node)) {
            var result = lpUser.data().add(node);
            NibiruConnector.LOGGER.info(String.format("[NBR|INF] %s add node %s result: %s.", player, node.getKey(), result.wasSuccessful() ? "OK" : "FAIL"));
            LuckPermsApi.saveUser(lpUser);
            NibiruConnector.LOGGER.info(String.format("[NBR|INF] %s has the primary group set to %s.", player, group));
        }
        else {
            NibiruConnector.LOGGER.info(String.format("[NBR|INF] %s already in the group %s.", player, group));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int removePlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();
        var player = StringArgumentType.getString(ctx, "player");

        var profile = PlayerInfoUtils.getGameProfile(player);

        var isWhitelisted = source.getServer().getPlayerManager().isWhitelisted(profile);
        if (!isWhitelisted) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Player %s is not in the whitelist.", player))).create();
        }

        executeRemove(source, Collections.singletonList(profile));

        return Command.SINGLE_SUCCESS;
    }

    public static int listPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();

        var whitelistedNames = source.getServer().getPlayerManager().getWhitelistedNames();

        var lpUsers = (Set<UUID>)new HashSet<UUID>();
        try {
            lpUsers = LuckPermsApi.getAllUsers().get();
        } catch (InterruptedException e) {
            throw new SimpleCommandExceptionType(Text.literal("Luckperm user query has been interrupted.")).create();
        } catch (ExecutionException e) {
            throw new SimpleCommandExceptionType(Text.literal("An exception has been threw in luckperm user query process.")).create();
        }

        var playerList = new HashMap<String, HashSet<String>>();

        for (var name: whitelistedNames) {
            var profile = PlayerInfoUtils.getGameProfile(name);
            var lpContains = lpUsers.contains(profile.getId());

            var group = "unknown";
            if (lpContains) {
                try {
                    group = LuckPermsApi.getUserGroup(profile.getId());
                } catch (InterruptedException e) {
                    throw new SimpleCommandExceptionType(Text.literal("Luckperm user query has been interrupted.")).create();
                } catch (ExecutionException e) {
                    throw new SimpleCommandExceptionType(Text.literal("An exception has been threw in luckperm user query process.")).create();
                }
            }

            if (!playerList.containsKey(group)) {
                playerList.put(group, new HashSet<>());
            }

            playerList.get(group).add(profile.getName());
        }

        var sb = new StringBuilder();
        var groups = playerList.keySet();
        for (var g: groups) {
            sb.append(String.format("§6[%s]:§r", g));
            for (var p: playerList.get(g)) {
                sb.append(p);
                sb.append(';');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('|');
        }
        sb.deleteCharAt(sb.length() - 1);

        var msg = sb.toString();
        source.sendMessage(Text.literal(msg));

        return Command.SINGLE_SUCCESS;
    }
}