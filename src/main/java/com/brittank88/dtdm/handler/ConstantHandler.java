package com.brittank88.dtdm.handler;

import com.ibm.icu.lang.UCharacter;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.mathcollection.AstronomicalConstants;
import org.mariuszgromada.math.mxparser.mathcollection.MathConstants;
import org.mariuszgromada.math.mxparser.mathcollection.PhysicalConstants;

import java.lang.reflect.Field;
import java.util.*;

public abstract class ConstantHandler {

    public static final Collection<Constant> USER_CONSTANTS = new ArrayList<>();

    public static int addConstant(String name, double value) throws CommandException {
        if (!name.chars().filter(UCharacter::isLetter).allMatch(UCharacter::isUpperCase)) throw new CommandException(Text.of("Constant name must be all uppercase letters!"));
        if (ALL_DEFAULT_CONSTANTS.stream().anyMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of("Cannot override default constant: " + name));
        // TODO: Handle case where a user overwrites a user constant they previously set.
        if (USER_CONSTANTS.stream().anyMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of("Constant already exists: " + name));
        USER_CONSTANTS.add(new Constant(name, value));
        return 1;
    }

    public static int addConstant(String name, double value, CommandContext<ServerCommandSource> context) throws CommandException {
        int ret = addConstant(name, value);
        context.getSource().sendFeedback(Text.of(
                "Added constant "
                        + StringArgumentType.getString(context, "name")
                        + " with value "
                        + DoubleArgumentType.getDouble(context, "constant")
                ), true
        );
        return ret;
    }

    public static int removeConstant(String name) throws CommandException {
        if (ALL_DEFAULT_CONSTANTS.stream().anyMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of("Cannot remove default constant: " + name));
        if (USER_CONSTANTS.stream().noneMatch(c -> c.getConstantName().equals(name))) throw new CommandException(Text.of("Nonexistent: " + name));
        USER_CONSTANTS.removeIf(c -> c.getConstantName().equals(name));
        return 1;
    }

    public static int removeConstant(String name, CommandContext<ServerCommandSource> context) throws CommandException {
        int ret = removeConstant(name);
        context.getSource().sendFeedback(Text.of("Removed constant " + StringArgumentType.getString(context, "name")), true);
        return ret;
    }

    public static final Collection<Constant> MATHEMATICAL_CONSTANTS = getConstants(MathConstants.class);
    public static final Collection<Constant> ASTRONOMICAL_CONSTANTS = getConstants(AstronomicalConstants.class);
    public static final Collection<Constant> PHYSICAL_CONSTANTS = getConstants(PhysicalConstants.class);
    public static final Collection<Constant> ALL_DEFAULT_CONSTANTS = new ArrayList<>() {{
        addAll(MATHEMATICAL_CONSTANTS);
        addAll(ASTRONOMICAL_CONSTANTS);
        addAll(PHYSICAL_CONSTANTS);
    }};

    private static Collection<Constant> getConstants(Class<?>... constantContainers) {
        Collection<Constant> constants = new ArrayList<>(constantContainers.length);
        for (Class<?> c : constantContainers) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getType() == Double.TYPE) {
                    try { constants.add(new Constant(f.getName(), f.getDouble(null))); }
                    catch (IllegalAccessException e) { throw new RuntimeException(e); }
                }
            }
        }
        return constants;
    }

    public static int sendMathematicalConstant(CommandContext<ServerCommandSource> context) throws CommandException { return sendConstant(context, MATHEMATICAL_CONSTANTS); }
    public static int sendAstronomicalConstant(CommandContext<ServerCommandSource> context) throws CommandException { return sendConstant(context, ASTRONOMICAL_CONSTANTS); }
    public static int sendPhysicalConstant(CommandContext<ServerCommandSource> context) throws CommandException { return sendConstant(context, PHYSICAL_CONSTANTS); }
    public static int sendUserConstant(CommandContext<ServerCommandSource> context) throws CommandException { return sendConstant(context, USER_CONSTANTS); }

    private static int sendConstant(CommandContext<ServerCommandSource> context, Collection<Constant> constants) throws CommandException {
        String name = StringArgumentType.getString(context, "name");
        Double value = constants.stream().filter(c -> c.getConstantName().equals(name)).map(Constant::getConstantValue).findFirst().orElse(null);
        if (value == null) throw new CommandException(Text.of("Nonexistent: " + name));

        context.getSource().sendFeedback(Text.of(name + " = " + value), false);

        return 1;
    }
}
