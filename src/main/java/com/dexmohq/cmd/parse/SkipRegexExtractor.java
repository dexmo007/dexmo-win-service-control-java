package com.dexmohq.cmd.parse;

import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SkipRegexExtractor<T> implements PropertyType<T> {

    private final PropertyType<T> propertyType;
    private final Pattern regex;
    private final int skip;

    @Override
    public T parse(String in) {
        final Matcher matcher = regex.matcher(in);
        for (int i = 0; i < skip; i++) {
            if (!matcher.find()) {
                return null;
            }
        }
        return propertyType.parse(in.substring(matcher.end()));
    }

}
