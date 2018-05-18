package com.dexmohq.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnumPropertyType<T extends Enum<T>> implements PropertyType<Enum> {

    private final Class<T> enumType;
    private final EnumFormatType formatType;

    @Override
    public Enum parse(String in) {
        return formatType.parse(enumType, in);
    }
}
