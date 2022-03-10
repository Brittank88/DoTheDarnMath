package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.command.UniversalSuggestionProvider;
import com.brittank88.dtdm.util.command.argument_type.FunctionParametersArgumentType;
import com.brittank88.dtdm.util.function.FunctionCategory;
import com.brittank88.dtdm.util.function.FunctionCoordinator;
import com.brittank88.dtdm.util.function.FunctionUtils;
import com.brittank88.dtdm.util.suggestion.SuggestionUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Function;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class FunctionCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        // TODO: Support optional function description.

        // TODO: Migrate to common lang class.
        String nameString = new TranslatableText("commands.generic.argument.name").asString(),
                parametersString = new TranslatableText("commands.generic.argument.parameters").asString(),
                expressionString = new TranslatableText("commands.generic.argument.expression").asString(),
                addString = new TranslatableText("commands.generic.literal.add").asString(),
                removeString = new TranslatableText("commands.generic.literal.remove").asString(),
                getString = new TranslatableText("commands.generic.literal.get").asString();


        @NonNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal(getString);
        for (Map.Entry<FunctionCategory, Collection<Function>> entry : FunctionCoordinator.FUNCTIONS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument(nameString, StringArgumentType.word())
                            .suggests(new UniversalSuggestionProvider<>(ignored -> entry.getValue().stream()
                                    .map(Function::getFunctionName)
                                    .collect(Collectors.toList())
                            )).executes(ctx -> FunctionUtils.CommandTools.sendFunction(StringArgumentType.getString(ctx, nameString), entry.getValue(), ctx))
                    )
            );
        }

        @NonNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal(addString)
                .then(CommandManager.argument(nameString, StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> SuggestionUtils.suggestionFromIntOffset(
                                new TranslatableText("commands.dtdm.function.add.argument.name.suggestPrefix"), FunctionCoordinator.getUserFunctions().size(), 3)
                        )).then(CommandManager.argument(parametersString, FunctionParametersArgumentType.functionParameters())
                                .then(CommandManager.argument(expressionString, StringArgumentType.greedyString())
                                        .executes(ctx -> FunctionCoordinator.addFunction(
                                                StringArgumentType.getString(ctx, nameString),
                                                FunctionParametersArgumentType.getFunctionParams(ctx, parametersString),
                                                StringArgumentType.getString(ctx, expressionString),
                                                ctx
                                        ))
                                )
                        )
                );

        @NonNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal(removeString)
                .then(CommandManager.argument(nameString, StringArgumentType.word())
                        .executes(ctx -> FunctionCoordinator.removeFunction(StringArgumentType.getString(ctx, nameString), ctx))
                );

        return CommandManager.literal(new TranslatableText("commands.dtdm.function.literal").asString())
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);
    }
}
