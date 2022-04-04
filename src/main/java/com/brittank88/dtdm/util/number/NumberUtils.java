package com.brittank88.dtdm.util.number;

public abstract class NumberUtils {

    public static abstract class StringTools {

        public static String toMinimalString(Number number) {
            return String.valueOf((int) number == number.doubleValue() ? (int) number : number);
        }
    }
}
