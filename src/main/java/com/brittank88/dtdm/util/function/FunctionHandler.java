package com.brittank88.dtdm.util.function;

import com.brittank88.dtdm.util.constant.ConstantHandler;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.mathcollection.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class FunctionHandler {

    /** {@link ImmutableMap} of all {@link Function Functions}, keyed by {@link FunctionCategory}. **/
    public static final @NonNull ImmutableMap<@NonNull FunctionCategory, @NonNull Collection<@NonNull Function>> FUNCTIONS = ImmutableMap.ofEntries(
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.USER                     , new ArrayList<>()),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.BINARY_RELATIONS         , getFunctionsFromClasses(BinaryRelations.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.BOOLEAN_ALGEBRA          , getFunctionsFromClasses(BooleanAlgebra.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.CALCULUS                 , getFunctionsFromClasses(Calculus.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.EVALUATE                 , getFunctionsFromClasses(Evaluate.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.MATH                     , getFunctionsFromClasses(MathFunctions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.NUMBER_THEORY            , getFunctionsFromClasses(NumberTheory.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.PROBABILITY_DISTRIBUTIONS, getFunctionsFromClasses(ProbabilityDistributions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.SPECIAL                  , getFunctionsFromClasses(SpecialFunctions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.STATISTICS               , getFunctionsFromClasses(Statistics.class))
            // TODO: org.mariuszgromada.math.mxparser.mathcollection.PrimesCache ?
            // TODO: org.mariuszgromada.math.mxparser.mathcollection.SpecialValue ?
    );

    /**
     * Returns a {@link Collection<Function>} of all default {@link Function Functions}.
     *
     * @return A {@link Collection<Function>} of all default {@link Function Functions}.
     */
    public static @NonNull Collection<@NonNull Function> getAllDefaultFunctions() {
        return FUNCTIONS.entrySet().stream()
                .filter(e -> e.getKey().isDefault())
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns a {@link Collection<Function>} of all user-defined {@link Function Functions}.
     *
     * @return A {@link Collection<Function>} of user-defined {@link Function Functions}.
     */
    public static @NonNull Collection<@NonNull Function> getUserFunctions() { return Objects.requireNonNull(FUNCTIONS.get(FunctionCategory.USER)); }

    /**
     * Returns a {@link Collection<Function>} of all {@link Function Functions}, user-defined or not.
     *
     * @return A {@link Collection<Function>} of all {@link Function Functions}.
     */
    public static @NonNull Collection<@NonNull Function> getAllFunctions() { return FUNCTIONS.values().stream().flatMap(Collection::stream).collect(Collectors.toList()); }

    /**
     * Adds a {@link Function} to the {@link Collection<Function>} of user-defined {@link Function Functions}.
     * Warns if the user is overwriting a {@link Function} they defined earlier.
     *
     * @param name The {@link String name} of the {@link Function}.
     * @param parameters The {@link String parameters} of the {@link Function}.
     * @param expression The {@link String expression} of the {@link Function}.
     * @param ctx The {@link CommandContext<ServerCommandSource>} of the command (for sending feedback to the {@link ServerCommandSource command invoker}).
     * @return Status {@link Integer}.
     * @throws CommandException If the function {@link String name} or {@link String expression} is invalid, or the {@link String name} references a default {@link Function}.
     */
    public static @NonNull Integer addFunction(
            @NonNull String name,
            @NonNull Character[] parameters,
            @NonNull String expression,
            @NonNull CommandContext<ServerCommandSource> ctx
    ) throws CommandException {

        // Check that function name and expression are valid and don't already exist as a default function.
        if (name.isEmpty()) throw new CommandException(Text.of("Function name cannot be empty"));
        if (expression.isEmpty()) throw new CommandException(Text.of("Function expression cannot be empty"));
        if (name.contains(" ")) throw new CommandException(Text.of("Function name cannot contain spaces"));

        // Assemble the function, so we can check complete equality with default functions.
        String parameterString = Arrays.stream(parameters).map(Object::toString).collect(Collectors.joining(","));
        String functionDefinitionString = name + "(" + parameterString + ")=" + expression;
        Function function = new Function(functionDefinitionString);

        // Check that the function doesn't already exist as a default function.
        if (getAllDefaultFunctions().stream().anyMatch(f -> compare(f, function))) throw new CommandException(Text.of("Cannot override default function: " + name));

        // Warn if user-defined function is being overridden.
        String existingExpression = getUserFunctions().stream()
                .filter(c -> c.getFunctionName().equals(name)).findFirst()
                .map(Function::getFunctionExpressionString).orElse("");
        ctx.getSource().sendFeedback(Text.of(
                getUserFunctions().stream().anyMatch(c -> c.getFunctionName().equals(name)) && !existingExpression.isEmpty() ?
                        ("Overwrote function " + name + ": " + existingExpression + "->" + expression) :
                        ("Added function " + functionDefinitionString)
        ), false);

        ConstantHandler.getUserConstants().stream().filter(c -> function.getConstant(c.getConstantName()) == null).forEach(function::addConstants);
        FunctionHandler.getUserFunctions().stream().filter(f -> function.getFunction(f.getFunctionName()) == null).forEach(function::addFunctions); // Support function-in-function.
        getUserFunctions().add(function);

        // Return success.
        return 1;
    }

    /**
     * Compares two {@link Function}s by their {@link String name}, {@link String expression}, {@link Integer parameter count} and {@link String parameter names}.
     *
     * @param f The first {@link Function} of the comparison.
     * @param g The second {@link Function} of the comparison.
     * @return {@code true} if the {@link Function}s are equal, {@code false} otherwise.
     */
    public static boolean compare(Function f, Function g) {

        return f.getFunctionName().equals(g.getFunctionName())
                && f.getFunctionExpressionString().equals(g.getFunctionExpressionString())
                && f.getParametersNumber() == g.getParametersNumber()
                && IntStream.range(0, f.getParametersNumber())
                .mapToObj(i -> new Pair<>(f.getParameterName(i), g.getParameterName(i)))
                .allMatch(p -> p.getLeft().equals(p.getRight()));
    }

    /**
     * Removes a user-defined {@link Function}, given its {@link String name}.
     *
     * @param name The {@link String name} of the {@link Function} to remove.
     * @param ctx The {@link CommandContext<ServerCommandSource>} (used to send feedback to the {@link ServerCommandSource command invoker}).
     * @return Status {@link Integer}.
     * @throws CommandException If the {@link String name} is invalid or references a default {@link Function}.
     */
    public static @NonNull Integer removeFunction(@NonNull String name, @NonNull CommandContext<ServerCommandSource> ctx) throws CommandException {
        if (name.isEmpty()) throw new CommandException(Text.of("Function name cannot be empty"));
        if (name.contains(" ")) throw new CommandException(Text.of("Function name cannot contain spaces"));
        if (getAllDefaultFunctions().stream().anyMatch(f -> f.getFunctionName().equals(name))) throw new CommandException(Text.of("Cannot remove default function: " + name));

        getUserFunctions().removeIf(f -> f.getFunctionName().equals(name));

        ctx.getSource().sendFeedback(Text.of("Removed function " + name), false);

        return 1;
    }

    /**
     * Sends a {@link Function}'s {@link String name} and {@link Double value} to the {@link ServerCommandSource command invoker}.
     *
     * @param name The {@link String name} of the {@link Function}.
     * @param functions The {@link Collection<Function>} of {@link Function Functions}.
     * @param ctx The {@link CommandContext<ServerCommandSource>} of the command (for sending feedback to the {@link ServerCommandSource command invoker}).
     * @return Status {@link Integer}.
     * @throws CommandException If the {@link Function} could not be found.
     */
    public static @NonNull Integer sendFunction(@NonNull String name, @NonNull Collection<Function> functions, @NonNull CommandContext<ServerCommandSource> ctx) throws CommandException {
        Function function = functions.stream()
                .filter(f -> f.getFunctionName().equals(name))
                .findFirst().orElseThrow(() -> new CommandException(Text.of("Nonexistent: " + name)));

        ctx.getSource().sendFeedback(Text.of(
                name + " = " + function.getFunctionExpressionString() + '\n'
                + function.getDescription()
        ), false);

        return 1;
    }

    /**
     * Converts the {@link Method Methods} within a {@link Class} to a {@link Collection<Function>} of {@link Function Functions}.
     * The {@link Function Functions} within the {@link Class} are only considered if they have input parameters and a return type of {@link Double}.
     *
     * @param classes {@link Class Classes} to get {@link Function Functions} from.
     * @return A {@link Collection<Function>} of {@link Function Functions}.
     */
    private static @NonNull Collection<@NonNull Function> getFunctionsFromClasses(@NonNull Class<?>... classes) {
        Collection<Function> functions = new ArrayList<>(classes.length);
        for (Class<?> c : classes) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.getParameterCount() > 0 && m.getReturnType() == double.class) {
                    functions.add(new Function(m.getName(), new FunctionExtensionMethodWrapper(m)));
                }
            }
        }
        return functions;
    }

}
