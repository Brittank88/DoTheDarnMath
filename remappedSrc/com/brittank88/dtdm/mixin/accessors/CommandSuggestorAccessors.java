package com.brittank88.dtdm.mixin.accessors;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CommandSuggestor.class)
public interface CommandSuggestorAccessors {
    @Accessor TextRenderer getTextRenderer();
    @Accessor boolean getChatScreenSized();
    @Accessor Screen getOwner();
    @Accessor("x") int getX();
    @Accessor int getWidth();
    @Accessor int getColor();
}
