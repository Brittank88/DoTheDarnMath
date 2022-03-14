package com.brittank88.dtdm.util.lang;

import com.brittank88.dtdm.client.DTDMClient;
import net.minecraft.client.resource.language.I18n;

public abstract class CommonLang {

    public static void init() {
        Literal.init();
        Argument.init();
        DTDMClient.LOGGER.info(I18n.translate("logger.info.initialisation.common_lang_registry"));
    }

    public abstract static class Literal {

        private static void init() { DTDMClient.LOGGER.info(I18n.translate("logger.info.initialisation.common_lang.literal")); }

        public static String
                MOD_NAME = I18n.translate("mod.displayName"),
                ADD      = I18n.translate("commands.generic.literal.add"),
                REMOVE   = I18n.translate("commands.generic.literal.remove"),
                GET      = I18n.translate("commands.generic.literal.get");
    }

    public abstract static class Argument {

        private static void init() { DTDMClient.LOGGER.info(I18n.translate("logger.info.initialisation.common_lang.argument")); }

        public static String
                NAME       = I18n.translate("commands.generic.argument.name"),
                VALUE      = I18n.translate("commands.generic.argument.value"),
                PARAMETERS = I18n.translate("commands.generic.argument.parameters"),
                EXPRESSION = I18n.translate("commands.generic.argument.expression"),
                TARGET     = I18n.translate("commands.generic.argument.target");
    }
}
