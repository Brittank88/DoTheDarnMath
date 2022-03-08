package com.brittank88.dtdm.util.suggestion;

import com.brittank88.dtdm.mixin.accessors.ChatScreenAccessors;
import com.brittank88.dtdm.mixin.accessors_invokers.CommandSuggestorAccessorsInvokers;
import com.brittank88.dtdm.mixin.accessors_invokers.SuggestionWindowAccessorsInvokers;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.stream.Collectors;

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
    public static CommandSuggestor.SuggestionWindow createSuggestionWindow(@NonNull ChatScreen chatScreen, @NonNull Suggestions suggestions, boolean narrateFirstSuggestion) {

        ChatScreenAccessors castChatScreen                     = (ChatScreenAccessors) chatScreen;
        CommandSuggestor commandSuggestor                      = castChatScreen.getCommandSuggestor();
        CommandSuggestorAccessorsInvokers castCommandSuggestor = (CommandSuggestorAccessorsInvokers) commandSuggestor;

        int width = 0;
        Suggestion suggestion;
        TextRenderer textRenderer = castCommandSuggestor.getTextRenderer();
        for (
                Iterator<Suggestion> var4 = suggestions.getList().iterator();
                var4.hasNext();
                width = Math.max(width, textRenderer.getWidth(suggestion.getText()))
        ) { suggestion = var4.next(); }

        TextFieldWidget chatField = castChatScreen.getChatField();
        int x = MathHelper.clamp(
                chatField.getCharacterX(suggestions.getRange().getStart()),
                0,
                chatField.getCharacterX(0) + chatField.getInnerWidth() - width
        );

        int y = castCommandSuggestor.getChatScreenSized() ? castCommandSuggestor.getOwner().height - 12 : 72;

        List<Suggestion> sortedSuggestions = castCommandSuggestor.invokeSortSuggestions(suggestions);

        return SuggestionWindowAccessorsInvokers.create(commandSuggestor, x, y, width, sortedSuggestions, narrateFirstSuggestion);
    }

    /**
     * Converts a {@link Collection}<{@link Suggestion}> to a {@link Suggestions} instance.
     *
     * @param chatScreen The {@link ChatScreen} instance used for retrieving the {@link TextFieldWidget chatField}.
     * @param suggestions The {@link Collection}<{@link Suggestion}> to convert.
     * @return The converted {@link Suggestions} instance.
     */
    public static Suggestions fromSuggestionCollection(ChatScreen chatScreen, Collection<Suggestion> suggestions) {

        ChatScreenAccessors castChatScreen = (ChatScreenAccessors) chatScreen;
        TextFieldWidget chatField = castChatScreen.getChatField();
        int cursor = chatField.getCursor();

        return CommandSource.suggestMatching(
                suggestions.stream().map(Suggestion::getText).collect(Collectors.toList()),
                new SuggestionsBuilder(chatField.getText().substring(0, cursor), cursor)
        ).join();
    }
}