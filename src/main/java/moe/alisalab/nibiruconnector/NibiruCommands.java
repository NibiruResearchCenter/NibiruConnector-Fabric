package moe.alisalab.nibiruconnector;

import moe.alisalab.nibiruconnector.commands.FetchCommand;
import moe.alisalab.nibiruconnector.commands.WarpCommand;
import moe.alisalab.nibiruconnector.commands.WhitelistCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import moe.alisalab.nibiruconnector.commands.suggestion.LuckpermsGroupSuggestion;
import moe.alisalab.nibiruconnector.commands.suggestion.WarpPointNameSuggestion;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public final class NibiruCommands {

    public static void init() {
        var nibiruNode = CommandManager
                .literal("nibiru")
                .build();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.getRoot().addChild(nibiruNode));

        addWhitelistCommand(nibiruNode);
        addFetchCommand(nibiruNode);
        addWarpCommand(nibiruNode);
    }

    private static void addWhitelistCommand(LiteralCommandNode<ServerCommandSource> nibiruNode) {
        var whitelistNode = CommandManager
                .literal("whitelist")
                .requires(Permissions.require("nibiru-connector.command.whitelist", 4))
                .build();

        var whitelistAddNode = CommandManager
                .literal("add")
                .requires(Permissions.require("nibiru-connector.command.whitelist.add", 4))
                .then(
                        CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                .then(
                                        CommandManager.argument("group", StringArgumentType.word())
                                                .suggests(new LuckpermsGroupSuggestion())
                                                .executes(WhitelistCommand::addPlayer)
                                )
                )
                .build();

        var whitelistRemoveNode = CommandManager
                .literal("remove")
                .requires(Permissions.require("nibiru-connector.command.whitelist.remove", 4))
                .then(
                        CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                .executes(WhitelistCommand::removePlayer)
                )
                .build();

        var whitelistListNode = CommandManager
                .literal("list")
                .requires(Permissions.require("nibiru-connector.command.whitelist.list", 4))
                .executes(WhitelistCommand::listPlayer)
                .build();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            nibiruNode.addChild(whitelistNode);

            whitelistNode.addChild(whitelistAddNode);
            whitelistNode.addChild(whitelistRemoveNode);
            whitelistNode.addChild(whitelistListNode);
        });
    }

    private static void addFetchCommand(LiteralCommandNode<ServerCommandSource> nibiruNode) {
        var fetchNode = CommandManager
                .literal("fetch")
                .requires(Permissions.require("nibiru-connector.command.fetch", 4))
                .build();

        var fetchGroupNode = CommandManager
                .literal("groups")
                .requires(Permissions.require("nibiru-connector.command.fetch.group", 4))
                .executes(FetchCommand::getAllGroups)
                .build();

        var fetchWhitelistedPlayerNode = CommandManager
                .literal("whitelisted")
                .requires(Permissions.require("nibiru-connector.command.fetch.whitelisted", 4))
                .executes(FetchCommand::getAllWhitelistedPlayers)
                .build();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            nibiruNode.addChild(fetchNode);

            fetchNode.addChild(fetchGroupNode);
            fetchNode.addChild(fetchWhitelistedPlayerNode);
        });
    }

    private static void addWarpCommand(LiteralCommandNode<ServerCommandSource> nibiruNode) {
        var warpNode = CommandManager
                .literal("warp")
                .requires(Permissions.require("nibiru-connector.command.warp", 0))
                .build();

        var warpToNode = CommandManager
                .literal("to")
                .requires(Permissions.require("nibiru-connector.command.warp.to", 2))
                .then(
                        CommandManager.argument("name", StringArgumentType.word())
                                .suggests(new WarpPointNameSuggestion())
                                .executes(WarpCommand::warpTo)
                )
                .build();

        var warpAddNode = CommandManager
                .literal("add")
                .requires(Permissions.require("nibiru-connector.command.warp.add", 4))
                .then(
                        CommandManager.argument("name", StringArgumentType.word())
                                .executes(WarpCommand::warpAdd)
                )
                .build();

        var warpRemoveNode = CommandManager
                .literal("remove")
                .requires(Permissions.require("nibiru-connector.command.warp.remove", 4))
                .then(
                        CommandManager.argument("name", StringArgumentType.word())
                                .executes(WarpCommand::warpRemove)
                )
                .build();

        var warpListNode = CommandManager
                .literal("list")
                .requires(Permissions.require("nibiru-connector.command.warp.list", 2))
                .executes(WarpCommand::warpList)
                .build();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            nibiruNode.addChild(warpNode);

            warpNode.addChild(warpToNode);
            warpNode.addChild(warpAddNode);
            warpNode.addChild(warpRemoveNode);
            warpNode.addChild(warpListNode);
        });
    }
}
