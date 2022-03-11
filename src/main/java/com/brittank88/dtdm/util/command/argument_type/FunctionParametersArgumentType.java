package com.brittank88.dtdm.util.command.argument_type;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

// TODO: Support multi-char parameter names.

/**
 * An argument type for function parameters.
 *
 * @see ArgumentType
 * @author Brittank88
 */
public class FunctionParametersArgumentType implements ArgumentType<Character[]> {

    private static final Pair<Character, Character> BRACES = new Pair<>('(', ')');
    private static @NotNull String appendBraces(String str) { return BRACES.getLeft() + str + BRACES.getRight(); }

    private static final SimpleCommandExceptionType INVALID_PARAM_EXCEPTION = new SimpleCommandExceptionType(Text.of(I18n.translate("message.error.custom_param.generic.invalid")));

    public static @NotNull FunctionParametersArgumentType functionParameters() { return new FunctionParametersArgumentType(); }

    public static Character[] getFunctionParams(CommandContext<ServerCommandSource> ctx, String name) { return ctx.getArgument(name, Character[].class); }

    @Override
    public Character @NotNull [] parse(@NotNull StringReader reader) throws CommandSyntaxException {

        if (!reader.canRead() || reader.read() != BRACES.getLeft()) throw INVALID_PARAM_EXCEPTION.create();
        else {
            // This list holds the function parameters.
            List<Character> chars = new ArrayList<>();

            // We note the position of the cursor now that we have read the opening curly brace.
            int start = reader.getCursor();

            // We must then be able to locate a closing curly brace to match the opening one.
            // If the closing curly brace is directly after the opening one, just return an empty array.
            try { if (reader.readStringUntil(BRACES.getRight()).isEmpty()) return new Character[0]; }
            catch (CommandSyntaxException e) { throw INVALID_PARAM_EXCEPTION.create(); }

            // We rewind back to the beginning of the substring contained within the curly braces.
            reader.setCursor(start);

            // We then read each character of this substring.
            while (reader.canRead() && reader.peek() != ' ') {

                // Read the character.
                char cRead = reader.read();

                // We check the current character to see if it is a comma or the closing curly brace, and if it is, we skip it.
                if (cRead == ',' || cRead == BRACES.getRight()) continue;

                // Ensure it is a new alphabetic character.
                if (chars.contains(cRead) || !Character.isAlphabetic(cRead)) throw INVALID_PARAM_EXCEPTION.create();

                // We then add the character to our list of characters.
                chars.add(cRead);
            }

            return chars.toArray(new Character[0]);
        }
    }

    @Override
    public Collection<String> getExamples() { return Stream.of("x,y,z", "x").map(FunctionParametersArgumentType::appendBraces).toList(); } //NON-NLS
}
