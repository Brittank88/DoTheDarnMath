package com.brittank88.dtdm.mixin.accessors_invokers;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.screen.CommandSuggestor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestor.SuggestionWindow.class)
public interface SuggestionWindowAccessorsInvokers {

    // Accessors
    @Accessor List<Suggestion> getSuggestions();
    @Accessor @Mutable void setSuggestions(List<Suggestion> suggestions);
    @Accessor int getLastNarrationIndex();

    // Invokers
    @Invoker("<init>") static CommandSuggestor.SuggestionWindow create(CommandSuggestor outer, int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion) {
        throw new UnsupportedOperationException();
    }
}
