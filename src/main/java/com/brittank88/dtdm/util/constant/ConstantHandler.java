package com.brittank88.dtdm.util.constant;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.mathcollection.AstronomicalConstants;
import org.mariuszgromada.math.mxparser.mathcollection.MathConstants;
import org.mariuszgromada.math.mxparser.mathcollection.PhysicalConstants;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ConstantHandler {

    /** {@link ImmutableMap} of all {@link Constant Constants}, keyed by {@link ConstantCategory}. **/
    public static final @NonNull ImmutableMap<@NonNull ConstantCategory, @NonNull Collection<@NonNull Constant>> CONSTANTS = ImmutableMap.ofEntries(
            new AbstractMap.SimpleImmutableEntry<>(ConstantCategory.USER        , new ArrayList<>()),
            new AbstractMap.SimpleImmutableEntry<>(ConstantCategory.MATHEMATICAL, getConstantsFromClasses(MathConstants.class)),
            new AbstractMap.SimpleImmutableEntry<>(ConstantCategory.ASTRONOMICAL, getConstantsFromClasses(AstronomicalConstants.class)),
            new AbstractMap.SimpleImmutableEntry<>(ConstantCategory.PHYSICAL    , getConstantsFromClasses(PhysicalConstants.class))
    );

    /**
     * Returns a {@link Collection<Constant>} of all default {@link Constant Constants}.
     *
     * @return A {@link Collection<Constant>} of all default {@link Constant Constants}.
     */
    public static @NonNull Collection<@NonNull Constant> getAllDefaultConstants() {
        return CONSTANTS.entrySet().stream()
                .filter(e -> e.getKey().isDefault())
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns a {@link Collection<Constant>} of all user-defined {@link Constant Constants}.
     *
     * @return A {@link Collection<Constant>} of user-defined {@link Constant Constants}.
     */
    public static @NonNull Collection<@NonNull Constant> getUserConstants() { return Objects.requireNonNull(CONSTANTS.get(ConstantCategory.USER)); }

    /**
     * Returns a {@link Collection<Constant>} of all {@link Constant Constants}, user-defined or not.
     *
     * @return A {@link Collection<Constant>} of all {@link Constant Constants}.
     */
    public static @NonNull Collection<@NonNull Constant> getAllConstants() { return CONSTANTS.values().stream().flatMap(Collection::stream).collect(Collectors.toList()); }

    /**
     * Adds a {@link Constant} to the {@link Collection<Constant>} of user-defined {@link Constant Constants}.
     * Warns if the user is overwriting a {@link Constant} they defined earlier.
     *
     * @param name The {@link String name} of the {@link Constant}.
     * @param ctx The {@link CommandContext<ServerCommandSource>} of the command (for sending feedback to the {@link ServerCommandSource command invoker}).
     * @return Status {@link Integer}.
     * @throws CommandException If the function {@link String name} is invalid, or the {@link String name} references a default {@link Constant}.
     */
    public static @NonNull Integer addConstant(@NonNull String name, double value, @NonNull CommandContext<ServerCommandSource> ctx) throws CommandException {
        if (name.isEmpty()) throw new CommandException(Text.of("Name cannot be empty"));
        if (name.contains(" ")) throw new CommandException(Text.of("Function name cannot contain spaces"));
        if (getAllDefaultConstants().stream().anyMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of("Cannot override default constant: " + name));

        double existingConstantValue = getUserConstants().stream().filter(c -> c.getConstantName().equals(name)).findFirst().map(Constant::getConstantValue).orElse(Double.NaN);
        ctx.getSource().sendFeedback(Text.of(
                getUserConstants().stream().anyMatch(c -> c.getConstantName().equals(name)) && !Double.isNaN(existingConstantValue) ?
                        ("Overwrote constant " + name + ": " + existingConstantValue + "->" + value) :
                        ("Added constant " + StringArgumentType.getString(ctx, "name") + " with value " + DoubleArgumentType.getDouble(ctx, "constant"))
        ), false);

        getUserConstants().add(new Constant(name, value));

        return 1;
    }

    /**
     * Removes a user-defined {@link Constant}, given its {@link String name}.
     *
     * @param name The {@link String name} of the {@link Constant}.
     * @param ctx The {@link CommandContext<ServerCommandSource>} of the command (for sending feedback to the {@link ServerCommandSource command invoker}).
     * @return Status {@link Integer}.
     * @throws CommandException If the {@link String name} references a default {@link Constant} or none at all.
     */
    public static @NonNull Integer removeConstant(@NonNull String name, @NonNull CommandContext<ServerCommandSource> ctx) throws CommandException {
        if (getAllDefaultConstants().stream().anyMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of("Cannot remove default constant: " + name));
        if (getUserConstants().stream().noneMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of("Nonexistent: " + name));

        getUserConstants().removeIf(c -> c.getConstantName().equals(name));

        ctx.getSource().sendFeedback(Text.of("Removed constant " + StringArgumentType.getString(ctx, "name")), false);

        return 1;
    }

    /**
     * Sends a {@link Constant}'s {@link String name} and {@link Double value} to the {@link ServerCommandSource command invoker}.
     *
     * @param name The {@link String name} of the {@link Constant}.
     * @param constants The {@link Collection<Constant>} of {@link Constant Constants}.
     * @param ctx The {@link CommandContext<ServerCommandSource>} of the command (for sending feedback to the {@link ServerCommandSource command invoker}).
     * @return Status {@link Integer}.
     * @throws CommandException If the {@link Constant} could not be found.
     */
    public static @NonNull Integer sendConstant(@NonNull String name, @NonNull Collection<Constant> constants, @NonNull CommandContext<ServerCommandSource> ctx) throws CommandException {
        Double value = constants.stream()
                .filter(c -> c.getConstantName().equals(name))
                .map(Constant::getConstantValue)
                .findFirst().orElseThrow(() -> new CommandException(Text.of("Nonexistent: " + name)));

        ctx.getSource().sendFeedback(Text.of(name + " = " + value), false);

        return 1;
    }

    private static @NonNull Collection<@NonNull Constant> getConstantsFromClasses(@NonNull Class<?>... classes) {
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