package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.command.UniversalSuggestionProvider;
import com.brittank88.dtdm.util.constant.ConstantCategory;
import com.brittank88.dtdm.util.constant.ConstantCoordinator;
import com.brittank88.dtdm.util.constant.ConstantUtils;
import com.brittank88.dtdm.util.lang.CommonLang;
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
import java.util.stream.Collectors;

public abstract class ConstantCommand {

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> build() {

        @NotNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal(CommonLang.Literal.GET);
        for (Map.Entry<ConstantCategory, Collection<Constant>> entry : ConstantCoordinator.CONSTANTS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument(CommonLang.Argument.NAME, StringArgumentType.word())
                            .suggests(new UniversalSuggestionProvider<>(ignored -> entry.getValue().stream()
                                    .map(Constant::getConstantName)
                                    .collect(Collectors.toList())
                            )).executes(ctx -> ConstantUtils.CommandTools.sendConstant(ctx, entry.getValue()))
                    )
            );
        }

        // TODO: Support expressions that evaluate to a double for the value argument.

        @NotNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal(CommonLang.Literal.ADD)
                .then(CommandManager.argument(CommonLang.Argument.NAME, StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> SuggestionUtils.suggestionFromIntOffset(
                                I18n.translate("commands.dtdm.constant.add.argument.name.suggest_prefix"),
                                ConstantCoordinator.getUserConstants().size(), 3
                        ))).then(CommandManager.argument(CommonLang.Argument.VALUE, DoubleArgumentType.doubleArg())
                                .executes(ctx -> ConstantCoordinator.addConstant(
                                        StringArgumentType.getString(ctx, CommonLang.Argument.NAME),
                                        DoubleArgumentType.getDouble(ctx, CommonLang.Argument.VALUE),
                                        ctx
                                ))
                        )
                );

        @NotNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal(CommonLang.Literal.REMOVE)
                .then(CommandManager.argument(CommonLang.Argument.NAME, StringArgumentType.word())
                        .executes(ctx -> ConstantCoordinator.removeConstant(StringArgumentType.getString(ctx, CommonLang.Argument.NAME), ctx))
                );

        return CommandManager.literal(I18n.translate("commands.dtdm.constant.literal"))
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);
    }
}
