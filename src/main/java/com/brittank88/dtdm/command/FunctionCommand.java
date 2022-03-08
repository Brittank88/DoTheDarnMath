package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.command.UniversalSuggestionProvider;
import com.brittank88.dtdm.util.command.argument_type.FunctionParametersArgumentType;
import com.brittank88.dtdm.util.function.FunctionCategory;
import com.brittank88.dtdm.util.function.FunctionHandler;
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
        for (Map.Entry<FunctionCategory, Collection<Function>> entry : FunctionHandler.FUNCTIONS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument("name", StringArgumentType.word())
                            .suggests(new UniversalSuggestionProvider<>(ignored -> entry.getValue().stream()
                                    .map(Function::getFunctionName)
                                    .collect(Collectors.toList())
                            )).executes(ctx -> FunctionHandler.sendFunction(StringArgumentType.getString(ctx, "name"), entry.getValue(), ctx))
                    )
            );
        }

        @NonNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal("add")
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> Collections.singletonList("f" + 1)))
                        .then(CommandManager.argument("parameters", FunctionParametersArgumentType.functionParameters())
                                .then(CommandManager.argument("expression", StringArgumentType.greedyString())
                                        .executes(ctx -> FunctionHandler.addFunction(
                                                StringArgumentType.getString(ctx, "name"),
                                                FunctionParametersArgumentType.getFunctionParams(ctx, "parameters"),
                                                StringArgumentType.getString(ctx, "expression"),
                                                ctx
                                        ))
                                )
                        )
                );

        @NonNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal("remove")
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .executes(ctx -> FunctionHandler.removeFunction(StringArgumentType.getString(ctx, "name"), ctx))
                );

        return CommandManager.literal("function")
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);
    }
}
