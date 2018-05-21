package com.dexmohq.cmd.parse;

public class StringPropertyType implements PropertyType<String> {
    @Override
    public String parse(String in) {
        return in;
    }
}
