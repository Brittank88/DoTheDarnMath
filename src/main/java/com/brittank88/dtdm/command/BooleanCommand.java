package com.brittank88.dtdm.command;

import com.brittank88.dtdm.util.bool.BooleanUtils;
import com.brittank88.dtdm.util.command.UniversalSuggestionProvider;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

public abstract class BooleanCommand {

    // TODO: Add support for viewing truth tables in org.mariuszgromada.math.mxparser.mathcollection.BooleanAlgebra.

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> build() {

        ImmutableMap<String, ImmutableMap<Double, ImmutableMap<Double, Double>>> truthTables = BooleanUtils.TruthTable.ClassTools.getTruthTables();

        @NotNull LiteralArgumentBuilder<ServerCommandSource> truthTableCommand = CommandManager.literal(I18n.translate("commands.dtdm.boolean.truthTable"))
                .then(CommandManager.argument(I18n.translate("commands.generic.argument.name"), StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(truthTables::keySet))
                        .executes(ctx -> BooleanUtils.TruthTable.CommandTools.sendTruthTable(ctx, truthTables))
                );

        return CommandManager.literal(I18n.translate("commands.dtdm.boolean.literal"))
                .then(truthTableCommand);
    }
}
