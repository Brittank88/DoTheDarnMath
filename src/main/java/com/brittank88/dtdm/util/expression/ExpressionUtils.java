package com.brittank88.dtdm.util.expression;

import com.brittank88.dtdm.util.constant.ConstantUtils;
import com.brittank88.dtdm.util.function.FunctionUtils;
import net.minecraft.command.CommandException;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Expression;

public abstract class ExpressionUtils {

    public static abstract class CalculationTools {

        /**
         * Evaluates the {@link Expression} and returns the {@link Double result}.
         *
         * @param expression The {@link Expression} to evaluate.
         * @return The result of the {@link Expression}.
         * @throws CommandException If the {@link Expression} fails to parse or calculate.
         */
        public static @NonNull Double calculateExpression(@NonNull Expression expression) throws CommandException {

            // Add all missing user-defined constants and functions.
            try {
                FunctionUtils.PopulationTools.populateExpression(expression);   // Populate functions first, otherwise function names may be seen as missing constants.
                ConstantUtils.PopulationTools.populateExpression(expression);   // Then populate the constants.
            } catch (RuntimeException e) { throw new CommandException(new TranslatableText("message.error.expression.population", e.getLocalizedMessage())); }

            double result;
            try { result = expression.calculate(); } catch (Exception e) { throw new CommandException(new TranslatableText("message.error.expression.calculation", e.getLocalizedMessage())); }

            if (!expression.checkSyntax()) throw new CommandException(new TranslatableText("message.error.expression.syntax", expression.getErrorMessage()));

            return result;
        }

        /**
         * Overload for {@link #calculateExpression(Expression)} to accept an {@link String expression string}.
         *
         * @see #calculateExpression(Expression)
         * @param expression The {@link String expression string} to evaluate.
         * @return The result of the {@link String expression string}.
         * @throws CommandException If the {@link String string expression} fails to parse or calculate.
         */
        public static @NonNull Double calculateExpression(@NonNull String expression) throws CommandException {
            Expression expr;
            try { expr = new Expression(expression); } catch (Exception e) { throw new CommandException(new TranslatableText("message.error.expression.parse", e.getLocalizedMessage())); }
            return calculateExpression(expr);
        }
    }
}
