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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * An argument type for function parameters.
 *
 * @see ArgumentType
 * @author Brittank88
 */
public class FunctionParametersArgumentType implements ArgumentType<String[]> {

    /** Braces that we expect provided function parameters to be surrounded by. **/
    private static final Pair<Character, Character> BRACES = new Pair<>('(', ')');

    /**
     * Surrounds a supplied string with braces provided by {@link FunctionParametersArgumentType#BRACES} and returns the result.
     *
     * @param str The string to surround.
     * @return The string surrounded by braces.
     */
    private static @NotNull String appendBraces(String str) { return BRACES.getLeft() + str + BRACES.getRight(); }

    /** A list of examples of valid parameter inputs. **/
    private @NonNls static final List<String> EXAMPLES = Stream.of(
            "x",                                        // Single single-letter parameter.
            "x,y,z", "x, y, z",                         // Multiple single-letter parameters separated by commas.
            "x;y;z", "x; y; z",                         // Multiple single-letter parameters separated by semicolons.
            "p1",                                       // Single multi-char (alphanumeric) parameter.
            "p1,p2,p3", "p1, p2, p3",                   // Multiple multi-char (alphanumeric) parameters separated by commas.
            "p1;p2;p3", "p1; p2; p3",                   // Multiple multi-char (alphanumeric) parameters separated by semicolons.
            "var_1",                                    // Single multi-char (alphanumeric) parameter with underscore.
            "var_1,var_2,var_3", "var_1, var_2, var_3", // Multiple multi-char (alphanumeric) parameters with underscores separated by commas.
            "var_1;var_2;var_3", "var_1; var_2; var_3"  // Multiple multi-char (alphanumeric) parameters with underscores separated by semicolons.
    ).map(FunctionParametersArgumentType::appendBraces).toList();

    /** The {@link CommandSyntaxException} thrown when the argument parsing fails. **/
    private static final SimpleCommandExceptionType INVALID_PARAM_EXCEPTION = new SimpleCommandExceptionType(Text.of(I18n.translate("message.error.function.invalid_parameters")));

    /**
     * Returns a new instance of {@link FunctionParametersArgumentType this class}.
     *
     * @return A new instance of {@link FunctionParametersArgumentType this class}.
     */
    public static @NotNull FunctionParametersArgumentType functionParameters() { return new FunctionParametersArgumentType(); }

    /**
     * Uses the provided {@link CommandContext<ServerCommandSource>} to parse the argument defined by the supplied {@link String name},
     * and return the {@link ArrayList<String> results}.
     *
     * @param ctx The {@link CommandContext<ServerCommandSource>} to use to parse the argument.
     * @param name The name of the argument to parse.
     * @return The {@link ArrayList<String> results} of the argument parsing.
     */
    public static @NotNull String @NotNull [] getFunctionParameters(CommandContext<ServerCommandSource> ctx, String name) { return ctx.getArgument(name, String[].class); }

    /**
     * Uses the supplied {@link StringReader} defined on the argument {@link String} to produce a list of function parameters, if possible.
     *
     * @param reader The {@link StringReader} to use, defined on the argument {@link String}.
     * @return A {@link List<String>} of {@link String function parameters}.
     * @throws CommandSyntaxException If the parse failed.
     */
    @Override public @NotNull String @NotNull [] parse(@NotNull StringReader reader) throws CommandSyntaxException {

        // Ensure we can read forward, and that the opening brace is present.
        if (!reader.canRead() || reader.read() != BRACES.getLeft()) throw INVALID_PARAM_EXCEPTION.create();

        // Read the parameters.
        List<String> params = new ArrayList<>();
        StringBuilder currentParam = new StringBuilder();
        while (reader.canRead() && reader.peek() != BRACES.getRight()) {

            // Read the next character.
            char c = reader.read();

            // If we reach a separator, add the current parameter to the list and start a new one.
            if (c == ',' || c == ';') {
                // Prevents empty parameters or consecutive separators.
                if (currentParam.isEmpty()) throw INVALID_PARAM_EXCEPTION.create();

                // Add the current parameter to the list.
                params.add(currentParam.toString());

                // Clear the StringBuilder.
                currentParam = new StringBuilder();

                // If there is are spaces after the comma, skip them.
                while (reader.canRead() && reader.peek() == ' ') reader.skip();
            }
            // Otherwise, append the character to the current parameter if it is a letter, digit, or underscore.
            else if (Character.isLetterOrDigit(c) || c == '_') currentParam.append(c);
            // Skip any space characters.
            else if (Character.isSpaceChar(c)) reader.skip();
            // Otherwise, throw an exception.
            else throw INVALID_PARAM_EXCEPTION.create();
        }

        // Ensure we can read forward, and that the closing brace is present.
        // This is still required as the while loop may have existed as a result of reader#canRead() returning false.
        if (!reader.canRead() || reader.read() != BRACES.getRight()) throw INVALID_PARAM_EXCEPTION.create();

        // Return the parameters.
        return params.toArray(String[]::new);
    }

    /**
     * Provides examples of {@link String valid parameter inputs}.
     *
     * @return A {@link Collection<String>} of examples of {@link String valid parameter inputs}.
     */
    @Override public Collection<String> getExamples() { return EXAMPLES; }
}
