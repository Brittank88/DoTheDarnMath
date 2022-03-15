package com.brittank88.dtdm.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;


import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Provides a simple way to generate a list of {@link com.mojang.brigadier.suggestion.Suggestion Suggestions}
 * for an {@link com.mojang.brigadier.builder.RequiredArgumentBuilder argument}, from a {@link Iterable<String>}<{@link String}>.
 *
 * @author Brittank88
 * @param <T> The type of the command source.
 */
public record UniversalSuggestionProvider<T>(@NotNull Function<CommandContext<T>, Iterable<String>> suggestions) implements SuggestionProvider<T> {

    /**
     * Constructor for a {@link UniversalSuggestionProvider}.
     *
     * @param suggestions The {@link Iterable<String>}<{@link String}> to use as suggestions.
     */
    public UniversalSuggestionProvider(@NotNull Function<@NotNull CommandContext<T>, @NotNull Iterable<@NotNull String>> suggestions) { this.suggestions = suggestions; }

    /**
     * Builds and returns a {@link CompletableFuture<Suggestions>} containing all the {@link com.mojang.brigadier.suggestion.Suggestion Suggestions}.
     *
     * @param ctx The {@link CommandContext} to apply to {@link com.mojang.brigadier.suggestion.Suggestion Suggestions}.
     * @param builder The {@link SuggestionsBuilder} to add {@link com.mojang.brigadier.suggestion.Suggestion Suggestions} to.
     * @return A built {@link CompletableFuture<Suggestions>} containing the {@link com.mojang.brigadier.suggestion.Suggestion Suggestions}.
     */
    @Override public @NotNull CompletableFuture<@NotNull Suggestions> getSuggestions(CommandContext<T> ctx, SuggestionsBuilder builder) {
        this.suggestions.apply(ctx).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
