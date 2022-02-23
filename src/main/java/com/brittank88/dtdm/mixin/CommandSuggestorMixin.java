package com.brittank88.dtdm.mixin;

import com.brittank88.dtdm.access.CommandSuggestorAccessors;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestor.class)
public abstract class CommandSuggestorMixin implements CommandSuggestorAccessors {

    @Shadow @Nullable private CompletableFuture<Suggestions> pendingSuggestions;
    @Shadow @Final TextFieldWidget textField;
    @Shadow @Final TextRenderer textRenderer;
    @Shadow @Final boolean chatScreenSized;
    @Shadow @Final Screen owner;
    @Shadow @Nullable CommandSuggestor.SuggestionWindow window;
    @Shadow protected abstract List<Suggestion> sortSuggestions(Suggestions suggestions);

    @Shadow public abstract void setWindowActive(boolean windowActive);

    public CommandSuggestor.SuggestionWindow getSuggestionWindow(CompletableFuture<Suggestions> pendingSuggestions, boolean narrateFirstSuggestion) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Suggestions suggestions = pendingSuggestions.join();
        if (suggestions.isEmpty()) return null;

        int i = 0;

        Suggestion suggestion;
        for (
                Iterator<Suggestion> var4 = suggestions.getList().iterator();
                var4.hasNext();
                i = Math.max(i, this.textRenderer.getWidth(suggestion.getText()))
        ) { suggestion = var4.next(); }

        int j = MathHelper.clamp(this.textField.getCharacterX(suggestions.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
        int k = this.chatScreenSized ? this.owner.height - 12 : 72;

        return SuggestionWindowAccessors.create((CommandSuggestor) (Object) this, j, k, i, this.sortSuggestions(suggestions), narrateFirstSuggestion);
    }
}
