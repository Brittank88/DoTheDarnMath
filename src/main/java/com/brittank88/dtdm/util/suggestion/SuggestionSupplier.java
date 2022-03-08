package com.brittank88.dtdm.util.suggestion;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandException;
import net.minecraft.text.OrderedText;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class SuggestionSupplier {

    private final Function<TextFieldWidget, List<Suggestion>> parser;

    private final List<Suggestion> suggestions = new ArrayList<>();
    private final List<OrderedText> messages = new ArrayList<>();

    public SuggestionSupplier(Function<TextFieldWidget, List<Suggestion>> parser) { this.parser = parser; }

    public SuggestionSupplier parse(TextFieldWidget chatField) {
        this.suggestions.clear();
        try { this.suggestions.addAll(this.parser.apply(chatField)); }
        catch (CommandException e) { this.messages.add(e.getTextMessage().asOrderedText()); }
        return this;
    }
    public List<Suggestion> getSuggestions() { return this.suggestions; }
    public List<OrderedText> getMessages() { return this.messages; }
}
