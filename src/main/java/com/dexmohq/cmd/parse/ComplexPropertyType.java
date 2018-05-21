package com.dexmohq.cmd.parse;

import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
public class ComplexPropertyType<T> implements PropertyType<T> {

    private final BeanLineParser<T> lineParser;

    @Override
    public T parse(String in) {
        return lineParser.parse(Stream.of(in));
    }
}
