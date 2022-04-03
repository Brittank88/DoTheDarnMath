package com.brittank88.dtdm.event.callback.chat_screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;

public interface ChatScreenMouseScrolledCallback {

    Event<ChatScreenMouseScrolledCallback> EVENT = EventFactory.createArrayBacked(ChatScreenMouseScrolledCallback.class,
            listeners -> (mouseX, mouseY, amount) -> {
                for (final ChatScreenMouseScrolledCallback listener : listeners) {
                    ActionResult result = listener.interact(mouseX, mouseY, amount);
                    if (result != ActionResult.PASS) { return result; }
                }
                return ActionResult.PASS;
            }
    );

    @NotNull ActionResult interact(final double mouseX, final double mouseY, final double amount);
}
