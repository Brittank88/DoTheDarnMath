package com.brittank88.dtdm.event.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface ChatScreenKeyPressedCallback {

    Event<ChatScreenKeyPressedCallback> EVENT = EventFactory.createArrayBacked(ChatScreenKeyPressedCallback.class,
            listeners -> (keyCode, scanCode, modifiers) -> {
                for (ChatScreenKeyPressedCallback listener : listeners) {
                    ActionResult result = listener.interact(keyCode, scanCode, modifiers);
                    if (result != ActionResult.PASS) { return result; }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(int keyCode, int scanCode, int modifiers);
}
