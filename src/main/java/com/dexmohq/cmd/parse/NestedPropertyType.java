package com.dexmohq.cmd.parse;

import com.dexmohq.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author Henrik Drefs
 */
public class NestedPropertyType<T> implements PropertyType<T> {

    private final Constructor<T> constructor;
    private final Map<String, ? extends PropertyType<?>> propertyTypes;

    public NestedPropertyType(Class<T> nestedType, Map<String, ? extends PropertyType<?>> propertyTypes) {
        this.constructor = ReflectionUtils.getDefaultConstructor(nestedType);
        this.propertyTypes = propertyTypes;
    }

    @Override
    public T parse(String in) {
        final T t = ReflectionUtils.newDefaultInstance(constructor);
        propertyTypes.forEach((name, propertyType) -> {
            final Object property = propertyType.parse(in);
            ReflectionUtils.setProperty(t, name, property);
        });
        return t;
    }
}
