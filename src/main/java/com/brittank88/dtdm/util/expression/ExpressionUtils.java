package com.brittank88.dtdm.util.expression;

import com.brittank88.dtdm.util.constant.ConstantUtils;
import com.brittank88.dtdm.util.function.FunctionUtils;
import com.brittank88.dtdm.util.lang.CommonLang;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import org.jetbrains.annotations.NotNull;
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
        public static @NotNull Double calculateExpression(@NotNull Expression expression) throws CommandException {

            // Add all missing user-defined constants and functions.
            try {
                FunctionUtils.PopulationTools.populateExpression(expression);   // Populate functions first, otherwise function names may be seen as missing constants.
                ConstantUtils.PopulationTools.populateExpression(expression);   // Then populate the constants.
            } catch (RuntimeException e) { throw new CommandException(Text.of(I18n.translate("message.error.expression.population", e.getLocalizedMessage()))); }

            double result;
            try { result = expression.calculate(); } catch (Exception e) { throw new CommandException(Text.of(I18n.translate("message.error.expression.calculation", e.getLocalizedMessage()))); }

            if (!expression.checkSyntax()) throw new CommandException(Text.of(I18n.translate("message.error.expression.syntax", expression.getErrorMessage())));

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
        public static @NotNull Double calculateExpression(@NotNull String expression) throws CommandException {
            Expression expr;
            try { expr = new Expression(expression); } catch (Exception e) { throw new CommandException(Text.of(I18n.translate("message.error.expression.parse", e.getLocalizedMessage()))); }
            return calculateExpression(expr);
        }

        /**
         * Sends the result of a calculation to the {@link ServerPlayerEntity player} via the provided {@link CommandContext<ServerCommandSource>}.
         *
         * @param ctx The {@link CommandContext<ServerCommandSource>} to send the result to.
         * @return Command status integer.
         * @throws CommandSyntaxException If the {@link String string expression} from the {@link CommandContext<ServerCommandSource>} fails to parse or calculate.
         */
        @SuppressWarnings("SameReturnValue")
        public static int sendCalculation(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {

            // Send feedback to the player.
            String expression = StringArgumentType.getString(ctx, CommonLang.Argument.EXPRESSION);
            ctx.getSource().sendFeedback(Text.of(expression + " = " + calculateExpression(expression)), false);

            // Broadcast to players in the selector (excluding the player who issued the command).
            ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
            try {
                EntityArgumentType.getPlayers(ctx, CommonLang.Argument.TARGET)
                        .stream().filter(target -> !target.equals(playerEntity))
                        .forEach(player -> player.sendMessage(Text.of(I18n.translate(
                                "commands.generic.calculate.broadcast",
                                playerEntity.getDisplayName(),
                                expression,
                                ExpressionUtils.CalculationTools.calculateExpression(expression)
                        )), false));
            } catch (IllegalArgumentException ignored) {}

            // Return command status integer.
            return 1;
        }
    }
}
