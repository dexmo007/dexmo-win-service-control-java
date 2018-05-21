package com.dexmohq.cmd.parse;

import com.dexmohq.util.Property;
import com.dexmohq.util.ReflectionUtils;
import com.dexmohq.win.service.ServiceInfo;
import com.google.common.base.CaseFormat;
import lombok.RequiredArgsConstructor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class BeanLineParser<T> implements LineParser<T> {

    private final Class<T> type;

    private final Constructor<T> constructor;

    private final String keyValueDelimiter;

    private final CaseFormat caseFormat;

    private final Map<String, PropertyType<?>> propertyTypes;

    public static <T> BeanLineParser<T> createForType(Class<T> type) {
        final Constructor<T> constructor;
        constructor = ReflectionUtils.getDefaultConstructor(type);
        final Naming naming = type.getAnnotation(Naming.class);
        final CaseFormat caseFormat;
        if (naming == null) {
            caseFormat = Naming.DEFAULT_FORMAT;
        } else {
            caseFormat = naming.format();
        }
        final Map<String, PropertyType<?>> types = ReflectionUtils.getProperties(type)
                .collect(toMap(pd -> CaseFormat.LOWER_CAMEL.to(caseFormat, pd.getName()),
                        BeanLineParser::getPropertyType));
        return new BeanLineParser<>(type, constructor, ":", caseFormat, types);
    }

    private static PropertyType<?> getPropertyType(Property property) {
        final PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        final Class<?> type = propertyDescriptor.getPropertyType();
        final Field field = property.getField();
        final PropertyType<?> propertyType = getPropertyType(type, field);
        if (field.isAnnotationPresent(Index.class)) {
            final Index index = field.getAnnotation(Index.class);
            return new IndexExtractor<>(index.value(), Pattern.compile(index.split()), propertyType);
        }
        final Skip skip = field.getAnnotation(Skip.class);
        if (skip != null && skip.value() != 0) {
            if (skip.split().equals(Skip.NO_SPLIT)) {
                return new SkipExtractor<>(propertyType, skip.value());
            }
            return new SkipRegexExtractor<>(propertyType, Pattern.compile(skip.split()), skip.value());
        }
        return propertyType;
    }

    @SuppressWarnings("unchecked")
    private static PropertyType<?> getPropertyType(Class<?> type, Field field) {
        if (type.isEnum()) {
            final EnumFormatType enumFormatType = ReflectionUtils.getAnnotationValueOrDefault(field, EnumFormat.class, EnumFormat::value, EnumFormat.DEFAULT);
            return new EnumPropertyType<>((Class<? extends Enum>) type, enumFormatType);
        } else if (type == Integer.TYPE || type == Integer.class) {
            return ReflectionUtils.getAnnotationValueOrDefault(field, NumberFormat.class, NumberFormat::value, NumberFormat.DEFAULT);
        } else if (type.isArray()) {
            final DelimiterType delimiterType = ReflectionUtils.getAnnotationValueOrDefault(field, Delimiter.class, Delimiter::value, Delimiter.DEFAULT);
            final Class<?> componentType = type.getComponentType();
            final BeanLineParser<?> componentLineParser = createForType(componentType);

        } else if (type.isAssignableFrom(Collection.class)) {
            final DelimiterType delimiterType = ReflectionUtils.getAnnotationValueOrDefault(field, Delimiter.class, Delimiter::value, Delimiter.DEFAULT);
        }
        if (field.isAnnotationPresent(Nested.class)) {
            return new NestedPropertyType<>(type,
                    ReflectionUtils.getProperties(type)
                            .collect(toMap(Property::getName, BeanLineParser::getPropertyType)));
        }
        return new StringPropertyType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parse(Iterator<String> lines) {
        final T t = newInstance();
        lines.forEachRemaining(line -> {
            final String[] keyValue = line.split(keyValueDelimiter, 2);
            if (keyValue.length != 2) {
                return;
            }
            final String key = keyValue[0].trim();
            final String value = keyValue[1].trim();
            final String propertyName = caseFormat.to(CaseFormat.LOWER_CAMEL, key);
            final PropertyType<?> propertyType = propertyTypes.get(key);
            if (propertyType == null) {
                return;
            }
            final Object readValue = propertyType.parse(value);
            ReflectionUtils.setProperty(t, propertyName, readValue);
        });
        return t;
    }

    private T newInstance() {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InternalError(e);
        }
    }

    public static void main(String[] args) {
        final BeanLineParser<ServiceInfo> parser = createForType(ServiceInfo.class);
        System.out.println(parser.parse(Arrays.asList(
                "SERVICE_NAME      : MongoDB",
                "TYPE              : 10   WIN32_OWN_PROCESS",
                "STATE             : 0  STOPPED",
                "WIN32_EXIT_CODE   : 0  (0x0)",
                "SERVICE_EXIT_CODE : 0  (0x0)",
                "CHECKPOINT        : 0x0",
                "WAIT_HINT         : 0x0"
        )));
    }

}
