package com.brittank88.dtdm.client;

import com.brittank88.dtdm.register.CommandRegistry;
import com.brittank88.dtdm.register.SuggestionSupplierRegistry;
import com.brittank88.dtdm.util.lang.LangUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class DTDMClient implements ClientModInitializer {

    // TODO: Hack in complex numbers support???

    public static final String MOD_ID = "dtdm"; //NON-NLS
    public static final Logger LOGGER = LogManager.getLogger(LangUtils.CommonLang.Literal.MOD_NAME);

    @Override public void onInitializeClient() {
        CommandRegistry.register();
        SuggestionSupplierRegistry.register();
        LOGGER.info(I18n.translate("logger.info.initialisation.complete"));
    }
}
