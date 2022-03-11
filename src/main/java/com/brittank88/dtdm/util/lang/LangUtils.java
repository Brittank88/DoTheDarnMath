package com.brittank88.dtdm.util.lang;

import net.minecraft.client.resource.language.I18n;

public abstract class LangUtils {

    public abstract static class CommonLang {

        public abstract static class Argument {

            public static String
                    NAME       = I18n.translate("commands.generic.argument.name"),
                    VALUE      = I18n.translate("commands.generic.argument.value"),
                    PARAMETERS = I18n.translate("commands.generic.argument.parameters"),
                    EXPRESSION = I18n.translate("commands.generic.argument.expression"),
                    TARGET = I18n.translate("commands.generic.argument.target");
        }

        public abstract static class Literal {

            public static String
                    ADD    = I18n.translate("commands.generic.literal.add"),
                    REMOVE = I18n.translate("commands.generic.literal.remove"),
                    GET    = I18n.translate("commands.generic.literal.get");
        }
    }
}
