package moe.alisalab.nibiruconnector.commands.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.alisalab.nibiruconnector.utils.LuckPermsApi;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class LuckpermsGroupSuggestion implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        var groups = LuckPermsApi.getAllGroups();

        for (var g : groups) {
            builder.suggest(g.getName());
        }

        return builder.buildFuture();
    }
}
