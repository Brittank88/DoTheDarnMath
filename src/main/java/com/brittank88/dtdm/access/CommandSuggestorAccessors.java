package com.brittank88.dtdm.access;

import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.CommandSuggestor;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;

public interface CommandSuggestorAccessors {
    CommandSuggestor.SuggestionWindow getSuggestionWindow(CompletableFuture<Suggestions> pendingSuggestions, boolean narrateFirstSuggestion)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
