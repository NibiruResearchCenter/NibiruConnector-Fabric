package moe.alisalab.nibiruconnector.commands;

import moe.alisalab.nibiruconnector.NibiruLogger;
import moe.alisalab.nibiruconnector.exceptions.LuckpermApiException;
import moe.alisalab.nibiruconnector.utils.LuckPermsApi;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import moe.alisalab.nibiruconnector.utils.PlayerInfoUtils;

import java.util.*;

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
        NibiruLogger.debug("WL-ADD Group inheritance node: %s", node.getKey());

        var profile = PlayerInfoUtils.getGameProfile(player);
        NibiruLogger.debug("WL-ADD Get player uuid %s(%s)", profile.getName(), profile.getId().toString());

        var isWhitelisted = source.getServer().getPlayerManager().isWhitelisted(profile);
        if (isWhitelisted) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Player %s is already in the whitelist.", player))).create();
        }

        var lpUser = (User) null;
        try {
            lpUser = LuckPermsApi.getUser(profile.getId());
        } catch (LuckpermApiException e) {
            throw new SimpleCommandExceptionType(Text.literal(e.reason)).create();
        }
        if (lpUser == null) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Luckperm could not find user metadata of player %s.", player))).create();
        }

        NibiruLogger.debug("WL-ADD Get Luckperms user with name %s", lpUser.getUsername());

        executeAdd(source, Collections.singletonList(profile));

        if (!lpUser.getNodes().contains(node)) {
            var result = lpUser.data().add(node);
            if (!result.wasSuccessful()) {
                NibiruLogger.warn("Failed to add node %s to player %s", node.getKey(), player);
                throw new SimpleCommandExceptionType(Text.literal(result.name())).create();
            }
            LuckPermsApi.saveUser(lpUser);
            NibiruLogger.info("Add %s node to player %s.", node.getKey(), player);
        }
        else {
            NibiruLogger.debug("WL-ADD Player %s already has the node %s", player, node.getKey());
        }

        source.sendMessage(Text.literal(String.format("[NBR] Player %s has been added to whitelist and has the node %s.", player, node.getKey())));
        return Command.SINGLE_SUCCESS;
    }

    public static int removePlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();
        var player = StringArgumentType.getString(ctx, "player");

        var profile = PlayerInfoUtils.getGameProfile(player);
        NibiruLogger.debug("WL-REMOVE Get player uuid %s(%s)", profile.getName(), profile.getId().toString());

        var isWhitelisted = source.getServer().getPlayerManager().isWhitelisted(profile);
        if (!isWhitelisted) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Player %s is not in the whitelist.", player))).create();
        }

        executeRemove(source, Collections.singletonList(profile));

        source.sendMessage(Text.literal(String.format("[NBR] Player %s has been removed from whitelist.", player)));
        return Command.SINGLE_SUCCESS;
    }

    public static int listPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();

        var whitelistedNames = source.getServer().getPlayerManager().getWhitelistedNames();

        var lpUsers = (Set<UUID>)new HashSet<UUID>();
        try {
            lpUsers = LuckPermsApi.getAllUsers();
        } catch (LuckpermApiException e) {
            throw new SimpleCommandExceptionType(Text.literal(e.reason)).create();
        }
        NibiruLogger.debug("WL-LIST Load all lp users, total count: %s", Integer.toString(lpUsers.size()));

        var playerList = new HashMap<String, HashSet<String>>();

        for (var name: whitelistedNames) {
            var profile = PlayerInfoUtils.getGameProfile(name);
            var lpContains = lpUsers.contains(profile.getId());
            NibiruLogger.debug("WL-LIST Player %s(%s) in lp: %s", profile.getName(), profile.getId().toString(), lpContains ? "true" : "false");

            var group = "unknown";
            if (lpContains) {
                try {
                    group = LuckPermsApi.getUserGroup(profile.getId());
                    NibiruLogger.debug("WL-LIST Get user %s primary group %s", profile.getName(), group);
                } catch (LuckpermApiException e) {
                    throw new SimpleCommandExceptionType(Text.literal(e.reason)).create();
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