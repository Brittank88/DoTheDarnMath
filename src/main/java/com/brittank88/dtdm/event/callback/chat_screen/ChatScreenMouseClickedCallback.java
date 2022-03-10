package com.brittank88.dtdm.event.callback.chat_screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;

public interface ChatScreenMouseClickedCallback {

    Event<ChatScreenMouseClickedCallback> EVENT = EventFactory.createArrayBacked(ChatScreenMouseClickedCallback.class,
            listeners -> (mouseX, mouseY, button) -> {
                for (ChatScreenMouseClickedCallback listener : listeners) {
                    ActionResult result = listener.interact(mouseX, mouseY, button);
                    if (result != ActionResult.PASS) { return result; }
                }
                return ActionResult.PASS;
            }
    );

    @NotNull ActionResult interact(double mouseX, double mouseY, int button);
}
