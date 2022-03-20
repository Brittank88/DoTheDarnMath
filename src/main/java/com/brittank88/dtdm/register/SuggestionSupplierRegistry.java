package com.brittank88.dtdm.register;

import com.brittank88.dtdm.client.DTDMClient;
import com.brittank88.dtdm.event.callback.minecraft_client.MinecraftClientSetScreenCallback;
import com.brittank88.dtdm.util.exception.RegistrationFailedException;
import com.brittank88.dtdm.util.expression.ExpressionUtils;
import com.brittank88.dtdm.util.suggestion.CommandSuggestorWrapper;
import com.brittank88.dtdm.util.suggestion.SuggestionSupplier;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NonNls;

import java.util.Collections;

public @NonNls abstract class SuggestionSupplierRegistry {

    public static void register() throws RegistrationFailedException {

        try {
            MinecraftClientSetScreenCallback.EVENT.register(screen -> {

                // We only care about ChatScreen instances.
                if (!(screen instanceof ChatScreen)) return ActionResult.PASS;

                CommandSuggestorWrapper.getOrCreate((ChatScreen) screen)
                        .addSupplier(new SuggestionSupplier((chatField, reader) -> {

                            // If the string isn't at least two characters long, there clearly cannot be a valid expression.
                            // If the string is at least two characters long but the reader's cursor is not that far,
                            // we definitely are not in the right position to suggestion an expression solution.
                            if (chatField.getText().length() < 2 || reader.getCursor() < 2) return Collections.emptyList();

                            // Expect an '=' char directly preceding the cursor.
                            final int end = reader.getCursor() - 1;
                            reader.setCursor(end);  // Save the position just prior to our current cursor position, as it should mark the end of the possible expression string.
                            if (reader.read() != '=') return Collections.emptyList();

                            // Now, expect either a whitespace, or the end of the string.
                            // Either way, the cursor should now be just past the '=' char
                            // (the correct location for suggesting an expression solution).
                            if (reader.canRead() && reader.peek() != ' ') return Collections.emptyList();

                            // Attempt to find the largest substring that is a valid expression by testing each possible substring prior to the '=' char.
                            double result;
                            for (int i = 0; i < end; i++) {

                                // Seek to position i.
                                reader.setCursor(i);

                                // Read out an expression between the current index i and the end position.
                                StringBuilder expressionString = new StringBuilder();
                                while (reader.canRead() && reader.getCursor() < end) expressionString.append(reader.read());

                                // Ensure this expression begins at the start of the string or is preceded by a whitespace:
                                reader.setCursor(i - 1);
                                if (i != 0 && reader.peek() != ' ') continue;

                                // Attempt to calculate the expression and determine a result from it.
                                try { result = ExpressionUtils.CalculationTools.calculateExpression(expressionString.toString()); }
                                catch (CommandException e) { continue; }

                                // If we were able to calculate a result, we can return a suggestion containing it.
                                String resultString = String.valueOf(result);
                                return CommandSource.suggestMatching(
                                        Collections.singleton(resultString.endsWith(".0") ? resultString.substring(0, resultString.length() - 2) : resultString),
                                        new SuggestionsBuilder(chatField.getText().substring(0, end), end)
                                ).join().getList();
                            }

                            // If we were unable to calculate a result, we return an empty list.
                            return Collections.emptyList();
                        }));

                return ActionResult.PASS;
            });
        } catch (Exception e) { throw new RegistrationFailedException(e, RegistrationFailedException.REGISTRY_TYPE.SUGGESTION_SUPPLIER); }

        DTDMClient.LOGGER.info("(SuggestionSupplierRegistry) Registration complete!");
    }
}
