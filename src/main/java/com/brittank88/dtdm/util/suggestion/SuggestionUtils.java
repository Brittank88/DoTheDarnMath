package com.brittank88.dtdm.util.suggestion;

import com.brittank88.dtdm.mixin.accessors.ChatScreenAccessors;
import com.brittank88.dtdm.mixin.accessors_invokers.CommandSuggestorAccessorsInvokers;
import com.brittank88.dtdm.mixin.accessors_invokers.SuggestionWindowAccessorsInvokers;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A collection of utility methods for working with suggestions and classes that handle suggestion.
 *
 * @author brittank88
 */
public abstract class SuggestionUtils {

    /**
     * Creates a new {@link CommandSuggestor.SuggestionWindow SuggestionWindow} instance using information from the provided {@link ChatScreen} instance.
     * This {@link CommandSuggestor.SuggestionWindow SuggestionWindow} instance will display the provided {@link Suggestions} list.
     * The first suggestion of this {@link CommandSuggestor.SuggestionWindow SuggestionWindow} will be narrated if narrateFirstSuggestion is {@link Boolean#TRUE true}.
     *
     * @param chatScreen The {@link ChatScreen} instance to use for creating the {@link CommandSuggestor.SuggestionWindow SuggestionWindow} instance.
     * @param suggestions The {@link Suggestions} to display in the {@link CommandSuggestor.SuggestionWindow SuggestionWindow} instance.
     * @param narrateFirstSuggestion Whether to narrate the first suggestion.
     * @return The created {@link CommandSuggestor.SuggestionWindow SuggestionWindow} instance.
     */
    public static CommandSuggestor.SuggestionWindow createSuggestionWindow(final @NotNull ChatScreen chatScreen, final @NotNull Suggestions suggestions, final boolean narrateFirstSuggestion) {

        final ChatScreenAccessors castChatScreen                     = (ChatScreenAccessors) chatScreen;
        final CommandSuggestor commandSuggestor                      = castChatScreen.getCommandSuggestor();
        final CommandSuggestorAccessorsInvokers castCommandSuggestor = (CommandSuggestorAccessorsInvokers) commandSuggestor;

        int width = 0;
        Suggestion suggestion;
        final TextRenderer textRenderer = castCommandSuggestor.getTextRenderer();
        for (
                Iterator<Suggestion> var4 = suggestions.getList().iterator();
                var4.hasNext();
                width = Math.max(width, textRenderer.getWidth(suggestion.getText()))
        ) { suggestion = var4.next(); }

        final TextFieldWidget chatField = castChatScreen.getChatField();
        final int x = MathHelper.clamp(
                chatField.getCharacterX(suggestions.getRange().getStart()),
                0,
                chatField.getCharacterX(0) + chatField.getInnerWidth() - width
        );

        final int y = castCommandSuggestor.getChatScreenSized() ? castCommandSuggestor.getOwner().height - 12 : 72;

        final List<Suggestion> sortedSuggestions = castCommandSuggestor.invokeSortSuggestions(suggestions);

        return SuggestionWindowAccessorsInvokers.create(commandSuggestor, x, y, width, sortedSuggestions, narrateFirstSuggestion);
    }

    /**
     * Converts a {@link Collection}<{@link Suggestion}> to a {@link Suggestions} instance.
     *
     * @param chatScreen The {@link ChatScreen} instance used for retrieving the {@link TextFieldWidget chatField}.
     * @param suggestions The {@link Collection}<{@link Suggestion}> to convert.
     * @return The converted {@link Suggestions} instance.
     */
    public static Suggestions fromSuggestionCollection(final ChatScreen chatScreen, final @NotNull Collection<Suggestion> suggestions) {

        final ChatScreenAccessors castChatScreen = (ChatScreenAccessors) chatScreen;
        final TextFieldWidget chatField = castChatScreen.getChatField();
        final int cursor = chatField.getCursor();

        return CommandSource.suggestMatching(
                suggestions.stream().map(Suggestion::getText).collect(Collectors.toList()),
                new SuggestionsBuilder(chatField.getText().substring(0, cursor), cursor)
        ).join();
    }

    /**
     * <p>Creates and returns a {@link StringReader} for the given {@link TextFieldWidget chatField}'s {@link String}.</p>
     * <p>The position of the {@link Integer cursor} of the {@link StringReader} is equal to that of the {@link Integer cursor} of the {@link TextFieldWidget chatField}.</p>
     *
     * @param chatField The {@link TextFieldWidget chatField} to create the {@link StringReader} for.
     * @return The created {@link StringReader} instance.
     */
    public static @NotNull StringReader getReaderAtCursor(final @NotNull TextFieldWidget chatField) {
        final StringReader reader = new StringReader(chatField.getText());
        reader.setCursor(chatField.getCursor());
        return reader;
    }

    /**
     * Creates a {@link Collection}<{@link String}> of suggestions given a {@link String prefix}
     * and an integer range defined by a {@link Integer start} (inclusive) and {@link Integer end} (exclusive) point.
     *
     * @param prefix The {@link String prefix} to use.
     * @param start The {@link Integer start} (inclusive) point.
     * @param end The {@link Integer end} (exclusive) point.
     * @return The created {@link Collection}<{@link String}> of suggestions.
     */
    public static Collection<String> suggestFromIntRange(final String prefix, final int start, final int end) {
        return IntStream.range(start, end).mapToObj(i -> prefix + i).collect(Collectors.toList());
    }

    /**
     * Acts like {@link #suggestFromIntRange(String, int, int)} but with an {@link Integer offset} rather than a {@link Integer end}.
     *
     * @see #suggestFromIntRange(String, int, int) 
     * @param prefix The {@link String prefix} to use.
     * @param start The {@link Integer start} (inclusive) point.
     * @param offset The {@link Integer offset} to use.
     * @return The created {@link Collection}<{@link String}> of suggestions.
     */
    public static Collection<String> suggestionFromIntOffset(final @NotNull String prefix, final int start, final int offset) { return suggestFromIntRange(prefix, start, start + offset);}
}