package com.brittank88.dtdm.mixin;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.screen.CommandSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestor.SuggestionWindow.class)
public interface SuggestionWindowAccessors
{
    @Invoker("<init>")
    static CommandSuggestor.SuggestionWindow create(CommandSuggestor outer, int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion) {
        throw new UnsupportedOperationException();
    }
}
