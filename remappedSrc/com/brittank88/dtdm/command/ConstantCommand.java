package com.brittank88.dtdm.command;

import com.brittank88.dtdm.handler.ConstantHandler;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Constant;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ConstantCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        @NonNull LiteralArgumentBuilder<ServerCommandSource> getCommand = CommandManager.literal("get");
        for (Map.Entry<ConstantHandler.CONSTANT_CATEGORY, Collection<Constant>> entry : ConstantHandler.CONSTANTS.entrySet()) {
            getCommand.then(CommandManager.literal(entry.getKey().name())
                    .then(CommandManager.argument("name", StringArgumentType.string())
                            .suggests(new UniversalSuggestionProvider<>(ignored -> entry.getValue().stream()
                                    .map(Constant::getConstantName)
                                    .collect(Collectors.toList())
                            )).executes(ctx -> ConstantHandler.sendConstant(StringArgumentType.getString(ctx, "name"), entry.getValue(), ctx))
                    )
            );
        }

        @NonNull LiteralArgumentBuilder<ServerCommandSource> addCommand = CommandManager.literal("add")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests(new UniversalSuggestionProvider<>(ignored -> Collections.singletonList("C" + ConstantHandler.getUserConstants().size())))
                        .then(CommandManager.argument("constant", DoubleArgumentType.doubleArg())
                                .executes(ctx -> ConstantHandler.addConstant(StringArgumentType.getString(ctx, "name"), DoubleArgumentType.getDouble(ctx, "constant"), ctx))
                        )
                );

        @NonNull LiteralArgumentBuilder<ServerCommandSource> removeCommand = CommandManager.literal("remove")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(ctx -> ConstantHandler.removeConstant(StringArgumentType.getString(ctx, "name"), ctx))
                );

        return CommandManager.literal("constant")
                .then(getCommand)
                .then(addCommand)
                .then(removeCommand);
    }
}
