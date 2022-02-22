package com.brittank88.dtdm;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DTDM implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("DTDM");

    @Override
    public void onInitialize() { LOGGER.info("DTDM has been initialized!"); }
}
