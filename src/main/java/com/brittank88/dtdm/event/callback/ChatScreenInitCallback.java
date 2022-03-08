package com.brittank88.dtdm.event.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.ActionResult;

public interface ChatScreenInitCallback {

    Event<ChatScreenInitCallback> EVENT = EventFactory.createArrayBacked(ChatScreenInitCallback.class,
            listeners -> instance -> {
                for (ChatScreenInitCallback listener : listeners) {
                    ActionResult result = listener.interact(instance);
                    if (result != ActionResult.PASS) { return result; }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(ChatScreen instance);
}
