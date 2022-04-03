package com.brittank88.dtdm.util.function;

import com.brittank88.dtdm.util.constant.ConstantCoordinator;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.mathcollection.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class FunctionCoordinator {

    /** {@link ImmutableMap} of all {@link Function Functions}, keyed by {@link FunctionCategory}. **/
    public static final @NotNull ImmutableMap<@NotNull FunctionCategory, @NotNull Collection<@NotNull Function>> FUNCTIONS = ImmutableMap.ofEntries(
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.USER                     , new ArrayList<>()),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.BINARY_RELATIONS         , FunctionUtils.ClassTools.getFunctionsFromClasses(BinaryRelations.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.BOOLEAN_ALGEBRA          , FunctionUtils.ClassTools.getFunctionsFromClasses(BooleanAlgebra.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.CALCULUS                 , FunctionUtils.ClassTools.getFunctionsFromClasses(Calculus.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.EVALUATE                 , FunctionUtils.ClassTools.getFunctionsFromClasses(Evaluate.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.MATH                     , FunctionUtils.ClassTools.getFunctionsFromClasses(MathFunctions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.NUMBER_THEORY            , FunctionUtils.ClassTools.getFunctionsFromClasses(NumberTheory.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.PROBABILITY_DISTRIBUTIONS, FunctionUtils.ClassTools.getFunctionsFromClasses(ProbabilityDistributions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.SPECIAL                  , FunctionUtils.ClassTools.getFunctionsFromClasses(SpecialFunctions.class)),
            new AbstractMap.SimpleImmutableEntry<>(FunctionCategory.STATISTICS               , FunctionUtils.ClassTools.getFunctionsFromClasses(Statistics.class))
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
    public static @NotNull Collection<@NotNull Function> getUserFunctions() { return Objects.requireNonNull(FUNCTIONS.get(FunctionCategory.USER)); }

    /**
     * Returns a {@link Collection<Function>} of all {@link Function Functions}, user-defined or not.
     *
     * @return A {@link Collection<Function>} of all {@link Function Functions}.
     */
    @SuppressWarnings("unused")
    public static @NotNull Collection<@NotNull Function> getAllFunctions() { return FUNCTIONS.values().stream().flatMap(Collection::stream).collect(Collectors.toList()); }

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
    @SuppressWarnings("SameReturnValue")
    public static @NotNull Integer addFunction(
            final @NotNull String name,
            final @NotNull String @NotNull [] parameters,
            final @NotNull String expression,
            final CommandContext<ServerCommandSource> ctx
    ) throws CommandException {

        // Check that function name and expression are valid and don't already exist as a default function.
        if (name.isEmpty()) throw new CommandException(Text.of(I18n.translate("message.error.name.generic.empty")));
        if (expression.isEmpty()) throw new CommandException(Text.of(I18n.translate("message.error.expression.empty")));
        if (name.contains(" ")) throw new CommandException(Text.of(I18n.translate("message.error.name.generic.spaces")));

        final String parameterString = Arrays.stream(parameters).map(Object::toString).collect(Collectors.joining(", "));
        final String functionDefinitionString = name + "(" + parameterString + ")=" + expression;

        final Function newFunction = new Function(functionDefinitionString);
        final Function existingFunction = getUserFunctions().stream()
                .filter(f -> FunctionUtils.ComparisonTools.compareName(f, new Function(functionDefinitionString))
                        && FunctionUtils.ComparisonTools.compareParameters(f, new Function(functionDefinitionString)))
                .findFirst().orElse(null);
        final String message;
        if (existingFunction != null) {
            message = I18n.translate(
                    "message.warning.function.override",
                    FunctionUtils.StringTools.getFunctionDisplayString(existingFunction, false),
                    existingFunction.getFunctionExpressionString(),
                    expression
            );

            getUserFunctions().remove(existingFunction);
        } else {
            // Check that the function doesn't already exist as a default function.
            if (getAllDefaultFunctions().stream().anyMatch(f -> FunctionUtils.ComparisonTools.compareAll(f, newFunction)))
                throw new CommandException(Text.of(I18n.translate("message.error.function.override", name)));

            message = I18n.translate("message.info.function.add", FunctionUtils.StringTools.getFunctionDisplayString(newFunction, false));
        }

        // Populate with functions and constants.
        // Allows function composition to occur.
        ConstantCoordinator.getUserConstants().stream().filter(c -> newFunction.getConstant(c.getConstantName()) == null).forEach(newFunction::addConstants);
        FunctionCoordinator.getUserFunctions().stream().filter(f -> newFunction.getFunction(f.getFunctionName()) == null).forEach(newFunction::addFunctions);

        // If there are still any un-defined variables, throw an error.
        if (newFunction.checkSyntax() == Function.SYNTAX_ERROR_OR_STATUS_UNKNOWN) throw new CommandException(Text.of(I18n.translate("message.error.function.syntax")));

        // Add the function to the list of user functions.
        getUserFunctions().add(newFunction);

        // Send feedback to the command invoker.
        ctx.getSource().sendFeedback(Text.of(message), false);

        // Return success.
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
    @SuppressWarnings("SameReturnValue")
    public static @NotNull Integer removeFunction(final @NotNull String name, final @NotNull CommandContext<ServerCommandSource> ctx) throws CommandException {
        if (name.isEmpty()) throw new CommandException(Text.of(I18n.translate("message.error.name.generic.empty")));
        if (name.contains(" ")) throw new CommandException(Text.of(I18n.translate("message.error.name.generic.spaces")));
        if (getAllDefaultFunctions().stream().anyMatch(f -> f.getFunctionName().equals(name))) throw new CommandException(Text.of(I18n.translate("message.error.function.remove_default", name)));

        // Remove the function from the list of user functions, by name.
        getUserFunctions().removeIf(f -> f.getFunctionName().equals(name));

        // Send feedback to the command invoker.
        ctx.getSource().sendFeedback(Text.of(I18n.translate("message.info.function.remove", name)), false);

        // Return success.
        return 1;
    }
}
