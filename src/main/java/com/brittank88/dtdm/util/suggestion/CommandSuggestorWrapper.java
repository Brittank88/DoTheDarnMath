package com.brittank88.dtdm.util.suggestion;

import com.brittank88.dtdm.event.callback.*;
import com.brittank88.dtdm.mixin.accessors.ChatScreenAccessors;
import com.brittank88.dtdm.mixin.accessors_invokers.SuggestionWindowAccessorsInvokers;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CommandSuggestorWrapper {

    //#// Fields //#//

    // Constants
    private static final boolean DEFAULT_NARRATION = switch (MinecraftClient.getInstance().options.narrator) {
        case ALL, SYSTEM -> true;
        default -> false;
    };

    // Internal properties.
    private @NonNull final ChatScreen chatScreen;

    private @Nullable CommandSuggestor.SuggestionWindow suggestionWindow;

    private final List<SuggestionSupplier> SuggestionSuppliers = new ArrayList<>();

    private boolean narrateFirstSuggestion = DEFAULT_NARRATION;

    //#// Constructors //#//

    public CommandSuggestorWrapper(@NonNull ChatScreen chatScreen) {

        // Set internal properties from provided.
        this.chatScreen = chatScreen;

        // Connect ChatScreen events.
        ChatScreenChatFieldUpdateCallback.EVENT.register(this::onChatFieldUpdate);
        ChatScreenRenderCallback.EVENT.register(this::onRender);
        ChatScreenKeyPressedCallback.EVENT.register(this::onKeyPressed);
        ChatScreenMouseClickedCallback.EVENT.register(this::onMouseClicked);
        ChatScreenMouseScrolledCallback.EVENT.register(this::onMouseScrolled);
    }

    private @NonNull CommandSuggestor.SuggestionWindow prepareSuggestionsWindow(@NonNull List<Suggestion> suggestions) throws IllegalArgumentException {
        if (suggestions.isEmpty()) throw new IllegalArgumentException("No Suggestion instances were present in the suggestions list!");
        return SuggestionUtils.createSuggestionWindow(
                this.chatScreen,
                SuggestionUtils.fromSuggestionCollection(this.chatScreen, suggestions),
                this.narrateFirstSuggestion
        );
    }

    //#// Getters //#//

    public boolean getNarrateFirstSuggestion() {
        return this.suggestionWindow == null ? DEFAULT_NARRATION : ((SuggestionWindowAccessorsInvokers) this.suggestionWindow).getLastNarrationIndex() == -1;
    }

    //#// Chain-ables //#//

    public CommandSuggestorWrapper setNarration(boolean narrate) { this.narrateFirstSuggestion = narrate; return this; }
    public CommandSuggestorWrapper addSupplier(@NonNull SuggestionSupplier supplier) { this.SuggestionSuppliers.add(supplier); return this; }
    public CommandSuggestorWrapper addSuppliers(@NonNull SuggestionSupplier... suppliers) { this.SuggestionSuppliers.addAll(List.of(suppliers)); return this; }

    //#// Event Callbacks //#//

    private ActionResult onChatFieldUpdate(String chatText) {

        // Get the chatField.
        TextFieldWidget chatField = ((ChatScreenAccessors) this.chatScreen).getChatField();

        // Collect suggestions and messages from all parsers.
        List<Suggestion> suggestions = this.SuggestionSuppliers.stream()
                .map(parser -> parser.parse(chatField).getSuggestions())
                .flatMap(Collection::stream)
                .toList();

        // If there are suggestions, create a new suggestion window.
        // Otherwise, null the suggestion window.
        if (suggestions.isEmpty()) { this.suggestionWindow = null; }
        else this.suggestionWindow = prepareSuggestionsWindow(suggestions);

        // Allow the event to continue propagating.
        return ActionResult.PASS;
    }

    private ActionResult onRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.suggestionWindow != null) this.suggestionWindow.render(matrices, mouseX, mouseY);
        return ActionResult.PASS;
    }

    private ActionResult onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.suggestionWindow != null) this.suggestionWindow.keyPressed(keyCode, scanCode, modifiers);
        return ActionResult.PASS;
    }

    private ActionResult onMouseClicked(double mouseX, double mouseY, int button) {
        if (this.suggestionWindow != null) this.suggestionWindow.mouseClicked((int) mouseX, (int) mouseY, button);
        return ActionResult.PASS;
    }

    private ActionResult onMouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.suggestionWindow != null) this.suggestionWindow.mouseScrolled(amount);
        return ActionResult.PASS;
    }
}
