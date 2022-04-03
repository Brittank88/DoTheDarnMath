package com.brittank88.dtdm.util.function;

import org.mariuszgromada.math.mxparser.Function;

/** Represents the {@link FunctionCategory category} of a {@link Function}, as well as whether said category is default or user-defined. **/
public enum FunctionCategory {

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

    FunctionCategory(final boolean isDefault) { this.isDefault = isDefault; }

    /**
     * Returns whether this {@link FunctionCategory} is default or user-defined.
     *
     * @return {@link Boolean True} if it is default, {@link Boolean False} if it is user-defined.
     */
    public final boolean isDefault() { return this.isDefault; }
}
