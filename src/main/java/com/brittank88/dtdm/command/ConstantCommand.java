package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.command.UniversalSuggestionProvider;
import com.brittank88.dtdm.util.constant.ConstantCategory;
import com.brittank88.dtdm.util.constant.ConstantCoordinator;
import com.brittank88.dtdm.util.constant.ConstantUtils;
import com.brittank88.dtdm.util.suggestion.SuggestionUtils;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import org.mariuszgromada.math.mxparser.Constant;

import java.util.Collection;
import java.util.Map;

public abstract class ConstantCommand {

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> build() {

        /* Command for getting the value of a default or user-defined constant. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal(I18n.translate("commands.generic.literal.get"));
        for (final Map.Entry<ConstantCategory, Collection<Constant>> entry : ConstantCoordinator.CONSTANTS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument(I18n.translate("commands.generic.argument.name"), StringArgumentType.word())
                            .suggests(new UniversalSuggestionProvider<>(() -> entry.getValue().stream().map(Constant::getConstantName).toList()))
                            .executes(ctx -> ConstantUtils.CommandTools.sendConstant(ctx, entry.getValue()))
                    )
            );
        }

        // TODO: Support expressions that evaluate to a double as the value argument.

        /* Command for adding a new user-defined constant, or setting the value of an existing user-defined constant. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal(I18n.translate("commands.generic.literal.add"))
                .then(CommandManager.argument(I18n.translate("commands.generic.argument.name"), StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(() -> SuggestionUtils.suggestionFromIntOffset(
                                "C", ConstantCoordinator.getUserConstants().size(), 3 // NON-NLS
                        ))).then(CommandManager.argument(I18n.translate("commands.generic.argument.value"), DoubleArgumentType.doubleArg())
                                .executes(ctx -> ConstantCoordinator.addConstant(
                                        StringArgumentType.getString(ctx, I18n.translate("commands.generic.argument.name")),
                                        DoubleArgumentType.getDouble(ctx, I18n.translate("commands.generic.argument.value")),
                                        ctx
                                ))
                        )
                );

        /* Command for removing a user-defined constant. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal(I18n.translate("commands.generic.literal.remove"))
                .then(CommandManager.argument(I18n.translate("commands.generic.argument.name"), StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(() -> ConstantCoordinator.getUserConstants().stream().map(Constant::getConstantName).toList()))
                        .executes(ctx -> ConstantCoordinator.removeConstant(StringArgumentType.getString(ctx, I18n.translate("commands.generic.argument.name")), ctx))
                );

        /* Root for all constant-related commands. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> constantCommandRoot = CommandManager.literal(I18n.translate("commands.dtdm.constant.literal"))
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);

        return constantCommandRoot;
    }
}
