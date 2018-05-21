package com.dexmohq.util;

import lombok.Value;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author Henrik Drefs
 */
@Value
public class Property {
    PropertyDescriptor propertyDescriptor;
    Field field;

    public String getName() {
        return propertyDescriptor.getName();
    }
}
