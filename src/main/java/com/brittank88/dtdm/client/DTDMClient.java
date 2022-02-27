package com.brittank88.dtdm.client;

import com.brittank88.dtdm.DTDM;
import com.brittank88.dtdm.command.DTDMCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.mariuszgromada.math.mxparser.mXparser;

@Environment(EnvType.CLIENT)
public class DTDMClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DTDMCommand.register();
        DTDM.LOGGER.info("DTDM client has been initialized!");
    }
}
