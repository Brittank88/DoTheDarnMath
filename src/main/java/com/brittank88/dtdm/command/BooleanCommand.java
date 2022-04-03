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

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> build() {

        /* Map of all boolean truth tables, keyed by name. */
        final ImmutableMap<String, ImmutableMap<Double, ImmutableMap<Double, Double>>> truthTables = BooleanUtils.TruthTable.ClassTools.getTruthTables();

        /* Commands related to truth tables. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> truthTableCommand = CommandManager.literal(I18n.translate("commands.dtdm.boolean.truthTable"))
                .then(CommandManager.argument(I18n.translate("commands.generic.argument.name"), StringArgumentType.word())
                        .suggests(new UniversalSuggestionProvider<>(truthTables::keySet))
                        .executes(ctx -> BooleanUtils.TruthTable.CommandTools.sendTruthTable(ctx, truthTables))
                );

        /* Root for all boolean-related commands. */
        final @NotNull LiteralArgumentBuilder<ServerCommandSource> booleanCommandRoot = CommandManager.literal(I18n.translate("commands.dtdm.boolean.literal"))
                .then(truthTableCommand);

        return booleanCommandRoot;
    }
}
