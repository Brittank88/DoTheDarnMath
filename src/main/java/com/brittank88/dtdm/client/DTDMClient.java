package com.brittank88.dtdm.client;

import com.brittank88.dtdm.register.CommandRegistry;
import com.brittank88.dtdm.register.SuggestionSupplierRegistry;
import com.brittank88.dtdm.util.exception.RegistrationFailedException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NonNls;

@Environment(EnvType.CLIENT)
public @NonNls class DTDMClient implements ClientModInitializer {

    // TODO: Hack in complex numbers support???
    // TODO: Extra support for unicode math symbols?
    // TODO: Make all possible function parameters final.
    // TODO: Unit conversion commands.

    public static final String MOD_ID = "dtdm";
    public static final Logger LOGGER = LogManager.getLogger("DTDM");

    @Override public void onInitializeClient() {

        try {
            CommandRegistry.register();
            SuggestionSupplierRegistry.register();
        } catch (RegistrationFailedException e) { LOGGER.error("Registration '" + e.getRegistryType().name() + "' failed:", e); }

        LOGGER.info("(DTDMClient) Initialisation complete!");
    }
}
