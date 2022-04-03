package com.brittank88.dtdm.util.bool;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mariuszgromada.math.mxparser.mathcollection.BooleanAlgebra;

import java.lang.reflect.Field;

public abstract class BooleanUtils {

    private static final Double[] FTN_DOUBLES = new Double[] { BooleanAlgebra.F, BooleanAlgebra.T, BooleanAlgebra.N };

    public static abstract class StringTools {

        public static @NonNls String booleanDoubleToString(@Nullable Double b, boolean longForm) {
            String postfix = longForm ? ".long" : ".short";
            if (b == null || b.isNaN()) return I18n.translate("commands.dtdm.boolean.literal.n" + postfix);
            return I18n.translate("commands.dtdm.boolean.literal." + (b.intValue() == 1 ? "t" : "f") + postfix);
        }
    }

    public abstract static class TruthTable {

        public static abstract class CommandTools {

            public static @NotNull Integer sendTruthTable(final CommandContext<ServerCommandSource> ctx, final ImmutableMap<String, ImmutableMap<Double, ImmutableMap<Double, Double>>> truthTables) {

                final String truthTableName = ctx.getArgument(I18n.translate("commands.generic.argument.name"), String.class);
                final ImmutableMap<Double, ImmutableMap<Double, Double>> truthTable = truthTables.get(truthTableName);
                if (truthTable == null) throw new CommandException(Text.of(I18n.translate("message.error.name.generic.nonexistent", truthTableName)));
                final StringBuilder truthTableString = new StringBuilder()

                        .append("o===o===o===o===o\n") // NON-NLS

                        .append("|:|_").append(StringUtils.rightPad(truthTableName, 14, '_')).append("|:|\n")

                        .append("o===o===o===o===o\n") // NON-NLS

                        .append("|:|///|:|")

                        .append('_')
                        .append(BooleanUtils.StringTools.booleanDoubleToString(BooleanAlgebra.F, false))
                        .append("_|:|_")
                        .append(BooleanUtils.StringTools.booleanDoubleToString(BooleanAlgebra.T, false))
                        .append("_|:|_")
                        .append(BooleanUtils.StringTools.booleanDoubleToString(BooleanAlgebra.N, false))
                        .append("_|:|");

                for (final Double ftn : FTN_DOUBLES) {
                    final ImmutableMap<Double, Double> row = truthTable.get(ftn);
                    final String ftnString = BooleanUtils.StringTools.booleanDoubleToString(ftn, false);
                    if (row == null) throw new CommandException(Text.of(I18n.translate("message.error.boolean.truthTable.row_not_found", truthTableName, ftnString)));

                    truthTableString
                            .append("\no===|:|===|:|===|:|===o\n") // NON-NLS

                            .append("|:|_")
                            .append(ftnString)
                            .append("_|:|_")
                            .append(BooleanUtils.StringTools.booleanDoubleToString(row.get(BooleanAlgebra.F), false))
                            .append("_|:|_")
                            .append(BooleanUtils.StringTools.booleanDoubleToString(row.get(BooleanAlgebra.T), false))
                            .append("_|:|_")
                            .append(BooleanUtils.StringTools.booleanDoubleToString(row.get(Double.NaN), false))
                            .append("_|:|");
                }

                truthTableString.append("\no===o===o===o===o"); // NON-NLS

                for (final Double ftn : FTN_DOUBLES) {
                    truthTableString.append("\n|:|_")
                            .append(StringUtils.rightPad(
                                    BooleanUtils.StringTools.booleanDoubleToString(ftn, false)
                                            + "="
                                            + BooleanUtils.StringTools.booleanDoubleToString(ftn, true),
                                    10, '_'
                            ))
                            .append("|:|");
                }

                truthTableString.append("\no===o===o===o"); // NON-NLS

                ctx.getSource().sendFeedback(Text.of(truthTableString.toString()), false);

                return 1;
            }
        }

        public static abstract class ClassTools {

            /**
             * <p>Retrieves {@link ImmutableMap truth tables}.</p>
             * <p>
             *     Wraps {@link BooleanUtils.TruthTable.ClassTools#getTruthTablesFromClasses(Class...)}
             *     by providing it {@link Class classes} that are known to correctly work with it.
             * </p>
             *
             * @return A {@link ImmutableMap} of {@link ImmutableMap truth tables} keyed by {@link String name}.
             */
            public static ImmutableMap<String, ImmutableMap<Double, ImmutableMap<Double, Double>>> getTruthTables() {
                return getTruthTablesFromClasses(BooleanAlgebra.class);
            }

            /**
             * <p>Retrieves {@link ImmutableMap truth tables} for each supplied {@link Class class}.</p>
             * <p><strong>Note: This assumes truth tables are declared with an identical structure to what is present in the {@link BooleanAlgebra BooleanAlgebra} class!</strong></p>
             *
             * @param classes The {@link ImmutableMap<Class>} of {@link Class classes} to retrieve truth tables for.
             * @return A {@link ImmutableMap} of {@link ImmutableMap truth tables} keyed by {@link String name}.
             */
            private static ImmutableMap<String, ImmutableMap<Double, ImmutableMap<Double, Double>>> getTruthTablesFromClasses(final @NotNull Class<?> @NotNull ... classes) {

                final ImmutableMap.Builder<String, ImmutableMap<Double, ImmutableMap<Double, Double>>> truthTables = ImmutableMap.builder();

                for (final Class<?> c : classes) {
                    for (final Field f : c.getDeclaredFields()) {

                        // Get the field type.
                        Class<?> type = f.getType();

                        // Ensure the field is an array.
                        if (!type.isArray()) continue;

                        // Check if the array is 2D.
                        boolean is2DArray = type.getComponentType().isArray();

                        // Ensure that the field is some array (1D or 2D) of Double values.
                        if (!(is2DArray ? type.getComponentType() : type).getComponentType().getName().equals("double")) continue;

                        // Get the field's value.
                        final Object value;
                        try { value = f.get(null); }
                        catch (final IllegalAccessException e) { continue; }
                        double[][] truthTable = is2DArray ? (double[][]) value : new double[][] { (double[]) value };

                        // Ensure the truth table has the correct dimensions.
                        if ((is2DArray && (
                                truthTable.length != 3
                                || truthTable[0].length != 3
                                || truthTable[1].length != 3
                                || truthTable[2].length != 3
                        )) || truthTable[0].length != 3) continue;

                        // Get the name.
                        final String name = f.getName().toUpperCase().replace("_TRUTH_TABLE", "");  // NON-NLS

                        // Convert truth table rows to ImmutableMap objects.
                        final ImmutableMap.Builder<Double, ImmutableMap<Double, Double>> truthTableValues = ImmutableMap.builder();
                        for (int i = 0; i < truthTable.length; i++) {
                            truthTableValues.put(FTN_DOUBLES[i], ImmutableMap.of(
                                    BooleanAlgebra.F, truthTable[i][0],
                                    BooleanAlgebra.T, truthTable[i][1],
                                    BooleanAlgebra.N, truthTable[i][2]
                            ));
                        }

                        // Add the truth table to the map.
                        truthTables.put(name, truthTableValues.build());
                    }
                }
                return truthTables.build();
            }
        }
    }
}
