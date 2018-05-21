package com.dexmohq.util;

import com.dexmohq.cmd.parse.BeanLineParser;
import lombok.experimental.UtilityClass;
import org.apache.commons.beanutils.BeanUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Henrik Drefs
 */
@UtilityClass
public class ReflectionUtils {

    public static <A extends Annotation, R> R getAnnotationValueOrDefault(AnnotatedElement ae, Class<A> annotationType, Function<A, R> valueFunction, R defaultValue) {
        final A annotation = ae.getAnnotation(annotationType);
        if (annotation == null) {
            return defaultValue;
        }
        return valueFunction.apply(annotation);
    }

    public static <T> T newDefaultInstance(Constructor<T> defaultConstructor) {
        try {
            return defaultConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> Constructor<T> getDefaultConstructor(Class<T> type) {
        try {
            final Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.trySetAccessible();
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No default constructor for type: " + type, e);
        }
    }

    public static Stream<Property> getProperties(Class<?> type) {
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new InternalError(e);
        }
        return Arrays.stream(beanInfo.getPropertyDescriptors())
                .filter(pd -> !pd.getName().equals("class"))
                .map(pd -> new Property(pd, getField(type, pd.getName())));
    }

    private static Field getField(Class<?> type, String field) {
        try {
            return type.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setProperty(Object o, String name, Object value) {
        try {
            BeanUtils.setProperty(o, name, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
