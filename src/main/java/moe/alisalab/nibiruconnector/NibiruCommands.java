package moe.alisalab.nibiruconnector;

import moe.alisalab.nibiruconnector.commands.WhitelistCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import moe.alisalab.nibiruconnector.commands.suggestion.LuckpermsGroupSuggestion;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public final class NibiruCommands {

    public static void init() {
        var nibiruNode = CommandManager
                .literal("nibiru")
                .requires(Permissions.require("nibiru-connector.command", 4))
                .build();
        var nibiruConsoleNode = CommandManager
                .literal("nibiruc")
                .redirect(nibiruNode)
                .build();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.getRoot().addChild(nibiruNode);
            dispatcher.getRoot().addChild(nibiruConsoleNode);
        });

        addWhitelistCommand(nibiruNode);
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
                        CommandManager.argument("player", StringArgumentType.word())
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
                        CommandManager.argument("player", StringArgumentType.word())
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
}
