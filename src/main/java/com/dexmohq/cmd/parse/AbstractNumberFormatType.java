package com.dexmohq.cmd.parse;

public interface AbstractNumberFormatType extends PropertyType<Integer> {

    @Override
    default Integer parse(String in) {
        final String first = in.split("\\s")[0];
        return parseInternal(first);
    }

    Integer parseInternal(String in);
}
