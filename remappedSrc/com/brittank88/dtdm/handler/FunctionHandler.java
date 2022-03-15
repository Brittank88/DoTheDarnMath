package com.brittank88.dtdm.handler;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.mariuszgromada.math.mxparser.mathcollection.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FunctionHandler {

    /** Represents the {@link FUNCTION_CATEGORY category} of a {@link Function}, as well as whether said category is default or user-defined. **/
    public enum FUNCTION_CATEGORY {

        USER(false),
        BINARY_RELATIONS(true),
        BOOLEAN_ALGEBRA(true),
        CALCULUS(true),
        EVALUATE(true),
        MATH(true),
        NUMBER_THEORY(true),
        PROBABILITY_DISTRIBUTIONS(true),
        SPECIAL(true),
        STATISTICS(true);

        private final boolean isDefault;

        FUNCTION_CATEGORY(boolean isDefault) { this.isDefault = isDefault; }

        /**
         * Returns whether this {@link FUNCTION_CATEGORY} is default or user-defined.
         *
         * @return {@link Boolean True} if it is default, {@link Boolean False} if it is user-defined.
         */
        public boolean isDefault() { return this.isDefault; }
    }

    /** {@link ImmutableMap} of all {@link Function Functions}, keyed by {@link FUNCTION_CATEGORY}. **/
    public static final @NotNull ImmutableMap<@NotNull FUNCTION_CATEGORY, @NotNull Collection<@NotNull Function>> FUNCTIONS = ImmutableMap.ofEntries(
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.USER                     , new ArrayList<>()),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.BINARY_RELATIONS         , getFunctionsFromClasses(BinaryRelations.class)),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.BOOLEAN_ALGEBRA          , getFunctionsFromClasses(BooleanAlgebra.class)),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.CALCULUS                 , getFunctionsFromClasses(Calculus.class)),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.EVALUATE                 , getFunctionsFromClasses(Evaluate.class)),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.MATH                     , getFunctionsFromClasses(MathFunctions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.NUMBER_THEORY            , getFunctionsFromClasses(NumberTheory.class)),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.PROBABILITY_DISTRIBUTIONS, getFunctionsFromClasses(ProbabilityDistributions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.SPECIAL                  , getFunctionsFromClasses(SpecialFunctions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FUNCTION_CATEGORY.STATISTICS               , getFunctionsFromClasses(Statistics.class))
            // TODO: org.mariuszgromada.math.mxparser.mathcollection.PrimesCache ?
            // TODO: org.mariuszgromada.math.mxparser.mathcollection.SpecialValue ?
    );

    /**
     * Returns a {@link Collection<Function>} of all default {@link Function Functions}.
     *
     * @return A {@link Collection<Function>} of all default {@link Function Functions}.
     */
    public static @NotNull Collection<@NotNull Function> getAllDefaultFunctions() {
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
    public static @NotNull Collection<@NotNull Function> getUserFunctions() { return Objects.requireNonNull(FUNCTIONS.get(FUNCTION_CATEGORY.USER)); }

    /**
     * Returns a {@link Collection<Function>} of all {@link Function Functions}, user-defined or not.
     *
     * @return A {@link Collection<Function>} of all {@link Function Functions}.
     */
    public static @NotNull Collection<@NotNull Function> getAllFunctions() { return FUNCTIONS.values().stream().flatMap(Collection::stream).collect(Collectors.toList()); }

    /**
     * Adds a {@link Function} to the {@link Collection<Function>} of user-defined {@link Function Functions}.
     * Warns if the user is overwriting a {@link Function} they defined earlier.
     *
     * @param name The {@link String name} of the {@link Function}.
     * @param expression The {@link String expression} of the {@link Function}.
     * @param ctx The {@link CommandContext<ServerCommandSource>} of the command (for sending feedback to the {@link ServerCommandSource command invoker}).
     * @return Status {@link Integer}.
     * @throws CommandException If the function {@link String name} or {@link String expression} is invalid, or the {@link String name} references a default {@link Function}.
     */
    public static @NotNull Integer addFunction(@NotNull String name, @NotNull String expression, @NotNull CommandContext<ServerCommandSource> ctx) throws CommandException {

        // Check that function name and expression are valid and don't already exist as a default function.
        if (name.isEmpty()) throw new CommandException(Text.of("Function name cannot be empty"));
        if (expression.isEmpty()) throw new CommandException(Text.of("Function expression cannot be empty"));
        if (name.contains(" ")) throw new CommandException(Text.of("Function name cannot contain spaces"));
        if (getAllDefaultFunctions().stream().anyMatch(f -> f.getFunctionName().equals(name))) throw new CommandException(Text.of("Cannot override default function: " + name));

        // Warn if user-defined function is being overridden.
        String existingExpression = getUserFunctions().stream()
                .filter(c -> c.getFunctionName().equals(name)).findFirst()
                .map(Function::getFunctionExpressionString).orElse("");
        ctx.getSource().sendFeedback(Text.of(
                getUserFunctions().stream().anyMatch(c -> c.getFunctionName().equals(name)) && !existingExpression.isEmpty() ?
                        ("Overwrote function " + name + ": " + existingExpression + "->" + expression) :
                        ("Added function " + StringArgumentType.getString(ctx, "name") + " with expression " + StringArgumentType.getString(ctx, "expression"))
        ), false);

        Function function = new Function(name + '=' + expression);
        function.addConstants(ConstantHandler.getAllConstants().toArray(new Constant[0]));
        function.addFunctions(getAllFunctions().toArray(new Function[0]));  // Support function-in-function.
        getUserFunctions().add(function);

        return 1;
    }

    /**
     * Removes a user-defined {@link Function}, given its {@link String name}.
     *
     * @param name The {@link String name} of the {@link Function} to remove.
     * @param ctx The {@link CommandContext<ServerCommandSource>} (used to send feedback to the {@link ServerCommandSource command invoker}).
     * @return Status {@link Integer}.
     * @throws CommandException If the {@link String name} is invalid or references a default {@link Function}.
     */
    public static @NotNull Integer removeFunction(@NotNull String name, @NotNull CommandContext<ServerCommandSource> ctx) throws CommandException {
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
    public static @NotNull Integer sendFunction(@NotNull String name, @NotNull Collection<Function> functions, @NotNull CommandContext<ServerCommandSource> ctx) throws CommandException {
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
    private static @NotNull Collection<@NotNull Function> getFunctionsFromClasses(@NotNull Class<?>... classes) {
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

    /**
     * A wrapper implementing {@link FunctionExtension}.
     * Allows a {@link Method} to be used in the creation of a {@link Function}.
     */
    private static class FunctionExtensionMethodWrapper implements FunctionExtension, Cloneable {

        private final @NotNull Method method;
        private final @NotNull List<AbstractMap.SimpleEntry<@NotNull String, @NotNull OptionalDouble>> parameters;

        public FunctionExtensionMethodWrapper(@NotNull Method method) {
            this.method = method;
            this.parameters = Arrays.stream(method.getParameters())
                    .map(p -> new AbstractMap.SimpleEntry<>(p.getName(), OptionalDouble.empty()))
                    .collect(Collectors.toList());
        }

        @Override public int getParametersNumber() { return parameters.size(); }
        @Override public void setParameterValue(int parameterIndex, double parameterValue) { parameters.get(parameterIndex).setValue(OptionalDouble.of(parameterValue)); }
        @Override public @NotNull String getParameterName(int parameterIndex) { return parameters.get(parameterIndex).getKey(); }

        @Override public double calculate() throws RuntimeException {
            if (parameters.stream().anyMatch(p -> p.getValue().isEmpty())) throw new RuntimeException("Missing parameter value.");
            try { return (double) method.invoke(null, (Object) parameters.stream().map(AbstractMap.SimpleEntry::getValue).toArray(OptionalDouble[]::new)); }
            catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }
        }

        @Override public @NotNull FunctionExtension clone() throws RuntimeException {
            try { return (FunctionExtensionMethodWrapper) super.clone(); } catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
        }
    }
}
