package com.brittank88.dtdm.mixin;

import com.brittank88.dtdm.DTDM;
import com.brittank88.dtdm.access.CommandSuggestorAccessors;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandSource;
import org.mariuszgromada.math.mxparser.Expression;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow CommandSuggestor commandSuggestor;
    @Shadow protected TextFieldWidget chatField;

    private Expression expr = null;

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    public void DTDM_onChatFieldUpdate(String chatText, CallbackInfo ci) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!chatText.endsWith("=")) { expr = null; return; }

        AtomicInteger start = new AtomicInteger(0);
        int end = chatText.length() - 1;
        do {
            if (start.get() >= end - 1) { expr = null; return; }
            expr = new Expression(chatText.substring(start.getAndAdd(1), end));
        } while (!expr.checkSyntax());
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void DTDM_render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (expr == null) return;

        DTDM.LOGGER.info(expr.calculate()); // TODO: Remove.

        int cursor = chatField.getCursor();
        CommandSuggestor.SuggestionWindow suggestionWindow = ((CommandSuggestorAccessors) commandSuggestor).getSuggestionWindow(
                CommandSource.suggestMatching(
                        new String[]{String.valueOf(expr.calculate())},
                        new SuggestionsBuilder(chatField.getText().substring(0, cursor), cursor)
                ),
                MinecraftClient.getInstance().options.narrator != NarratorMode.OFF
        );
        if (suggestionWindow != null){ suggestionWindow.render(matrices, mouseX, mouseY); }
    }
}
