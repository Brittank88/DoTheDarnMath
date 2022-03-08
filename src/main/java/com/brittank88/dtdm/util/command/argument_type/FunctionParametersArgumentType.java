package com.brittank88.dtdm.util.command.argument_type;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// TODO: Support multi-char parameter names.
public class FunctionParametersArgumentType implements ArgumentType<Character[]> {

    // TODO: Convert everything else to use TranslatableText too.
    private static final SimpleCommandExceptionType INVALID_FUNCTION_PARAM_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("arguments.function_param.invalid"));

    public static FunctionParametersArgumentType functionParameters() { return new FunctionParametersArgumentType(); }

    public static <S> Character[] getFunctionParams(CommandContext<S> context, String name) { return context.getArgument(name, Character[].class); }

    @Override
    public Character[] parse(StringReader reader) throws CommandSyntaxException {

        // We must first read an opening curly brace.
        if (reader.read() != '{') throw INVALID_FUNCTION_PARAM_EXCEPTION.create();
        else {
            // This list holds the function parameters.
            List<Character> chars = new ArrayList<>();

            // We note the position of the cursor now that we have read the opening curly brace.
            int start = reader.getCursor();

            // We must then be able to locate a closing curly brace to match the opening one.
            // If the closing curly brace is directly after the opening one, just return an empty array.
            try { if (reader.readStringUntil('}').isEmpty()) return new Character[0]; }
            catch (CommandSyntaxException e) { throw INVALID_FUNCTION_PARAM_EXCEPTION.create(); }

            // We rewind back to the beginning of the substring contained within the curly braces.
            reader.setCursor(start);

            // We then read each character of this substring.
            while (reader.canRead() && reader.peek() != ' ') {

                // Read the character.
                char cRead = reader.read();

                // We check the current character to see if it is a comma or the closing curly brace, and if it is, we skip it.
                if (cRead == ',' || cRead == '}') continue;

                // Ensure it is a new alphabetic character.
                if (chars.contains(cRead) || !Character.isAlphabetic(cRead)) throw INVALID_FUNCTION_PARAM_EXCEPTION.create();

                // We then add the character to our list of characters.
                chars.add(cRead);
            }

            return chars.toArray(new Character[0]);
        }
    }

    @Override
    public Collection<String> getExamples() { return Arrays.asList("{x,y,z}", "x"); }
}
