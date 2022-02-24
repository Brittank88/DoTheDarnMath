package com.brittank88.dtdm.mixin;

import com.google.common.collect.Lists;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.mariuszgromada.math.mxparser.Expression;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow CommandSuggestor commandSuggestor;
    @Shadow protected TextFieldWidget chatField;

    private CommandSuggestor.SuggestionWindow window = null;
    private final List<OrderedText> messages = Lists.newArrayList();

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    public void DTDM_onChatFieldUpdate(String chatText, CallbackInfo ci) {

        // If the cursor is not directly in front of an '=' char, clear the suggestions window.
        int cursor = chatField.getCursor();
        if (cursor == 0 || chatText.charAt(cursor - 1) != '=') { window = null; return; }

        // Clear exception messages.
        messages.clear();

        // Attempt to find the largest substring that is a valid expression.
        AtomicInteger start = new AtomicInteger(0);
        int end = cursor - 1;
        Expression expr;
        do {
            if (start.get() >= end) { window = null; return; }
            expr = new Expression(chatText.substring(start.getAndAdd(1), end));
        } while (!expr.checkSyntax());

        // Attempt to calculate the expression. If it fails, the error message is what we will display.
        String[] suggestionStrings = new String[0];
        try { suggestionStrings = new String[]{ String.valueOf(expr.calculate()) }; }
        catch (Exception e) {
            Message parseFailedMessage = new LiteralMessage("Failed to parse expression: " + e.getLocalizedMessage());
            messages.add(CommandSuggestorAccessorsInvokers.invokeFormatException(
                    new CommandSyntaxException(new DynamicCommandExceptionType(ignored -> parseFailedMessage), parseFailedMessage)
            ));
        }

        // Generate a suggestion that is either the result or error.
        Suggestions suggestions = CommandSource.suggestMatching(
                suggestionStrings,
                new SuggestionsBuilder(chatField.getText().substring(0, cursor), cursor)
        ).join();
        if (suggestions.isEmpty()) { window = null; return; }

        // Create a suggestion window.
        CommandSuggestorAccessorsInvokers castCommandSuggestor = (CommandSuggestorAccessorsInvokers) commandSuggestor;
        int i = 0;
        Suggestion suggestion;
        for (
                Iterator<Suggestion> var4 = suggestions.getList().iterator();
                var4.hasNext();
                i = Math.max(i, castCommandSuggestor.getTextRenderer().getWidth(suggestion.getText()))
        ) { suggestion = var4.next(); }
        window = SuggestionWindowInvokers.create(
                commandSuggestor,
                MathHelper.clamp(
                        this.chatField.getCharacterX(suggestions.getRange().getStart()),
                        0,
                        this.chatField.getCharacterX(0) + this.chatField.getInnerWidth() - i
                ),
                castCommandSuggestor.getChatScreenSized() ? castCommandSuggestor.getOwner().height - 12 : 72,
                i,
                castCommandSuggestor.invokeSortSuggestions(suggestions),
                MinecraftClient.getInstance().options.narrator != NarratorMode.OFF
        );
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void DTDM_render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (window != null) window.render(matrices, mouseX, mouseY);
        else {
            CommandSuggestorAccessorsInvokers castCommandSuggestor = (CommandSuggestorAccessorsInvokers) commandSuggestor;
            int i = 0;
            for(Iterator<OrderedText> var5 = messages.iterator(); var5.hasNext(); ++i) {
                int     j = castCommandSuggestor.getChatScreenSized() ? castCommandSuggestor.getOwner().height - 14 - 13 - 12 * i : 72 + 12 * i,
                        x = castCommandSuggestor.getX(),
                        w = castCommandSuggestor.getWidth();
                DrawableHelper.fill(matrices, x - 1, j, x + w + 1, j + 12, castCommandSuggestor.getColor());
                castCommandSuggestor.getTextRenderer().drawWithShadow(matrices, var5.next(), (float) x, (float) (j + 2), -1);
            }
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void DTDM_keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (window != null) window.keyPressed(keyCode, scanCode, modifiers);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"))
    public void DTDM_mouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
        if (window != null) window.mouseScrolled(amount);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void DTDM_mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (window != null) window.mouseClicked((int) mouseX, (int) mouseY, button);
    }
}
