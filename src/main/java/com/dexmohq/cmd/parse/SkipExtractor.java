package com.dexmohq.cmd.parse;

import lombok.RequiredArgsConstructor;

/**
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
public class SkipExtractor<T> implements PropertyType<T> {

    private final PropertyType<T> propertyType;
    private final int skip;

    @Override
    public T parse(String in) {
        return propertyType.parse(in.trim().substring(skip));
    }
}
