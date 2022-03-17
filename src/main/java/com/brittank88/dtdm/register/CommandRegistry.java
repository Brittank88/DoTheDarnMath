package com.brittank88.dtdm.register;

import com.brittank88.dtdm.client.DTDMClient;
import com.brittank88.dtdm.command.root.DTDMCommand;
import com.brittank88.dtdm.util.command.argument_type.FunctionParametersArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

@SuppressWarnings("HardCodedStringLiteral")
public abstract class CommandRegistry {

    public static void register() {
        ArgumentTypes.register(
                DTDMClient.MOD_ID + ":function_parameter",
                FunctionParametersArgumentType.class,
                new ConstantArgumentSerializer<>(FunctionParametersArgumentType::functionParameters)
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(DTDMCommand.build()));

        DTDMClient.LOGGER.info("(CommandRegistry) Registration complete!");
    }
}
