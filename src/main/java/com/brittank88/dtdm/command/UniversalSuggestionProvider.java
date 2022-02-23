package com.brittank88.dtdm.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class UniversalSuggestionProvider<T> implements SuggestionProvider<T> {

    private final Function<CommandContext<T>, Iterable<String>> suggestions;

    public UniversalSuggestionProvider() { this.suggestions = ignored -> new ArrayList<>(); }
    public UniversalSuggestionProvider(Function<CommandContext<T>, Iterable<String>> suggestions) { this.suggestions = suggestions; }

    public static SuggestionProvider<?> fromSingle(String suggestion) { return new UniversalSuggestionProvider<>(ignored -> Collections.singletonList(suggestion)); }
    public static SuggestionProvider<?> fromIterable(Iterable<String> iterable) { return new UniversalSuggestionProvider<>(ignored -> iterable); }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<T> context, SuggestionsBuilder builder) {
        this.suggestions.apply(context).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
