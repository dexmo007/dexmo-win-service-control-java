package com.dexmohq.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Extractor<T> implements PropertyType<T> {

    private final PropertyType<T> propertyType;
    private final String regex;
    private final int skip;

    @Override
    public T parse(String in) {
        final String[] parts = in.split(regex);
        int i = skip;
        if (skip < 0) {
            i = parts.length + skip;
        }
        final String value = parts[i].trim();
        return propertyType.parse(value);
    }
}
