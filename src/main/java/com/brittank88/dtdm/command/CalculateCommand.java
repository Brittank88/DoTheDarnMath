package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.expression.ExpressionUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class CalculateCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        // TODO: Migrate to common lang class.
        String targetString = I18n.translate("commands.generic.argument.targets"),
                expressionString = I18n.translate("commands.generic.argument.expression");

        @NonNull LiteralArgumentBuilder<ServerCommandSource> calculateCommand = CommandManager.literal(I18n.translate("commands.dtdm.calculate.literal"))
                .then(CommandManager.argument(expressionString, StringArgumentType.string())
                        .executes(context -> {  // Without selector.

                            // TODO: Migrate to util file.

                            String expression = StringArgumentType.getString(context, expressionString);
                            context.getSource().sendFeedback(Text.of(
                                    I18n.translate(
                                            "expression.withResult",
                                            expression,
                                            ExpressionUtils.CalculationTools.calculateExpression(expression)
                                    )), false);
                            return 1;
                        })
                        .then(CommandManager.argument(targetString, EntityArgumentType.players())
                                .executes(context -> {  // With selector.

                                    // TODO: Migrate to util file.

                                    String expression = StringArgumentType.getString(context, expressionString);
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity playerEntity = source.getPlayer();

                                    // Send feedback to the player.
                                    context.getSource().sendFeedback(Text.of(
                                            I18n.translate(
                                                    "expression.withResult",
                                                    expression,
                                                    ExpressionUtils.CalculationTools.calculateExpression(expression)
                                            )), false);

                                    // Broadcast to players in the selector (excluding the player who issued the command).
                                    EntityArgumentType.getPlayers(context, targetString)
                                            .stream().filter(target -> !target.equals(playerEntity))
                                            .forEach(player -> player.sendMessage(Text.of(I18n.translate(
                                                    "commands.generic.calculate.broadcast",
                                                    playerEntity.getDisplayName(),
                                                    expression,
                                                    ExpressionUtils.CalculationTools.calculateExpression(expression)
                                            )), false));
                                    return 1;
                                })
                        )
                );

        return calculateCommand;
    }
}
