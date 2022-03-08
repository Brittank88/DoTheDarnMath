package com.brittank88.dtdm.util.chat;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.text.OrderedText;

import java.util.List;

public class SuggestionWrapper {

    private CommandSuggestor COMMAND_SUGGESTOR;
    private CommandSuggestor.SuggestionWindow SUGGESTION_WINDOW;

    private final List<OrderedText> messages = Lists.newArrayList();

    public SuggestionWrapper(CommandSuggestor commandSuggestor) {
        this.COMMAND_SUGGESTOR = commandSuggestor;
    }
}
