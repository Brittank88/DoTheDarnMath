package com.brittank88.dtdm.register;

import com.brittank88.dtdm.client.DTDMClient;
import com.brittank88.dtdm.command.root.DTDMCommand;
import com.brittank88.dtdm.util.command.argument_type.FunctionParametersArgumentType;
import com.brittank88.dtdm.util.exception.RegistrationFailedException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import org.jetbrains.annotations.NonNls;

public @NonNls abstract class CommandRegistry {

    public static void register() throws RegistrationFailedException {

        try {
            // Register the command argument types.
            ArgumentTypes.register(
                    DTDMClient.MOD_ID + ":function_parameter",
                    FunctionParametersArgumentType.class,
                    new ConstantArgumentSerializer<>(FunctionParametersArgumentType::functionParameters)
            );

            // Register the commands.
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(DTDMCommand.build()));
        } catch (Exception e) { throw new RegistrationFailedException(e, RegistrationFailedException.REGISTRY_TYPE.COMMAND); }

        DTDMClient.LOGGER.info("(CommandRegistry) Registration complete!");
    }
}
