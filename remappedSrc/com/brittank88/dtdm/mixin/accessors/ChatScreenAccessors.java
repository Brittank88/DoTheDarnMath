package com.brittank88.dtdm.mixin.accessors;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatScreen.class)
public interface ChatScreenAccessors {
    @Accessor TextFieldWidget getChatField();
    @Accessor CommandSuggestor getCommandSuggestor();
}
