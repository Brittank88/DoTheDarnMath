package com.brittank88.dtdm.command.register;

import com.brittank88.dtdm.command.CalculateCommand;
import com.brittank88.dtdm.command.ConstantCommand;
import com.brittank88.dtdm.command.FunctionCommand;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public abstract class CommandRegistrar {

    // TODO: Add support for viewing truth tables in org.mariuszgromada.math.mxparser.mathcollection.BooleanAlgebra.
    // TODO: Support for org.mariuszgromada.math.mxparser.mathcollection.SpecialValuesTrigonometric.

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("dtdm")
                    .then(CalculateCommand.build())
                    .then(ConstantCommand.build())
                    .then(FunctionCommand.build())
            );
        });
    }
}
