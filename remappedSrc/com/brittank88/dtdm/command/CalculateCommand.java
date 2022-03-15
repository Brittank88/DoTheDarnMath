package com.brittank88.dtdm.command;

import com.brittank88.dtdm.handler.ExpressionHandler;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


public abstract class CalculateCommand {

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> build() {

        @NotNull LiteralArgumentBuilder<ServerCommandSource> calculateCommand = CommandManager.literal("calculate")
                .then(CommandManager.argument("expression", StringArgumentType.string())
                        .executes(ctx -> {  // Without selector.
                            String expression = StringArgumentType.getString(ctx, "expression");
                            ctx.getSource().sendFeedback(Text.of(expression + " = " + ExpressionHandler.calculateExpression(expression)), false);
                            return 1;
                        })
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(ctx -> {  // With selector.
                                    String expression = StringArgumentType.getString(ctx, "expression");
                                    ServerCommandSource source = ctx.getSource();
                                    ServerPlayerEntity playerEntity = source.getPlayer();

                                    // Send feedback to the player.
                                    source.sendFeedback(Text.of(expression + " = " + ExpressionHandler.calculateExpression(expression)), false);

                                    // Broadcast to players in the selector (excluding the player who issued the command).
                                    EntityArgumentType.getPlayers(ctx, "targets")
                                            .stream().filter(target -> !target.equals(playerEntity))
                                            .forEach(player -> player.sendMessage(Text.of(
                                                    playerEntity.getDisplayName() + "calculated" + expression + " = " + ExpressionHandler.calculateExpression(expression)
                                            ), false));
                                    return 1;
                                })
                        )
                );

        return calculateCommand;
    }
}
