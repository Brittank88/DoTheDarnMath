package com.brittank88.dtdm.mixin.event;

import com.brittank88.dtdm.event.ChatFieldUpdateCallback;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenEventMixin {

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"), cancellable = true)
    public void DTDM_onChatFieldUpdate(String chatText, CallbackInfo ci) {
        ActionResult result = ChatFieldUpdateCallback.EVENT.invoker().interact((ChatScreen) (Object) this, chatText);
        if (result == ActionResult.FAIL) ci.cancel();
    }
}
