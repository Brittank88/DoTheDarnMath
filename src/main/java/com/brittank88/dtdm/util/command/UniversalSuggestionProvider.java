package com.brittank88.dtdm.util.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Provides a simple way to generate a list of {@link com.mojang.brigadier.suggestion.Suggestion Suggestions}
 * for an {@link com.mojang.brigadier.builder.RequiredArgumentBuilder argument}, from a {@link Iterable<String>}<{@link String}>.
 *
 * @see SuggestionProvider
 * @author Brittank88
 * @param <T> The type of the command source.
 */
public record UniversalSuggestionProvider<T>(@NonNull Function<CommandContext<T>, Iterable<String>> suggestions) implements SuggestionProvider<T> {

    /**
     * Constructor for a {@link UniversalSuggestionProvider}.
     *
     * @param suggestions The {@link Iterable<String>}<{@link String}> to use as suggestions.
     */
    public UniversalSuggestionProvider(@NonNull Function<@NonNull CommandContext<T>, @NonNull Iterable<@NonNull String>> suggestions) { this.suggestions = suggestions; }

    /**
     * Builds and returns a {@link CompletableFuture<Suggestions>} containing all the {@link com.mojang.brigadier.suggestion.Suggestion Suggestions}.
     *
     * @param context The {@link CommandContext} to apply to {@link com.mojang.brigadier.suggestion.Suggestion Suggestions}.
     * @param builder The {@link SuggestionsBuilder} to add {@link com.mojang.brigadier.suggestion.Suggestion Suggestions} to.
     * @return A built {@link CompletableFuture<Suggestions>} containing the {@link com.mojang.brigadier.suggestion.Suggestion Suggestions}.
     */
    @Override public @NonNull CompletableFuture<@NonNull Suggestions> getSuggestions(CommandContext<T> context, @NotNull SuggestionsBuilder builder) {
        this.suggestions.apply(context).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
