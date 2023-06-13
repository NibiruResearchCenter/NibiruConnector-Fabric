package moe.alisalab.nibiruconnector.commands.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.alisalab.nibiruconnector.config.WarpPointsConfigManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class WarpPointNameSuggestion implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var warpPoints = WarpPointsConfigManager.getInstance().getWarpPointNames();

        for (var warpPoint : warpPoints) {
            builder.suggest(warpPoint);
        }

        return builder.buildFuture();
    }
}
