package moe.alisalab.nibiruconnector.commands;

import com.github.quiltservertools.ledger.utility.PlayerResult;
import com.mojang.authlib.GameProfile;
import moe.alisalab.nibiruconnector.NibiruConnectKotlin;
import moe.alisalab.nibiruconnector.NibiruLogger;
import moe.alisalab.nibiruconnector.exceptions.LuckpermApiException;
import moe.alisalab.nibiruconnector.utils.LuckPermsApi;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import moe.alisalab.nibiruconnector.utils.WhitelistUtils;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;

public final class WhitelistCommand {
    public static int addPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();
        var profile = GameProfileArgumentType.getProfileArgument(ctx, "player").iterator().next();
        var player = profile.getName();
        var group = StringArgumentType.getString(ctx, "group");

        source.getServer().getPlayerManager().getPlayer("");

        if (group.equals("default")) {
            throw new SimpleCommandExceptionType(Text.literal("Group could not be default.")).create();
        }
        if (!LuckPermsApi.isGroupExist(group)) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Group %s does not exist.", group))).create();
        }
        var node = InheritanceNode.builder(LuckPermsApi.getGroup(group)).build();
        NibiruLogger.debug("WL-ADD Group inheritance node: %s", node.getKey());

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

        var whitelist = source.getServer().getPlayerManager().getWhitelist();
        if (!whitelist.isAllowed(profile)) {
            whitelist.add(new WhitelistEntry(profile));
        }

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

        source.sendFeedback(() -> Text.literal(String.format("[NBR] Player %s has been added to whitelist and has the node %s.", player, node.getKey())), true);

        return Command.SINGLE_SUCCESS;
    }

    public static int removePlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();
        var profile = GameProfileArgumentType.getProfileArgument(ctx, "player").iterator().next();
        var player = profile.getName();

        var isWhitelisted = source.getServer().getPlayerManager().isWhitelisted(profile);
        if (!isWhitelisted) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Player %s is not in the whitelist.", player))).create();
        }

        var whitelist = source.getServer().getPlayerManager().getWhitelist();
        if (whitelist.isAllowed(profile)) {
            whitelist.remove(profile);
        }
        source.getServer().kickNonWhitelistedPlayers(source);

        source.sendMessage(Text.literal(String.format("[NBR] Player %s has been removed from whitelist.", player)));

        return Command.SINGLE_SUCCESS;
    }

    public static int listPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();

        var lpUsers = (Set<UUID>)new HashSet<UUID>();
        try {
            lpUsers = LuckPermsApi.getAllUsers();
        } catch (LuckpermApiException e) {
            throw new SimpleCommandExceptionType(Text.literal(e.reason)).create();
        }
        NibiruLogger.debug("WL-LIST Load all lp users, total count: %s", Integer.toString(lpUsers.size()));

        var playerList = new HashMap<String, HashSet<GameProfile>>();

        var whitelistFile = source.getServer().getPlayerManager().getWhitelist().getFile();
        var profiles = WhitelistUtils.getWhitelistProfiles(whitelistFile);

        var playerQueryFuture = NibiruConnectKotlin.getLedgerQueryApi().queryPlayers(new HashSet<>(profiles));
        var playerQueryResult = (List<PlayerResult>) null;
        try {
            playerQueryResult = playerQueryFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new SimpleCommandExceptionType(Text.literal(String.format("Failed to query Ledger Database. Message: %s", e.getMessage()))).create();
        }

        for (var profile: profiles) {
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

            playerList.get(group).add(profile);
        }

        var sb = new StringBuilder();
        var groups = playerList.keySet();
        for (var g: groups) {
            sb.append(String.format("§6[%s]:§r", g));
            for (var p: playerList.get(g)) {
                var playerResult = playerQueryResult
                        .stream()
                        .filter(x -> x.getUuid().equals(p.getId()))
                        .findFirst();
                var days = (long) -1;
                if (playerResult.isPresent()) {
                    days = Math.abs(ChronoUnit.DAYS.between(Instant.now(), playerResult.get().getLastJoin()));
                }
                var colorCode = "a";
                if (days >= 30) {
                    colorCode = "c";
                }
                else if (days >= 7) {
                    colorCode = "b";
                }
                else if (days < 0) {
                    colorCode = "d";
                }
                sb.append(String.format("%s§%s(%s)§r", p.getName(), colorCode, days));
                sb.append(';');
                sb.append(' ');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(' ');
        }

        sb.deleteCharAt(sb.length() - 1);

        var msg = sb.toString();
        source.sendFeedback(() -> Text.literal(msg), false);

        return Command.SINGLE_SUCCESS;
    }
}