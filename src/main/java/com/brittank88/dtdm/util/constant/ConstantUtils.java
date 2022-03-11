package com.brittank88.dtdm.util.constant;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public abstract class ConstantUtils {

    public abstract static class PopulationTools {

        public static void populateExpression(@NonNull Expression expression) {

            for (String missingConstName : expression.getMissingUserDefinedArguments()) {

                Constant missingConst = ConstantCoordinator.getUserConstants().stream()
                        .filter(userConstant -> userConstant.getConstantName().equals(missingConstName))
                        .findFirst().orElseThrow(() -> new RuntimeException("Missing user-defined constant: " + missingConstName));

                expression.addConstants(missingConst);
            }
        }
    }

    public static abstract class CommandTools {

        /**
         * Sends a {@link Constant}'s {@link String name} and {@link Double value} to the {@link ServerCommandSource command invoker}.
         *
         * @param name The {@link String name} of the {@link Constant}.
         * @param constants The {@link Collection <Constant>} of {@link Constant Constants}.
         * @param ctx The {@link CommandContext <ServerCommandSource>} of the command (for sending feedback to the {@link ServerCommandSource command invoker}).
         * @return Status {@link Integer}.
         * @throws CommandException If the {@link Constant} could not be found.
         */
        @SuppressWarnings("SameReturnValue")
        public static @NonNull Integer sendConstant(@NonNull String name, @NonNull Collection<Constant> constants, @NonNull CommandContext<ServerCommandSource> ctx) throws CommandException {
            Double value = constants.stream()
                    .filter(c -> c.getConstantName().equals(name))
                    .map(Constant::getConstantValue)
                    .findFirst().orElseThrow(() -> new CommandException(Text.of(I18n.translate("message.error.name.generic.nonexistent", name))));

            ctx.getSource().sendFeedback(Text.of(name + " = " + value), false);

            return 1;
        }
    }

    public static abstract class ClassTools {

        static @NonNull Collection<@NonNull Constant> getConstantsFromClasses(@NonNull Class<?> @NotNull ... classes) {
            Collection<Constant> constants = new ArrayList<>(classes.length);
            for (Class<?> c : classes) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.getType() == Double.TYPE) {
                        try { constants.add(new Constant(f.getName(), f.getDouble(null))); }
                        catch (IllegalAccessException e) { throw new RuntimeException(e); }
                    }
                }
            }
            return constants;
        }
    }
}
