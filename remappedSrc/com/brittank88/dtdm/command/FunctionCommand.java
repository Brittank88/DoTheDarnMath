package com.brittank88.dtdm.command;

import com.brittank88.dtdm.handler.FunctionHandler;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Function;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class FunctionCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        // TODO: Support optional function description.

        @NonNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal("get");
        for (Map.Entry<FunctionHandler.FUNCTION_CATEGORY, Collection<Function>> entry : FunctionHandler.FUNCTIONS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument("name", StringArgumentType.string())
                            .suggests(new UniversalSuggestionProvider<>(ignored -> entry.getValue().stream()
                                    .map(Function::getFunctionName)
                                    .collect(Collectors.toList())
                            )).executes(ctx -> FunctionHandler.sendFunction(StringArgumentType.getString(ctx, "name"), entry.getValue(), ctx))
                    )
            );
        }

        @NonNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal("add")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> Collections.singletonList("f" + 1)))
                        .then(CommandManager.argument("expression", StringArgumentType.string())
                                .executes(ctx -> FunctionHandler.addFunction(
                                        StringArgumentType.getString(ctx, "name"),
                                        StringArgumentType.getString(ctx, "expression"),
                                        ctx
                                ))
                        )
                );

        @NonNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal("remove")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(ctx -> FunctionHandler.removeFunction(StringArgumentType.getString(ctx, "name"), ctx))
                );

        return CommandManager.literal("function")
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);
    }
}
