package com.github.Soulphur0.config.command;

import com.github.Soulphur0.config.cloudlayer.CloudLayerAttributes;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class CloudLayerAttributeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        CloudLayerAttributes cloudLayerAttribute = context.getArgument("layerAttribute", CloudLayerAttributes.class);

        for(CloudLayerAttributes attribute : CloudLayerAttributes.values()){
            builder.suggest(attribute.toString());
        }

        return builder.buildFuture();
    }
}
