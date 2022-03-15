package com.brittank88.dtdm.handler;

import net.minecraft.command.CommandException;
import net.minecraft.text.Text;

import org.mariuszgromada.math.mxparser.Expression;

public abstract class ExpressionHandler {

    /**
     * Evaluates the {@link String string expression} and returns the {@link Double result}.
     *
     * @param expression The {@link String string expression} to evaluate.
     * @return The result of the {@link String string expression}.
     * @throws CommandException If {@link Expression} fails to parse or calculate the {@link String string expression}.
     */
    public static @NotNull Double calculateExpression(@NotNull String expression) throws CommandException {
        Expression expr;
        try { expr = new Expression(expression); } catch (Exception e) { throw new CommandException(Text.of("Failed to parse expression:\n" + e.getLocalizedMessage())); }

        ConstantHandler.getAllConstants().forEach(expr::addConstants);
        FunctionHandler.getAllFunctions().forEach(expr::addFunctions);

        double result;
        try { result = expr.calculate(); } catch (Exception e) { throw new CommandException(Text.of("Failed to calculate expression:\n" + e.getLocalizedMessage())); }

        if (!expr.checkSyntax()) throw new CommandException(Text.of("Syntax error: " + expr.getErrorMessage()));
        // if (Double.isNaN(result)) throw new CommandException(Text.of("Expression resulted in NaN"));

        return result;
    }
}
