package com.brittank88.dtdm.event.callback.chat_screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface ChatScreenMouseScrolledCallback {

    Event<ChatScreenMouseScrolledCallback> EVENT = EventFactory.createArrayBacked(ChatScreenMouseScrolledCallback.class,
            listeners -> (mouseX, mouseY, amount) -> {
                for (ChatScreenMouseScrolledCallback listener : listeners) {
                    ActionResult result = listener.interact(mouseX, mouseY, amount);
                    if (result != ActionResult.PASS) { return result; }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(double mouseX, double mouseY, double amount);
}
