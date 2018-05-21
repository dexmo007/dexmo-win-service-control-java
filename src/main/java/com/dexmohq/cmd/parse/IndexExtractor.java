package com.dexmohq.cmd.parse;

import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

/**
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
public class IndexExtractor<T> implements PropertyType<T> {

    private final int index;
    private final Pattern regex;
    private final PropertyType<T> propertyType;

    @Override
    public T parse(String in) {
        final String[] parts = regex.split(in);
        int i = index;
        if (index < 0) {
            i += parts.length;
        }
        if (i < 0 || i >= parts.length) {
            return null;
        }
        return propertyType.parse(parts[i]);
    }
}
