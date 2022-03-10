package com.brittank88.dtdm.util.suggestion;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandException;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class SuggestionSupplier {

    @FunctionalInterface
    public interface ParserFunction<T, U, R> {
        @NotNull R apply(T t, U u) throws CommandException;
    }

    /**
     * <p>The {@link BiFunction} that will be used to parse the contents of the {@link TextFieldWidget chatField} and generate a {@link List} of {@link Suggestion suggestions}.</p>
     * <p>Note that the supplied {@link StringReader}'s {@link Integer cursor} is at the position of the {@link Integer cursor} in the {@link TextFieldWidget chatField}.</p>
     */
    private final ParserFunction<TextFieldWidget, StringReader, List<Suggestion>> parser;

    private final List<Suggestion> suggestions = new ArrayList<>();
    private final List<OrderedText> messages = new ArrayList<>();

    public SuggestionSupplier(ParserFunction<TextFieldWidget, StringReader, List<Suggestion>> parser) { this.parser = parser; }

    public @NotNull SuggestionSupplier parse(@NotNull TextFieldWidget chatField, StringReader reader) {

        // Clear all suggestions.
        this.suggestions.clear();

        // Attempt to add all suggestions from the parser to our internal list.
        // If any CommandException is raised, catch it and add it to our messages list.
        try { this.suggestions.addAll(this.parser.apply(chatField, SuggestionUtils.getReaderAtCursor(chatField))); }
        catch (CommandException e) { this.messages.add(e.getTextMessage().asOrderedText()); }

        // Return this instance, for chained calls.
        return this;
    }
    public @NotNull List<Suggestion> getSuggestions() { return this.suggestions; }
    public @NotNull List<OrderedText> getMessages() { return this.messages; }
}
