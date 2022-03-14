package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.command.UniversalSuggestionProvider;
import com.brittank88.dtdm.util.command.argument_type.FunctionParametersArgumentType;
import com.brittank88.dtdm.util.function.FunctionCategory;
import com.brittank88.dtdm.util.function.FunctionCoordinator;
import com.brittank88.dtdm.util.function.FunctionUtils;
import com.brittank88.dtdm.util.lang.CommonLang;
import com.brittank88.dtdm.util.suggestion.SuggestionUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Function;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class FunctionCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        // TODO: Support optional function description.

        @NonNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal(CommonLang.Literal.GET);
        for (Map.Entry<FunctionCategory, Collection<Function>> entry : FunctionCoordinator.FUNCTIONS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument(CommonLang.Argument.NAME, StringArgumentType.word())
                            .suggests(new UniversalSuggestionProvider<>(ignored -> entry.getValue().stream()
                                    .map(Function::getFunctionName)
                                    .collect(Collectors.toList())
                            )).executes(ctx -> FunctionUtils.CommandTools.sendFunction(ctx, entry.getValue()))
                    )
            );
        }

        @NonNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal(CommonLang.Literal.ADD)
                .then(CommandManager.argument(CommonLang.Argument.NAME, StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> SuggestionUtils.suggestionFromIntOffset(
                                I18n.translate("commands.dtdm.function.add.argument.name.suggest_prefix"), FunctionCoordinator.getUserFunctions().size(), 3)
                        )).then(CommandManager.argument(CommonLang.Argument.PARAMETERS, FunctionParametersArgumentType.functionParameters())
                                .then(CommandManager.argument(CommonLang.Argument.EXPRESSION, StringArgumentType.greedyString())
                                        .executes(ctx -> FunctionCoordinator.addFunction(
                                                StringArgumentType.getString(ctx, CommonLang.Argument.NAME),
                                                FunctionParametersArgumentType.getFunctionParams(ctx, CommonLang.Argument.PARAMETERS),
                                                StringArgumentType.getString(ctx, CommonLang.Argument.EXPRESSION),
                                                ctx
                                        ))
                                )
                        )
                );

        @NonNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal(CommonLang.Literal.REMOVE)
                .then(CommandManager.argument(CommonLang.Argument.NAME, StringArgumentType.word())
                        .executes(ctx -> FunctionCoordinator.removeFunction(StringArgumentType.getString(ctx, CommonLang.Argument.NAME), ctx))
                );

        return CommandManager.literal(I18n.translate("commands.dtdm.function.literal"))
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);
    }
}
