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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Constant;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ConstantCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        // FIXME: Constants cannot be referenced in calculation.

        // TODO: Migrate to common lang class.
        String nameString = I18n.translate("commands.generic.argument.name"),
                valueString = I18n.translate("commands.generic.argument.value"),
                addString = I18n.translate("commands.generic.literal.add"),
                removeString = I18n.translate("commands.generic.literal.remove"),
                getString = I18n.translate("commands.generic.literal.get");

        @NonNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal(getString);
        for (Map.Entry<ConstantCategory, Collection<Constant>> entry : ConstantCoordinator.CONSTANTS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument(nameString, StringArgumentType.word())
                            .suggests(new UniversalSuggestionProvider<>(ignored -> entry.getValue().stream()
                                    .map(Constant::getConstantName)
                                    .collect(Collectors.toList())
                            )).executes(ctx -> ConstantUtils.CommandTools.sendConstant(StringArgumentType.getString(ctx, nameString), entry.getValue(), ctx))
                    )
            );
        }

        @NonNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal(addString)
                .then(CommandManager.argument(nameString, StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> SuggestionUtils.suggestionFromIntOffset(
                                I18n.translate("commands.dtdm.constant.add.argument.name.suggestPrefix"),
                                ConstantCoordinator.getUserConstants().size(), 3
                        ))).then(CommandManager.argument(valueString, DoubleArgumentType.doubleArg())
                                .executes(ctx -> ConstantCoordinator.addConstant(StringArgumentType.getString(ctx, nameString), DoubleArgumentType.getDouble(ctx, valueString), ctx))
                        )
                );

        @NonNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal(removeString)
                .then(CommandManager.argument(nameString, StringArgumentType.word())
                        .executes(ctx -> ConstantCoordinator.removeConstant(StringArgumentType.getString(ctx, nameString), ctx))
                );

        return CommandManager.literal(I18n.translate("commands.dtdm.constant.literal"))
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);
    }
}
