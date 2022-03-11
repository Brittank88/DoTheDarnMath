package com.brittank88.dtdm.util.function;

import com.brittank88.dtdm.util.lang.LangUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
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

        public static void populateExpression(@NonNull Expression expression) throws RuntimeException {

            for (String missingFuncName : expression.getMissingUserDefinedFunctions()) {

                Function missingFunc = FunctionCoordinator.getUserFunctions().stream()
                        .filter(userFunction -> userFunction.getFunctionName().equals(missingFuncName))
                        .findFirst().orElseThrow(() -> new RuntimeException("Missing user-defined function: " + missingFuncName));

                expression.addFunctions(missingFunc);
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
        public static boolean compareAll(@NotNull Function f, @NotNull Function g) { return compareName(f, g) && compareExpression(f, g) && compareParameters(f, g); }

        public static boolean compareName(@NotNull Function f, @NotNull Function g) { return f.getFunctionName().equals(g.getFunctionName()); }

        public static boolean compareExpression(@NotNull Function f, @NotNull Function g) { return f.getFunctionExpressionString().equals(g.getFunctionExpressionString()); }

        public static boolean compareParameters(@NotNull Function f, @NotNull Function g) {
            return f.getParametersNumber() == g.getParametersNumber()
                    && IntStream.range(0, f.getParametersNumber())
                    .mapToObj(i -> new Pair<>(f.getParameterName(i), g.getParameterName(i)))
                    .allMatch(p -> p.getLeft().equals(p.getRight()));
        }
    }

    public static abstract class StringTools {

        public static @NonNull String getFunctionDisplayString(@NonNull Function function, boolean includeBody) {
            return function.getFunctionName() + getFunctionParameterSetString(function) + (includeBody ? '=' + function.getFunctionExpressionString() : "");
        }

        public static @NonNull String getFunctionParameterSetString(@NonNull Function function) {
            return "(" + formatParametersToString(getAllParameterStrings(function))  + ")";
        }

        public static @NonNull Collection<String> getAllParameterStrings(@NonNull Function function) {
            return IntStream.range(0, function.getParametersNumber()).mapToObj(function::getParameterName).collect(Collectors.toList());
        }

        public static @NonNull String formatParametersToString(@NotNull Collection<String> parameters) {
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
        public static @NonNull Integer sendFunction(@NonNull CommandContext<ServerCommandSource> ctx, @NonNull Collection<Function> functions) throws CommandException {

            String name = StringArgumentType.getString(ctx, LangUtils.CommonLang.Argument.NAME);

            Function function = functions.stream()
                    .filter(f -> f.getFunctionName().equals(name))
                    .findFirst().orElseThrow(() -> new CommandException(Text.of(I18n.translate("message.error.name.generic.nonexistent", name))));

            String descriptionString = function.getDescription();
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
        static @NonNull Collection<@NonNull Function> getFunctionsFromClasses(@NonNull Class<?> @NotNull ... classes) {
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
}
