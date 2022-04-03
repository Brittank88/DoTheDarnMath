package com.brittank88.dtdm.mixin.accessors_invokers;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.screen.CommandSuggestor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestor.SuggestionWindow.class)
public interface SuggestionWindowAccessorsInvokers {

    // Accessors
    @Accessor int getLastNarrationIndex();

    // Invokers
    @SuppressWarnings("unused")
    @Invoker("<init>") static CommandSuggestor.@NotNull SuggestionWindow create(    // NON-NLS
            final CommandSuggestor outer,
            final int x,
            final int y,
            final int width,
            final List<Suggestion> suggestions,
            final boolean narrateFirstSuggestion
    ) { throw new UnsupportedOperationException("Invoker method stub called directly!"); }
}
