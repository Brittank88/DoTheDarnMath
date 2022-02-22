package com.brittank88.dtdm.mixin;

import com.brittank88.dtdm.DTDM;
import net.minecraft.client.gui.screen.ChatScreen;
import org.mariuszgromada.math.mxparser.Function;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    private static Map<String, Function> functions = new HashMap<>();

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    public void DTDM_onChatFieldUpdate(String chatText, CallbackInfo ci) {
        Expression expression = new Expression(chatText);
        if (expression.checkSyntax()) {
            expression.addFunctions(
                    Arrays.stream(expression.getMissingUserDefinedFunctions())
                            .filter(functions::containsKey)
                            .map(functions::get)
                            .toArray(Function[]::new)
            );
            DTDM.LOGGER.info(expression.calculate());
        }

        Function function = new Function(chatText);
        if (function.checkSyntax()) {
            DTDM.LOGGER.info(function);
            functions.putIfAbsent(function.getFunctionName(), function);
        }
    }
}
