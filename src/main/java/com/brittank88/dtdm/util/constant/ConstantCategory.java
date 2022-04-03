package com.brittank88.dtdm.util.constant;

/** Represents the {@link ConstantCategory category} of a {@link org.mariuszgromada.math.mxparser.Constant Constant}, as well as whether said category is default or user-defined. **/
public enum ConstantCategory {
    USER(false),
    MATHEMATICAL(true),
    ASTRONOMICAL(true),
    PHYSICAL(true);

    private final boolean isDefault;

    ConstantCategory(final boolean isDefault) { this.isDefault = isDefault; }

    /**
     * Returns whether this {@link ConstantCategory} is default or user-defined.
     *
     * @return {@link Boolean True} if it is default, {@link Boolean False} if it is user-defined.
     */
    public boolean isDefault() { return isDefault; }
}
