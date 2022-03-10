package com.brittank88.dtdm.mixin.accessors_invokers;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.screen.CommandSuggestor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestor.SuggestionWindow.class)
public interface SuggestionWindowAccessorsInvokers {
    // Accessors
    @Accessor int getLastNarrationIndex();

    // Invokers
    @SuppressWarnings("unused")
    @Invoker("<init>") static CommandSuggestor.@NotNull SuggestionWindow create(CommandSuggestor outer, int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion) { //NON-NLS
        throw new UnsupportedOperationException("Invoker method stub called directly!");
    }
}
