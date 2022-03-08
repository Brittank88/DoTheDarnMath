package com.brittank88.dtdm.mixin.accessors_invokers;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestor.class)
public interface CommandSuggestorAccessorsInvokers {
    // Accessors
    @Accessor TextRenderer getTextRenderer();
    @Accessor boolean getChatScreenSized();
    @Accessor Screen getOwner();

    // Invokers
    @Invoker List<Suggestion> invokeSortSuggestions(Suggestions suggestions);
}

