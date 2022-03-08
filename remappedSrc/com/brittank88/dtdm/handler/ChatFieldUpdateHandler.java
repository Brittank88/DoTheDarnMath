package com.brittank88.dtdm.handler;

import com.brittank88.dtdm.event.ChatFieldUpdateCallback;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.ActionResult;

public abstract class ChatFieldUpdateHandler {

    public static void register() { ChatFieldUpdateCallback.EVENT.register(ChatFieldUpdateHandler::handleChatFieldUpdate); }

    private static ActionResult handleChatFieldUpdate(ChatScreen screen, String text) {


        return ActionResult.PASS;
    }
}
