package com.brittank88.dtdm.register;

import com.brittank88.dtdm.event.callback.ChatScreenInitCallback;
import com.brittank88.dtdm.util.constant.ConstantHandler;
import com.brittank88.dtdm.util.expression.ExpressionHandler;
import com.brittank88.dtdm.util.suggestion.CommandSuggestorWrapper;
import com.brittank88.dtdm.util.suggestion.SuggestionSupplier;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ActionResult;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SuggestionSupplierRegistry {

    public static void register() {

        ChatScreenInitCallback.EVENT.register(instance -> {

            // TODO: Use a StringReader instead (and enforce spaces around the expression).
            // FIXME: User-defined functions are not recognised.
            new CommandSuggestorWrapper(instance)
                    .addSupplier(new SuggestionSupplier(chatField -> {

                        // If the cursor is not directly in front of an '=' char, we have no suggestions.
                        int cursor = chatField.getCursor();
                        String chatText = chatField.getText();
                        if (cursor == 0 || chatText.charAt(cursor - 1) != '=') { return Collections.emptyList(); }

                        // Attempt to find the largest substring that is a valid expression.
                        AtomicInteger start = new AtomicInteger(0);
                        int end = cursor - 1;
                        Expression expr;
                        do {
                            if (start.get() >= end) { return Collections.emptyList(); }
                            expr = new Expression(chatText.substring(start.getAndAdd(1), end));
                            expr.addConstants(
                                    ConstantHandler.getUserConstants().stream()
                                            .map(c -> new Constant("[" + c.getConstantName() + "]", c.getConstantValue()))
                                            .toArray(Constant[]::new)
                            );
                        } while (!expr.checkSyntax());

                        // Attempt to calculate the expression. If it fails, we have no suggestions.
                        String[] suggestionStrings;
                        try { suggestionStrings = new String[]{ String.valueOf(ExpressionHandler.calculateExpression(expr)) }; }
                        catch (Exception e) { return Collections.emptyList(); }

                        // Return either the suggestions or an empty list.
                        return CommandSource.suggestMatching(
                                suggestionStrings,
                                new SuggestionsBuilder(chatField.getText().substring(0, cursor), cursor)
                        ).join().getList();
                    }));

            return ActionResult.PASS;
        });
    }
}
