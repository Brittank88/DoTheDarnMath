package com.brittank88.dtdm.register;

import com.brittank88.dtdm.client.DTDMClient;
import com.brittank88.dtdm.command.root.DTDMCommand;
import com.brittank88.dtdm.util.command.argument_type.FunctionParametersArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public abstract class CommandRegistry {

    public static void register() {
        ArgumentTypes.register(
                DTDMClient.MOD_ID + ":function_parameter", //NON-NLS
                FunctionParametersArgumentType.class,
                new ConstantArgumentSerializer<>(FunctionParametersArgumentType::functionParameters)
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(DTDMCommand.build()));

        DTDMClient.LOGGER.info(I18n.translate("logger.info.initialisation.command_registry"));
    }
}
