package com.brittank88.dtdm.util.function;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class FunctionUtils {

    public abstract static class PopulationTools {

        public static void populateExpression(final @NotNull Expression expression) throws RuntimeException {

            for (final String missingFuncName : expression.getMissingUserDefinedFunctions()) {

                expression.addFunctions(
                        FunctionCoordinator.getUserFunctions().stream()
                                .filter(userFunction -> userFunction.getFunctionName().equals(missingFuncName))
                                .findFirst().orElseThrow(() -> new RuntimeException("Missing user-defined function: " + missingFuncName))
                );
            }
        }
    }

    public abstract static class ComparisonTools {

        /**
         * Compares two {@link Function}s by their {@link String name}, {@link String expression}, {@link Integer parameter count} and {@link String parameter names}.
         *
         * @see #compareName(Function, Function)
         * @see #compareExpression(Function, Function)
         * @see #compareParameters(Function, Function)
         * @param f The first {@link Function} of the comparison.
         * @param g The second {@link Function} of the comparison.
         * @return {@code true} if the {@link Function}s are equal, {@code false} otherwise.
         */
        public static boolean compareAll(final @NotNull Function f, final @NotNull Function g) { return compareName(f, g) && compareExpression(f, g) && compareParameters(f, g); }

        public static boolean compareName(final @NotNull Function f, final @NotNull Function g) { return f.getFunctionName().equals(g.getFunctionName()); }

        public static boolean compareExpression(final @NotNull Function f, final @NotNull Function g) { return f.getFunctionExpressionString().equals(g.getFunctionExpressionString()); }

        public static boolean compareParameters(final @NotNull Function f, final @NotNull Function g) {
            return f.getParametersNumber() == g.getParametersNumber()
                    && IntStream.range(0, f.getParametersNumber())
                    .mapToObj(i -> new Pair<>(f.getParameterName(i), g.getParameterName(i)))
                    .allMatch(p -> p.getLeft().equals(p.getRight()));
        }
    }

    public static abstract class StringTools {

        public static @NotNull String getFunctionDisplayString(final @NotNull Function function, final boolean includeBody) {
            return function.getFunctionName() + getFunctionParameterSetString(function) + (includeBody ? '=' + function.getFunctionExpressionString() : "");
        }

        public static @NotNull String getFunctionParameterSetString(final @NotNull Function function) {
            return "(" + formatParametersToString(getAllParameterStrings(function))  + ")";
        }

        public static @NotNull Collection<String> getAllParameterStrings(final @NotNull Function function) {
            return IntStream.range(0, function.getParametersNumber()).mapToObj(function::getParameterName).collect(Collectors.toList());
        }

        public static @NotNull String formatParametersToString(final @NotNull Collection<String> parameters) {
            return parameters.stream().map(Object::toString).collect(Collectors.joining(", "));
        }
    }

    public static abstract class CommandTools {

        /**
         * Sends a {@link Function}'s {@link String name} and {@link Double value} to the {@link ServerCommandSource command invoker}.
         *
         * @param ctx The {@link CommandContext <ServerCommandSource>} of the command (for sending feedback to the {@link ServerCommandSource command invoker}).
         * @param functions The {@link Collection<Function>} of {@link Function Functions}.
         * @return Status {@link Integer}.
         * @throws CommandException If the {@link Function} could not be found.
         */
        @SuppressWarnings("SameReturnValue")
        public static @NotNull Integer sendFunction(final @NotNull CommandContext<ServerCommandSource> ctx, final @NotNull Collection<Function> functions) throws CommandException {

            final String name = StringArgumentType.getString(ctx, I18n.translate("commands.generic.argument.name"));

            final Function function = functions.stream()
                    .filter(f -> f.getFunctionName().equals(name))
                    .findFirst().orElseThrow(() -> new CommandException(Text.of(I18n.translate("message.error.name.generic.nonexistent", name))));

            final String descriptionString = function.getDescription();
            ctx.getSource().sendFeedback(Text.of(
                    StringTools.getFunctionDisplayString(function, true)
                            + I18n.translate("message.info.function.compute_time", function.getComputingTime())
                            + (descriptionString.isBlank() ? "" : '\n' + descriptionString)
            ), false);

            // Return the status of the command.
            return 1;
        }
    }

    public static abstract class ClassTools {

        /**
         * Converts the {@link Method Methods} within a {@link Class} to a {@link Collection<Function>} of {@link Function Functions}.
         * The {@link Function Functions} within the {@link Class} are only considered if they have input parameters and a return type of {@link Double}.
         *
         * @param classes {@link Class Classes} to get {@link Function Functions} from.
         * @return A {@link Collection<Function>} of {@link Function Functions}.
         */
        static @NotNull Collection<@NotNull Function> getFunctionsFromClasses(final @NotNull Class<?> @NotNull ... classes) {
            final Collection<Function> functions = new ArrayList<>(classes.length);
            for (final Class<?> c : classes) {
                for (final Method m : c.getDeclaredMethods()) {
                    if (m.getParameterCount() > 0 && m.getReturnType() == double.class) {
                        functions.add(new Function(m.getName(), new FunctionExtensionMethodWrapper(m)));
                    }
                }
            }
            return functions;
        }
    }
}
