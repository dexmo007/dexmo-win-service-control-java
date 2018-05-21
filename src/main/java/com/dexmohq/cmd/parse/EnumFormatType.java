package com.dexmohq.cmd.parse;

import java.lang.reflect.InvocationTargetException;

public enum EnumFormatType {
    VALUE() {
        @SuppressWarnings("unchecked")
        @Override
        Enum parse(Class<? extends Enum> type, String in) {
            return Enum.valueOf(type, in);
        }
    },

    ORDINAL() {
        @Override
        Enum parse(Class<? extends Enum> type, String in) {
            try {
                return ((Enum[]) type.getMethod("values").invoke(null))[Integer.parseInt(in)];
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    };

    abstract Enum parse(Class<? extends Enum> type, String in);
}
