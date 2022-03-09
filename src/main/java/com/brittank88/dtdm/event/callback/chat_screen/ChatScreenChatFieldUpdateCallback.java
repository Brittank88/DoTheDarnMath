package com.brittank88.dtdm.event.callback.chat_screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface ChatScreenChatFieldUpdateCallback {

    Event<ChatScreenChatFieldUpdateCallback> EVENT = EventFactory.createArrayBacked(ChatScreenChatFieldUpdateCallback.class,
            listeners -> chatText -> {
                for (ChatScreenChatFieldUpdateCallback listener : listeners) {
                    ActionResult result = listener.interact(chatText);
                    if (result != ActionResult.PASS) { return result; }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(String chatText);
}
