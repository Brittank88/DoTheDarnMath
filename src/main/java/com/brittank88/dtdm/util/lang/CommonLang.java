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

        public static final String
                MOD_NAME = I18n.translate("mod.displayName");
        public static final String ADD      = I18n.translate("commands.generic.literal.add");
        public static final String REMOVE   = I18n.translate("commands.generic.literal.remove");
        public static final String GET      = I18n.translate("commands.generic.literal.get");
    }

    public abstract static class Argument {

        private static void init() { DTDMClient.LOGGER.info(I18n.translate("logger.info.initialisation.common_lang.argument")); }

        public static final String
                NAME       = I18n.translate("commands.generic.argument.name");
        public static final String VALUE      = I18n.translate("commands.generic.argument.value");
        public static final String PARAMETERS = I18n.translate("commands.generic.argument.parameters");
        public static final String EXPRESSION = I18n.translate("commands.generic.argument.expression");
        public static final String TARGET     = I18n.translate("commands.generic.argument.target");
    }
}
