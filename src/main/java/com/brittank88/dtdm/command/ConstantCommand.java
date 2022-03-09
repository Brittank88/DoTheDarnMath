package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.command.UniversalSuggestionProvider;
import com.brittank88.dtdm.util.constant.ConstantCategory;
import com.brittank88.dtdm.util.constant.ConstantCoordinator;
import com.brittank88.dtdm.util.constant.ConstantUtils;
import com.brittank88.dtdm.util.suggestion.SuggestionUtils;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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

        @NonNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal("get");
        for (Map.Entry<ConstantCategory, Collection<Constant>> entry : ConstantCoordinator.CONSTANTS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument("name", StringArgumentType.word())
                            .suggests(new UniversalSuggestionProvider<>(ignored -> entry.getValue().stream()
                                    .map(Constant::getConstantName)
                                    .collect(Collectors.toList())
                            )).executes(ctx -> ConstantUtils.CommandTools.sendConstant(StringArgumentType.getString(ctx, "name"), entry.getValue(), ctx))
                    )
            );
        }

        @NonNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal("add")
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> SuggestionUtils.suggestionFromIntOffset("C", ConstantCoordinator.getUserConstants().size(), 3)))
                        .then(CommandManager.argument("constant", DoubleArgumentType.doubleArg())
                                .executes(ctx -> ConstantCoordinator.addConstant(StringArgumentType.getString(ctx, "name"), DoubleArgumentType.getDouble(ctx, "constant"), ctx))
                        )
                );

        @NonNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal("remove")
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .executes(ctx -> ConstantCoordinator.removeConstant(StringArgumentType.getString(ctx, "name"), ctx))
                );

        return CommandManager.literal("constant")
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);
    }
}
