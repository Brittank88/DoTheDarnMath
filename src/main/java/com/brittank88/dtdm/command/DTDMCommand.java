package com.brittank88.dtdm.command;

import com.brittank88.dtdm.handler.ConstantHandler;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.*;

public class DTDMCommand {

    private static double calculateExpression(String expression) throws CommandException {
        try { return new Expression(expression).calculate(); }
        catch (Exception e) { throw new CommandException(Text.of("Failed to parse expression: " + e.getLocalizedMessage())); }
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("dtdm")
                    .then(CommandManager.literal("calculate")
                            .then(CommandManager.argument("expression", StringArgumentType.string())
                                    .executes(context -> {  // Without selector.
                                        String expression = StringArgumentType.getString(context, "expression");
                                        context.getSource().sendFeedback(Text.of(expression + " = " + calculateExpression(expression)), false);
                                        return 1;
                                    })
                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                            .executes(context -> {  // With selector.
                                                String expression = StringArgumentType.getString(context, "expression");
                                                ServerCommandSource source = context.getSource();
                                                ServerPlayerEntity playerEntity = source.getPlayer();

                                                // Send feedback to the player.
                                                source.sendFeedback(Text.of(expression + " = " + calculateExpression(expression)), false);

                                                // Broadcast to players in the selector (excluding the player who issued the command).
                                                EntityArgumentType.getPlayers(context, "targets")
                                                        .stream().filter(target -> !target.equals(playerEntity))
                                                        .forEach(player -> player.sendMessage(Text.of(
                                                                playerEntity.getDisplayName() + "calculated" + expression + " = " + calculateExpression(expression)
                                                        ), false));
                                                return 1;
                                            })
                                    )
                            )
                    ).then(CommandManager.literal("function")
                            .then(CommandManager.literal("list")
                                    .executes(context -> 0)
                            ).then(CommandManager.literal("add")
                                    .then(CommandManager.argument("name", StringArgumentType.string())
                                            .suggests(new UniversalSuggestionProvider<>(ignored -> Collections.singletonList("f" + 1)))
                                            .then(CommandManager.argument("function", StringArgumentType.string())
                                                    .executes(context -> 0)
                                            )
                                    )
                            ).then(CommandManager.literal("remove")
                                    .then(CommandManager.argument("name", StringArgumentType.string())
                                            .executes(context -> 0)
                                    )
                            ).then(CommandManager.literal("edit")
                                    .then(CommandManager.argument("name", StringArgumentType.string())
                                            .then(CommandManager.argument("function", StringArgumentType.string())
                                                    .executes(context -> 0)
                                            )
                                    )
                            )
                    ).then(CommandManager.literal("constant")
                            .then(CommandManager.literal("get")
                                    .then(CommandManager.literal("mathematical")
                                            .then(CommandManager.argument("name", StringArgumentType.string())
                                                    .suggests(new UniversalSuggestionProvider<>(ignored -> ConstantHandler.MATHEMATICAL_CONSTANTS.keySet()))
                                                    .executes(ConstantHandler::sendMathematicalConstant)
                                            )
                                    ).then(CommandManager.literal("physical")
                                            .then(CommandManager.argument("name", StringArgumentType.string())
                                                    .suggests(new UniversalSuggestionProvider<>(ignored -> ConstantHandler.PHYSICAL_CONSTANTS.keySet()))
                                                    .executes(ConstantHandler::sendPhysicalConstant)
                                            )
                                    ).then(CommandManager.literal("astronomical")
                                            .then(CommandManager.argument("name", StringArgumentType.string())
                                                    .suggests(new UniversalSuggestionProvider<>(ignored -> ConstantHandler.ASTRONOMICAL_CONSTANTS.keySet()))
                                                    .executes(ConstantHandler::sendAstronomicalConstant)
                                            )
                                    ).then(CommandManager.literal("user")
                                            .then(CommandManager.argument("name", StringArgumentType.string())
                                                    .suggests(new UniversalSuggestionProvider<>(ignored -> ConstantHandler.USER_CONSTANTS.keySet()))
                                                    .executes(ConstantHandler::sendUserConstant)
                                            )
                                    )
                            ).then(CommandManager.literal("add")
                                    .then(CommandManager.argument("name", StringArgumentType.string())
                                            .suggests(new UniversalSuggestionProvider<>(ignored -> Collections.singletonList("c" + 1)))
                                            .then(CommandManager.argument("constant", DoubleArgumentType.doubleArg())
                                                    .executes(context -> ConstantHandler.addConstant(
                                                            StringArgumentType.getString(context, "name"),
                                                            DoubleArgumentType.getDouble(context, "constant")
                                                    ))
                                            )
                                    )
                            ).then(CommandManager.literal("remove")
                                    .then(CommandManager.argument("name", StringArgumentType.string())
                                            .executes(context -> ConstantHandler.removeConstant(
                                                    StringArgumentType.getString(context, "name")
                                            ))
                                    )
                            )
                    )
            );
        });
    }
}
