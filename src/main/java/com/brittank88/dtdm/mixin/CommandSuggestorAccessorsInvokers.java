package com.brittank88.dtdm.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestor.class)
public interface CommandSuggestorAccessorsInvokers {

    @Accessor TextRenderer getTextRenderer();
    @Accessor boolean getChatScreenSized();
    @Accessor Screen getOwner();
    @SuppressWarnings("MixinAnnotationTarget") @Accessor int getX();
    @Accessor int getWidth();
    @Accessor int getColor();

    @Invoker List<Suggestion> invokeSortSuggestions(Suggestions suggestions);

    @Invoker static OrderedText invokeFormatException(CommandSyntaxException exception) { throw new UnsupportedOperationException(); }
}
