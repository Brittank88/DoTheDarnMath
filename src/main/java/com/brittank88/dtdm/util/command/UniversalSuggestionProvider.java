package com.brittank88.dtdm.util.command;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Provides a simple way to generate a list of {@link Suggestion Suggestions}
 * for an {@link RequiredArgumentBuilder argument}, from a {@link Iterable<String>}<{@link String}>.
 *
 * @see SuggestionProvider
 * @author Brittank88
 */
@SuppressWarnings("ClassCanBeRecord")
public final class UniversalSuggestionProvider<T> implements SuggestionProvider<T> {

    private final @NotNull Function<CommandContext<T>, Iterable<String>> suggestions;

    /**
     * Constructor for a {@link UniversalSuggestionProvider}.
     *
     * @param suggestions The {@link Iterable<String>}<{@link String}> to use as suggestions.
     */
    public UniversalSuggestionProvider(@NotNull Function<@NotNull CommandContext<T>, @NotNull Iterable<@NotNull String>> suggestions) {
        this.suggestions = suggestions;
    }

    /**
     * Builds and returns a {@link CompletableFuture<Suggestions>} containing all the {@link Suggestion Suggestions}.
     *
     * @param ctx The {@link CommandContext} to apply to {@link Suggestion Suggestions}.
     * @param builder The {@link SuggestionsBuilder} to add {@link Suggestion Suggestions} to.
     * @return A built {@link CompletableFuture<Suggestions>} containing the {@link Suggestion Suggestions}.
     */
    @Override
    public @NotNull CompletableFuture<@NotNull Suggestions> getSuggestions(CommandContext<T> ctx, @NotNull SuggestionsBuilder builder) {
        this.suggestions.apply(ctx).forEach(builder::suggest);
        return builder.buildFuture();
    }

    public @NotNull Function<CommandContext<T>, Iterable<String>> suggestions() { return suggestions; }

    @Override
    public int hashCode() { return Objects.hash(suggestions); }

    @Override
    public String toString() { return "UniversalSuggestionProvider[suggestions=" + suggestions + ']'; } //NON-NLS
}