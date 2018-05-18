package com.dexmohq.util;

public class StringPropertyType implements PropertyType<String> {
    @Override
    public String parse(String in) {
        return in;
    }
}
