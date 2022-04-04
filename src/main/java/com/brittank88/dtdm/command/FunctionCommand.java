package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.command.UniversalSuggestionProvider;
import com.brittank88.dtdm.util.command.argument_type.FunctionParametersArgumentType;
import com.brittank88.dtdm.util.function.FunctionCategory;
import com.brittank88.dtdm.util.function.FunctionCoordinator;
import com.brittank88.dtdm.util.function.FunctionUtils;
import com.brittank88.dtdm.util.suggestion.SuggestionUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import org.mariuszgromada.math.mxparser.Function;

import java.util.Collection;
import java.util.Map;

public abstract class FunctionCommand {

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> build() {

        // TODO: Support optional function description.

        // TODO: Prevent hanging on large recursive calculations.

        /* Command for getting the definition, body and metadata of a default or user-defined function. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal(I18n.translate("commands.generic.literal.get"));
        for (Map.Entry<FunctionCategory, Collection<Function>> entry : FunctionCoordinator.FUNCTIONS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument(I18n.translate("commands.generic.argument.name"), StringArgumentType.word())
                            .suggests(new UniversalSuggestionProvider<>(() -> entry.getValue().stream().map(Function::getFunctionName).toList()))
                            .executes(ctx -> FunctionUtils.CommandTools.sendFunction(ctx, entry.getValue()))
                    )
            );
        }

        /* Command for defining a new user-defined function. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal(I18n.translate("commands.generic.literal.add"))
                .then(CommandManager.argument(I18n.translate("commands.generic.argument.name"), StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> SuggestionUtils.suggestionFromIntOffset(
                                "f", FunctionCoordinator.getUserFunctions().size(), 3)   // NON-NLS
                        )).then(CommandManager.argument(I18n.translate("commands.generic.argument.parameters"), FunctionParametersArgumentType.functionParameters())
                                .then(CommandManager.argument(I18n.translate("commands.generic.argument.expression"), StringArgumentType.greedyString())
                                        .executes(ctx -> FunctionCoordinator.addFunction(
                                                StringArgumentType.getString(ctx, I18n.translate("commands.generic.argument.name")),
                                                FunctionParametersArgumentType.getFunctionParameters(ctx, I18n.translate("commands.generic.argument.parameters")),
                                                StringArgumentType.getString(ctx, I18n.translate("commands.generic.argument.expression")),
                                                ctx
                                        ))
                                )
                        )
                );

        /* Command for removing a user-defined function. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal(I18n.translate("commands.generic.literal.remove"))
                .then(CommandManager.argument(I18n.translate("commands.generic.argument.name"), StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(() -> FunctionCoordinator.getUserFunctions().stream().map(Function::getFunctionName).toList()))
                        .executes(ctx -> FunctionCoordinator.removeFunction(StringArgumentType.getString(ctx, I18n.translate("commands.generic.argument.name")), ctx))
                );

        /* Root for all function-related commands. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> functionCommandRoot = CommandManager.literal(I18n.translate("commands.dtdm.function.literal"))
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);

        return functionCommandRoot;
    }
}
