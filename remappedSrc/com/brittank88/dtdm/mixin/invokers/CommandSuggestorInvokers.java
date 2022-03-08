package com.brittank88.dtdm.mixin.invokers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestor.class)
public interface CommandSuggestorInvokers {
    @Invoker List<Suggestion> invokeSortSuggestions(Suggestions suggestions);
    @Invoker static OrderedText invokeFormatException(CommandSyntaxException exception) { throw new UnsupportedOperationException(); }
}
