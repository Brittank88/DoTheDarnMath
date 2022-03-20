package com.brittank88.dtdm.command.root;

import com.brittank88.dtdm.command.BooleanCommand;
import com.brittank88.dtdm.command.CalculateCommand;
import com.brittank88.dtdm.command.ConstantCommand;
import com.brittank88.dtdm.command.FunctionCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;


public abstract class DTDMCommand {

    // TODO: mXparser.consolePrintHelp() support.
    // TODO: Support for org.mariuszgromada.math.mxparser.mathcollection.SpecialValueTrigonometric.

    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> build() {
        return CommandManager.literal("dtdm") //NON-NLS
                .then(CalculateCommand.build())
                .then(ConstantCommand.build())
                .then(FunctionCommand.build())
                .then(BooleanCommand.build());
    }
}
