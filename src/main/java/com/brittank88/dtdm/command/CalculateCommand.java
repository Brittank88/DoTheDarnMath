package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.expression.ExpressionUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class CalculateCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        @NonNull LiteralArgumentBuilder<ServerCommandSource> calculateCommand = CommandManager.literal("calculate")
                .then(CommandManager.argument("expression", StringArgumentType.string())
                        .executes(context -> {  // Without selector.
                            String expression = StringArgumentType.getString(context, "expression");
                            context.getSource().sendFeedback(Text.of(expression + " = " + ExpressionUtils.CalculationTools.calculateExpression(expression)), false);
                            return 1;
                        })
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(context -> {  // With selector.
                                    String expression = StringArgumentType.getString(context, "expression");
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity playerEntity = source.getPlayer();

                                    // Send feedback to the player.
                                    source.sendFeedback(Text.of(expression + " = " + ExpressionUtils.CalculationTools.calculateExpression(expression)), false);

                                    // Broadcast to players in the selector (excluding the player who issued the command).
                                    EntityArgumentType.getPlayers(context, "targets")
                                            .stream().filter(target -> !target.equals(playerEntity))
                                            .forEach(player -> player.sendMessage(Text.of(
                                                    playerEntity.getDisplayName() + "calculated" + expression + " = " + ExpressionUtils.CalculationTools.calculateExpression(expression)
                                            ), false));
                                    return 1;
                                })
                        )
                );

        return calculateCommand;
    }
}
