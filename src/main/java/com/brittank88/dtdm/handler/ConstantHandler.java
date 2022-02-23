package com.brittank88.dtdm.handler;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.mariuszgromada.math.mxparser.mathcollection.AstronomicalConstants;
import org.mariuszgromada.math.mxparser.mathcollection.MathConstants;
import org.mariuszgromada.math.mxparser.mathcollection.PhysicalConstants;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class ConstantHandler {

    private static final Class<?>[] DEFAULT_CONSTANT_CLASSES = new Class<?>[] {
            AstronomicalConstants.class,
            MathConstants.class,
            PhysicalConstants.class
    };

    public static final Map<String, Double> MATHEMATICAL_CONSTANTS = getConstants(MathConstants.class);
    public static final Map<String, Double> ASTRONOMICAL_CONSTANTS = getConstants(AstronomicalConstants.class);
    public static final Map<String, Double> PHYSICAL_CONSTANTS = getConstants(PhysicalConstants.class);
    public static final Map<String, Double> USER_CONSTANTS = new HashMap<>();  // TODO: Implement.

    private static Map<String, Double> getConstants(Class<?>... constantContainers) {
        Map<String, Double> constants = new HashMap<>();
        for (Class<?> c : constantContainers) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getType() == Double.TYPE) {
                    try { constants.put(f.getName(), f.getDouble(null)); }
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

    private static int sendConstant(CommandContext<ServerCommandSource> context, Map<String, Double> constants) throws CommandException {
        String name = StringArgumentType.getString(context, "name");
        Double value = constants.get(name);
        if (value == null) throw new CommandException(Text.of("Nonexistent: " + name));

        context.getSource().sendFeedback(Text.of(name + " = " + value), false);

        return 1;
    }
}
