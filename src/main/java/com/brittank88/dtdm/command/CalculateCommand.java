package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.expression.ExpressionUtils;
import com.brittank88.dtdm.util.lang.LangUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class CalculateCommand {

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {

        @NonNull LiteralArgumentBuilder<ServerCommandSource> calculateCommand = CommandManager.literal(I18n.translate("commands.dtdm.calculate.literal"))
                .then(CommandManager.argument(LangUtils.CommonLang.Argument.EXPRESSION, StringArgumentType.string())
                        .executes(ExpressionUtils.CalculationTools::sendCalculation)
                        .then(CommandManager.argument(LangUtils.CommonLang.Argument.TARGET, EntityArgumentType.players())
                                .executes(ExpressionUtils.CalculationTools::sendCalculation)
                        )
                );

        return calculateCommand;
    }
}
