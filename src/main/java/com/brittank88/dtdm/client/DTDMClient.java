package com.brittank88.dtdm.client;

import com.brittank88.dtdm.register.CommandRegistry;
import com.brittank88.dtdm.register.SuggestionSupplierRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("HardCodedStringLiteral")
@Environment(EnvType.CLIENT)
public class DTDMClient implements ClientModInitializer {

    // TODO: Hack in complex numbers support???
    // TODO: Extra support for unicode math symbols?

    public static final String MOD_ID = "dtdm";
    public static final Logger LOGGER = LogManager.getLogger("DTDM");

    @Override public void onInitializeClient() {
        CommandRegistry.register();
        SuggestionSupplierRegistry.register();
        LOGGER.info("(DTDMClient) Initialisation complete!");
    }
}
