package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.expression.ExpressionUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;


public abstract class CalculateCommand {

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> build() {

        /* Root for all calculation-related commands. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> calculateCommandRoot = CommandManager.literal(I18n.translate("commands.dtdm.calculate.literal"))
                .then(CommandManager.argument(I18n.translate("commands.generic.argument.expression"), StringArgumentType.string())
                        .executes(ExpressionUtils.CalculationTools::sendCalculation)
                        .then(CommandManager.argument(I18n.translate("commands.generic.argument.target"), EntityArgumentType.players())
                                .executes(ExpressionUtils.CalculationTools::sendCalculation)
                        )
                );

        return calculateCommandRoot;
    }
}
