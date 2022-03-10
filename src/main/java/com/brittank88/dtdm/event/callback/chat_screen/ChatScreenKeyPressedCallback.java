package com.brittank88.dtdm.event.callback.chat_screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;

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

    @NotNull ActionResult interact(int keyCode, int scanCode, int modifiers);
}
