package com.brittank88.dtdm.event.mixin;

import com.brittank88.dtdm.event.callback.minecraft_client.MinecraftClientSetScreenCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientEventDispatcher {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreen(Screen screen, @NotNull CallbackInfo ci) {
        if (MinecraftClientSetScreenCallback.EVENT.invoker().interact(screen) == ActionResult.FAIL) ci.cancel();
    }
}
