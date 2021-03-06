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
import java.util.function.Supplier;

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
     * Constructs a {@link UniversalSuggestionProvider} from a {@link Function}<{@link CommandContext<T>}, {@link Iterable<String>}<{@link String}>>.
     *
     * @param suggestions The {@link Iterable<String>}<{@link String}> to use as suggestions.
     */
    public UniversalSuggestionProvider(final @NotNull Function<@NotNull CommandContext<T>, @NotNull Iterable<@NotNull String>> suggestions) {
        this.suggestions = suggestions;
    }

    /**
     * Constructs a {@link UniversalSuggestionProvider} from a {@link Supplier}<{@link Iterable<String>}<{@link String}>>.
     *
     * @param suggestions The {@link Iterable<String>}<{@link String}> to use as suggestions.
     */
    public UniversalSuggestionProvider(final @NotNull Supplier<@NotNull Iterable<@NotNull String>> suggestions) { this(ignored -> suggestions.get()); }

    /**
     * Builds and returns a {@link CompletableFuture<Suggestions>} containing all the {@link Suggestion Suggestions}.
     *
     * @param ctx The {@link CommandContext} to apply to {@link Suggestion Suggestions}.
     * @param builder The {@link SuggestionsBuilder} to add {@link Suggestion Suggestions} to.
     * @return A built {@link CompletableFuture<Suggestions>} containing the {@link Suggestion Suggestions}.
     */
    @Override public @NotNull CompletableFuture<@NotNull Suggestions> getSuggestions(final CommandContext<T> ctx, final @NotNull SuggestionsBuilder builder) {
        this.suggestions.apply(ctx).forEach(builder::suggest);
        return builder.buildFuture();
    }

    public @NotNull Function<CommandContext<T>, Iterable<String>> suggestions() { return suggestions; }

    @Override public int hashCode() { return Objects.hash(suggestions); }

    // TODO: Printing the suggestions variable isn't going to show all that much...
    @Override public String toString() { return "UniversalSuggestionProvider[suggestions=" + suggestions + ']'; } //NON-NLS
}