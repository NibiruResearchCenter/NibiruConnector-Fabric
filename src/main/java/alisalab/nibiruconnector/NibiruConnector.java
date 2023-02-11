package alisalab.nibiruconnector;

import alisalab.nibiruconnector.commands.WhitelistCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NibiruConnector implements ModInitializer {

    public static String MOD_ID = "nibiru-connector";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello from NibiruConnector!");
        initCommands();
    }

    private void initCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var nibiruNode = CommandManager
                    .literal("nibiru")
                    .requires(Permissions.require("nibiru-connector.command", 4))
                    .build();

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

            dispatcher.getRoot().addChild(nibiruNode);

            nibiruNode.addChild(whitelistNode);

            whitelistNode.addChild(whitelistAddNode);
            whitelistNode.addChild(whitelistRemoveNode);
            whitelistNode.addChild(whitelistListNode);
        });
    }
}
