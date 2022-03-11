package com.brittank88.dtdm.util.constant;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.mathcollection.AstronomicalConstants;
import org.mariuszgromada.math.mxparser.mathcollection.MathConstants;
import org.mariuszgromada.math.mxparser.mathcollection.PhysicalConstants;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ConstantCoordinator {

    /** {@link ImmutableMap} of all {@link Constant Constants}, keyed by {@link ConstantCategory}. **/
    public static final @NonNull ImmutableMap<@NonNull ConstantCategory, @NonNull Collection<@NonNull Constant>> CONSTANTS = ImmutableMap.ofEntries(
            new AbstractMap.SimpleImmutableEntry<>(ConstantCategory.USER        , new ArrayList<>()),
            new AbstractMap.SimpleImmutableEntry<>(ConstantCategory.MATHEMATICAL, ConstantUtils.ClassTools.getConstantsFromClasses(MathConstants.class)),
            new AbstractMap.SimpleImmutableEntry<>(ConstantCategory.ASTRONOMICAL, ConstantUtils.ClassTools.getConstantsFromClasses(AstronomicalConstants.class)),
            new AbstractMap.SimpleImmutableEntry<>(ConstantCategory.PHYSICAL    , ConstantUtils.ClassTools.getConstantsFromClasses(PhysicalConstants.class))
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("SameReturnValue")
    public static @NonNull Integer addConstant(@NonNull String name, double value, @NonNull CommandContext<ServerCommandSource> ctx) throws CommandException {

        // Check that constant name is valid and doesn't already exist as a default constant.
        if (name.isEmpty()) throw new CommandException(Text.of(I18n.translate("message.error.name.generic.empty")));
        if (name.contains(" ")) throw new CommandException(Text.of(I18n.translate("message.error.name.generic.spaces")));
        if (getAllDefaultConstants().stream().anyMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of(I18n.translate("message.error.constant.override", name)));

        Constant constant = getUserConstants().stream().filter(c -> c.getConstantName().equals(name)).findFirst().orElse(null);
        String message;
        if (constant != null) {
            message = I18n.translate("message.warning.constant.override", name, constant.getConstantValue(), value);
            constant.setConstantValue(value);
        } else {
            message = I18n.translate("message.info.constant.add", name, value);
            constant = new Constant(name, value);
            getUserConstants().add(constant);
        }

        ctx.getSource().sendFeedback(Text.of(message), false);

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
    @SuppressWarnings("SameReturnValue")
    public static @NonNull Integer removeConstant(@NonNull String name, @NonNull CommandContext<ServerCommandSource> ctx) throws CommandException {
        if (getAllDefaultConstants().stream().anyMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of(I18n.translate("message.error.constant.removeDefault", name)));
        if (getUserConstants().stream().noneMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of(I18n.translate("message.error.name.generic.nonexistent", name)));

        getUserConstants().removeIf(c -> c.getConstantName().equals(name));

        ctx.getSource().sendFeedback(Text.of(I18n.translate(
                "message.info.remove.constant",
                StringArgumentType.getString(ctx, I18n.translate("commands.generic.argument.name"))
        )), false);

        return 1;
    }
}
