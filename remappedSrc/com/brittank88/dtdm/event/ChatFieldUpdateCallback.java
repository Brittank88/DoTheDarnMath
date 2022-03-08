package com.brittank88.dtdm.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.ActionResult;

/**
 * Callback for changing the text content of the {@link net.minecraft.client.gui.widget.TextFieldWidget chatField}.
 * Called before the {@link net.minecraft.client.gui.widget.TextFieldWidget chatField} is updated.
 * <br>
 * Upon return:
 * <br> - {@link ActionResult#SUCCESS} cancels further processing and allows normal {@link net.minecraft.client.gui.widget.TextFieldWidget chatField} update behaviour.
 * <br> - {@link ActionResult#PASS} falls back to further processing and defaults to {@link ActionResult#SUCCESS} if no other listeners are available.
 * <br> - {@link ActionResult#FAIL} cancels further processing and prevents the {@link net.minecraft.client.gui.widget.TextFieldWidget chatField} from being updated.
 */
public interface ChatFieldUpdateCallback {

    Event<ChatFieldUpdateCallback> EVENT = EventFactory.createArrayBacked(ChatFieldUpdateCallback.class,
            listeners -> (screen, text) -> {
                for (ChatFieldUpdateCallback listener : listeners) {
                    ActionResult result = listener.interact(screen, text);
                    if (result != ActionResult.PASS) return result;
                }

                return ActionResult.PASS;
            });

    ActionResult interact(ChatScreen screen, String text);
}
