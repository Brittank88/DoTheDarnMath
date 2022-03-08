package com.brittank88.dtdm.mixin.ai_union;

import com.brittank88.dtdm.mixin.accessors.CommandSuggestorAccessors;
import com.brittank88.dtdm.mixin.invokers.CommandSuggestorInvokers;
import net.minecraft.client.gui.screen.CommandSuggestor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandSuggestor.class)
public interface CommandSuggestorAIUnion extends CommandSuggestorAccessors, CommandSuggestorInvokers {}
