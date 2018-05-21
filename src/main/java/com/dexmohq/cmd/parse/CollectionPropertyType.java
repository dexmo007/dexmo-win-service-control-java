package com.dexmohq.cmd.parse;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

/**
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
public class CollectionPropertyType<T> implements PropertyType<Collection<T>> {

    private final DelimiterType delimiterType;

    @Override
    public Collection<T> parse(String in) {
        return null;
    }
}
