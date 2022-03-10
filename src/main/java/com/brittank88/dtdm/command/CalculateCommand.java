package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.expression.ExpressionUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class CalculateCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        // TODO: Migrate to common lang class.
        String targetString = new TranslatableText("commands.generic.argument.targets").asString(),
                expressionString = new TranslatableText("commands.generic.argument.expression").asString();

        @NonNull LiteralArgumentBuilder<ServerCommandSource> calculateCommand = CommandManager.literal(new TranslatableText("command.calculate.literal").asString())
                .then(CommandManager.argument(expressionString, StringArgumentType.string())
                        .executes(context -> {  // Without selector.

                            // TODO: Migrate to util file.

                            String expression = StringArgumentType.getString(context, expressionString);
                            context.getSource().sendFeedback(
                                    new TranslatableText(
                                            "expression.withResult",
                                            expression,
                                            ExpressionUtils.CalculationTools.calculateExpression(expression)
                                    ), false);
                            return 1;
                        })
                        .then(CommandManager.argument(targetString, EntityArgumentType.players())
                                .executes(context -> {  // With selector.

                                    // TODO: Migrate to util file.

                                    String expression = StringArgumentType.getString(context, expressionString);
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity playerEntity = source.getPlayer();

                                    // Send feedback to the player.
                                    context.getSource().sendFeedback(
                                            new TranslatableText(
                                                    "expression.withResult",
                                                    expression,
                                                    ExpressionUtils.CalculationTools.calculateExpression(expression)
                                            ), false);

                                    // Broadcast to players in the selector (excluding the player who issued the command).
                                    EntityArgumentType.getPlayers(context, targetString)
                                            .stream().filter(target -> !target.equals(playerEntity))
                                            .forEach(player -> player.sendMessage(new TranslatableText(
                                                    "commands.generic.calculate.broadcast",
                                                    playerEntity.getDisplayName(),
                                                    expression,
                                                    ExpressionUtils.CalculationTools.calculateExpression(expression)
                                            ), false));
                                    return 1;
                                })
                        )
                );

        return calculateCommand;
    }
}
