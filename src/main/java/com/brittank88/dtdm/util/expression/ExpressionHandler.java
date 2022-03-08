package com.brittank88.dtdm.util.expression;

import com.brittank88.dtdm.util.function.FunctionHandler;
import com.brittank88.dtdm.util.constant.ConstantHandler;
import net.minecraft.command.CommandException;
import net.minecraft.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Expression;

public abstract class ExpressionHandler {

    /**
     * Evaluates the {@link Expression} and returns the {@link Double result}.
     *
     * @param expression The {@link Expression} to evaluate.
     * @return The result of the {@link Expression}.
     * @throws CommandException If the {@link Expression} fails to parse or calculate.
     */
    public static @NonNull Double calculateExpression(@NonNull Expression expression) throws CommandException {

        // Add all user-defined constants and functions (avoiding adding anything that is already present, just in case).
        ConstantHandler.getUserConstants().stream().filter(c -> expression.getConstant(c.getConstantName()) == null).forEach(expression::addConstants);
        FunctionHandler.getUserFunctions().stream().filter(f -> expression.getFunction(f.getFunctionName()) == null).forEach(expression::addFunctions);

        double result;
        try { result = expression.calculate(); } catch (Exception e) { throw new CommandException(Text.of("Failed to calculate expression:\n" + e.getLocalizedMessage())); }

        if (!expression.checkSyntax()) throw new CommandException(Text.of("Syntax error: " + expression.getErrorMessage()));

        return result;
    }

    /**
     * Overload for {@link #calculateExpression(Expression)} to accept an {@link String expression string}.
     *
     * @param expression The {@link String expression string} to evaluate.
     * @return The result of the {@link String expression string}.
     * @throws CommandException If the {@link String string expression} fails to parse or calculate.
     */
    public static @NonNull Double calculateExpression(@NonNull String expression) throws CommandException {
        Expression expr;
        try { expr = new Expression(expression); } catch (Exception e) { throw new CommandException(Text.of("Failed to parse expression:\n" + e.getLocalizedMessage())); }
        return calculateExpression(expr);
    }
}
