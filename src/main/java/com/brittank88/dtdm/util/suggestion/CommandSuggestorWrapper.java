package com.brittank88.dtdm.util.suggestion;

import com.brittank88.dtdm.event.callback.chat_screen.*;
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
import org.jetbrains.annotations.NotNull;

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
    private @NonNull ChatScreen chatScreen;

    private @Nullable CommandSuggestor.SuggestionWindow suggestionWindow;

    private final List<SuggestionSupplier> SuggestionSuppliers = new ArrayList<>();

    private boolean narrateFirstSuggestion = DEFAULT_NARRATION;

    //#// Singleton Code //#//

    /** The singleton instance of {@link CommandSuggestorWrapper this class}. **/
    private static CommandSuggestorWrapper INSTANCE;

    /**
     * Creates the singleton instance of the {@link CommandSuggestorWrapper} if it does not exist,
     * otherwise, returns the existing instance (with the {@link CommandSuggestorWrapper#chatScreen} field updated).
     *
     * @see #CommandSuggestorWrapper(ChatScreen)
     * @param chatScreen The {@link ChatScreen} instance used in the creation or updating of the {@link CommandSuggestorWrapper} instance.
     * @return The {@link CommandSuggestorWrapper} instance.
     */
    public static @NonNull CommandSuggestorWrapper getOrCreate(@NotNull ChatScreen chatScreen) {
        if (INSTANCE == null) return new CommandSuggestorWrapper(chatScreen);

        INSTANCE.chatScreen = chatScreen;
        return INSTANCE;
    }

    /**
     * Function used to get the {@link CommandSuggestorWrapper} instance,
     * if you need to access the instance without supplying a {@link ChatScreen} instance.
     *
     * @see #getOrCreate(ChatScreen) 
     * @return The {@link CommandSuggestorWrapper} instance if it exists, otherwise, null.
     */
    public static @Nullable CommandSuggestorWrapper getInstance() { return INSTANCE; }

    //#// Constructors //#//

    /**
     * Stores a reference to the {@link ChatScreen} instance used in the creation of the {@link CommandSuggestorWrapper} instance.
     * Also registers listeners for each necessary {@link net.fabricmc.fabric.api.event.Event Event}.
     *
     * @param chatScreen The {@link ChatScreen} instance to wrap.
     */
    private CommandSuggestorWrapper(@NonNull ChatScreen chatScreen) {

        // Set internal properties from provided.
        this.chatScreen = chatScreen;

        // Connect ChatScreen events.
        ChatScreenChatFieldUpdateCallback.EVENT.register(this::onChatFieldUpdate);
        ChatScreenRenderCallback.EVENT.register(this::onRender);
        ChatScreenKeyPressedCallback.EVENT.register(this::onKeyPressed);
        ChatScreenMouseClickedCallback.EVENT.register(this::onMouseClicked);
        ChatScreenMouseScrolledCallback.EVENT.register(this::onMouseScrolled);
    }

    /**
     * Creates a new {@link CommandSuggestor.SuggestionWindow} instance and stores it in the {@link CommandSuggestorWrapper} instance.
     *
     * @param suggestions The {@link Collection} of {@link Suggestion}s to display in the {@link CommandSuggestor.SuggestionWindow}.
     * @return The {@link CommandSuggestor.SuggestionWindow} instance.
     * @throws IllegalArgumentException If the {@link Collection} of {@link Suggestion}s is empty.
     */
    private @NonNull CommandSuggestor.SuggestionWindow prepareSuggestionsWindow(@NonNull List<Suggestion> suggestions) throws IllegalArgumentException {
        if (suggestions.isEmpty()) throw new IllegalArgumentException("No Suggestion instances were present in the suggestions list!");
        return SuggestionUtils.createSuggestionWindow(
                this.chatScreen,
                SuggestionUtils.fromSuggestionCollection(this.chatScreen, suggestions),
                this.narrateFirstSuggestion
        );
    }

    //#// Getters //#//

    /**
     * Used to determine if the {@link net.minecraft.client.gui.screen.CommandSuggestor.SuggestionWindow} is narrating the first suggestion.
     *
     * @return {@link Boolean#TRUE} if the {@link net.minecraft.client.gui.screen.CommandSuggestor.SuggestionWindow} is narrating the first suggestion, otherwise, {@link Boolean#FALSE}.
     */
    public boolean getNarrateFirstSuggestion() {
        return this.suggestionWindow == null ? DEFAULT_NARRATION : ((SuggestionWindowAccessorsInvokers) this.suggestionWindow).getLastNarrationIndex() == -1;
    }

    //#// Chain-ables //#//

    /**
     * Used to set whether the {@link net.minecraft.client.gui.screen.CommandSuggestor.SuggestionWindow} is narrating the first suggestion.
     *
     * @param narrate Whether the {@link net.minecraft.client.gui.screen.CommandSuggestor.SuggestionWindow} is narrating the first suggestion.
     * @return The {@link CommandSuggestorWrapper} instance for method chaining.
     */
    public @NotNull CommandSuggestorWrapper setNarration(boolean narrate) { this.narrateFirstSuggestion = narrate; return this; }

    /**
     * Adds a {@link SuggestionSupplier} to the {@link CommandSuggestorWrapper} instance.
     *
     * @param supplier The {@link SuggestionSupplier} to add.
     * @return The {@link CommandSuggestorWrapper} instance for method chaining.
     */
    public @NotNull CommandSuggestorWrapper addSupplier(@NonNull SuggestionSupplier supplier) { this.SuggestionSuppliers.add(supplier); return this; }

    /**
     * Adds multiple {@link SuggestionSupplier}s to the {@link CommandSuggestorWrapper} instance.
     *
     * @param suppliers The {@link SuggestionSupplier}s to add.
     * @return The {@link CommandSuggestorWrapper} instance for method chaining.
     */
    public @NotNull CommandSuggestorWrapper addSuppliers(@NonNull SuggestionSupplier... suppliers) { this.SuggestionSuppliers.addAll(List.of(suppliers)); return this; }

    /**
     * Removes a {@link SuggestionSupplier} from the {@link CommandSuggestorWrapper} instance.
     *
     * @param supplier The {@link SuggestionSupplier} to remove.
     * @return The {@link CommandSuggestorWrapper} instance for method chaining.
     */
    public @NotNull CommandSuggestorWrapper removeSupplier(@NonNull SuggestionSupplier supplier) { this.SuggestionSuppliers.remove(supplier); return this; }

    /**
     * Removes multiple {@link SuggestionSupplier}s from the {@link CommandSuggestorWrapper} instance.
     *
     * @param suppliers The {@link SuggestionSupplier}s to remove.
     * @return The {@link CommandSuggestorWrapper} instance for method chaining.
     */
    public @NotNull CommandSuggestorWrapper removeSuppliers(@NonNull SuggestionSupplier... suppliers) { this.SuggestionSuppliers.removeAll(List.of(suppliers)); return this; }

    //#// Event Callbacks //#//

    @SuppressWarnings("SameReturnValue")
    private @NotNull ActionResult onChatFieldUpdate(String chatText) {

        // Get the chatField.
        TextFieldWidget chatField = ((ChatScreenAccessors) this.chatScreen).getChatField();

        // Collect suggestions and messages from all parsers.

        List<Suggestion> suggestions = this.SuggestionSuppliers.stream()
                .map(parser -> parser.parse(chatField, SuggestionUtils.getReaderAtCursor(chatField)).getSuggestions())
                .flatMap(Collection::stream)
                .toList();

        // If there are suggestions, create a new suggestion window.
        // Otherwise, null the suggestion window.
        if (suggestions.isEmpty()) { this.suggestionWindow = null; }
        else this.suggestionWindow = prepareSuggestionsWindow(suggestions);

        // Allow the event to continue propagating.
        return ActionResult.PASS;
    }

    @SuppressWarnings("SameReturnValue")
    private @NotNull ActionResult onRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.suggestionWindow != null) this.suggestionWindow.render(matrices, mouseX, mouseY);
        return ActionResult.PASS;
    }

    @SuppressWarnings("SameReturnValue")
    private @NotNull ActionResult onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.suggestionWindow != null) this.suggestionWindow.keyPressed(keyCode, scanCode, modifiers);
        return ActionResult.PASS;
    }

    @SuppressWarnings("SameReturnValue")
    private @NotNull ActionResult onMouseClicked(double mouseX, double mouseY, int button) {
        if (this.suggestionWindow != null) this.suggestionWindow.mouseClicked((int) mouseX, (int) mouseY, button);
        return ActionResult.PASS;
    }

    @SuppressWarnings("SameReturnValue")
    private @NotNull ActionResult onMouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.suggestionWindow != null) this.suggestionWindow.mouseScrolled(amount);
        return ActionResult.PASS;
    }
}
