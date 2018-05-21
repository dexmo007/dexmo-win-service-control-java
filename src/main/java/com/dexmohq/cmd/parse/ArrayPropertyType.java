package com.dexmohq.cmd.parse;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
public class ArrayPropertyType<T> implements PropertyType<T[]> {

    private final Class<T> componentType;

    @Override
    public T[] parse(String in) {
        final ArrayList<T> arrayList = new ArrayList<>();

        return arrayList.toArray((T[])Array.newInstance(componentType,0));
    }
}
