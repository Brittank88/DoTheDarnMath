package com.brittank88.dtdm.command.root;

import com.brittank88.dtdm.command.CalculateCommand;
import com.brittank88.dtdm.command.ConstantCommand;
import com.brittank88.dtdm.command.FunctionCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class DTDMCommand {

    // TODO: Add support for viewing truth tables in org.mariuszgromada.math.mxparser.mathcollection.BooleanAlgebra.
    // TODO: Support for org.mariuszgromada.math.mxparser.mathcollection.SpecialValuesTrigonometric.

    public static @NonNull LiteralArgumentBuilder<ServerCommandSource> build() {
        return CommandManager.literal("dtdm")
                .then(CalculateCommand.build())
                .then(ConstantCommand.build())
                .then(FunctionCommand.build());
    }
}
