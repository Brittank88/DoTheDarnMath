package com.brittank88.dtdm.event.callback.chat_screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;

public interface ChatScreenRenderCallback {

    Event<ChatScreenRenderCallback> EVENT = EventFactory.createArrayBacked(ChatScreenRenderCallback.class,
            listeners -> (matrices, mouseX, mouseY, delta) -> {
                for (ChatScreenRenderCallback listener : listeners) {
                    ActionResult result = listener.interact(matrices, mouseX, mouseY, delta);
                    if (result != ActionResult.PASS) { return result; }
                }
                return ActionResult.PASS;
            }
    );

    @NotNull ActionResult interact(MatrixStack matrices, int mouseX, int mouseY, float delta);
}
