package com.brittank88.dtdm.event.mixin;

import com.brittank88.dtdm.client.DTDMClient;
import com.brittank88.dtdm.event.callback.*;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenEventDispatcher {

    @Inject(method = "init", at = @At("TAIL"), cancellable = true)
    public void DTDM_init(CallbackInfo ci) { if (ChatScreenInitCallback.EVENT.invoker().interact((ChatScreen) (Object) this) == ActionResult.FAIL) ci.cancel(); }

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"), cancellable = true)
    public void DTDM_onChatFieldUpdate(String chatText, CallbackInfo ci) {
        if (ChatScreenChatFieldUpdateCallback.EVENT.invoker().interact(chatText) == ActionResult.FAIL) ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void DTDM_render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ChatScreenRenderCallback.EVENT.invoker().interact(matrices, mouseX, mouseY, delta) == ActionResult.FAIL) ci.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void DTDM_keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (ChatScreenKeyPressedCallback.EVENT.invoker().interact(keyCode, scanCode, modifiers) == ActionResult.FAIL) cir.cancel();
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    public void DTDM_mouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
        if (ChatScreenMouseScrolledCallback.EVENT.invoker().interact(mouseX, mouseY, amount) == ActionResult.FAIL) cir.cancel();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void DTDM_mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (ChatScreenMouseClickedCallback.EVENT.invoker().interact(mouseX, mouseY, button) == ActionResult.FAIL) cir.cancel();
    }
}