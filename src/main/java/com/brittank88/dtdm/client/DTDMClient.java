package com.brittank88.dtdm.client;

import com.brittank88.dtdm.register.CommandRegistry;
import com.brittank88.dtdm.register.SuggestionSupplierRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class DTDMClient implements ClientModInitializer {

    public static final String MOD_ID = "dtdm"; //NON-NLS
    public static final Logger LOGGER = LogManager.getLogger(new TranslatableText("mod.displayName").asString());

    @Override public void onInitializeClient() {
        CommandRegistry.register();
        SuggestionSupplierRegistry.register();
        LOGGER.info(new TranslatableText("logger.info.initialised").asString());
    }
}
