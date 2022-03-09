package com.brittank88.dtdm.event.callback.minecraft_client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public interface MinecraftClientSetScreenCallback {
    Event<MinecraftClientSetScreenCallback> EVENT = EventFactory.createArrayBacked(MinecraftClientSetScreenCallback.class,
            listeners -> screen -> {
                for (MinecraftClientSetScreenCallback listener : listeners) {
                    ActionResult result = listener.interact(screen);
                    if (result != ActionResult.PASS) { return result; }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(@Nullable Screen screen);
}
