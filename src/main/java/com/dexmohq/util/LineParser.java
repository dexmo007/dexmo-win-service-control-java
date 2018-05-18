package com.dexmohq.util;

import com.dexmohq.ServiceInfo;
import com.dexmohq.ServiceType;
import com.google.common.base.CaseFormat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.beanutils.BeanUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LineParser<T> {

    private final Class<T> type;

    private final Constructor<T> constructor;

    private final String keyValueDelimiter;

    private final CaseFormat caseFormat;

    private final Map<String, PropertyType<?>> propertyTypes;

    public static <T> LineParser<T> createForType(Class<T> type) {
        final Constructor<T> constructor;
        try {
            constructor = type.getDeclaredConstructor();
            constructor.trySetAccessible();
        } catch (NoSuchMethodException e) {
            throw new LineParserException("No default constructor for type: " + type);
        }
        final Naming naming = type.getAnnotation(Naming.class);
        final CaseFormat caseFormat;
        if (naming == null) {
            caseFormat = Naming.DEFAULT_FORMAT;
        } else {
            caseFormat = naming.format();
        }
        final Map<String, PropertyType<?>> types = getProperties(type)
                .collect(Collectors.toMap(pd -> CaseFormat.LOWER_CAMEL.to(caseFormat, pd.getName()),
                        LineParser::getPropertyType));
        return new LineParser<>(type, constructor, ":", caseFormat, types);
    }

    @SuppressWarnings("unchecked")
    private static PropertyType<?> getPropertyType(Property property) {
        final PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        final Class<?> type = propertyDescriptor.getPropertyType();
        final Field field = property.getField();
        final PropertyType<?> propertyType = getPropertyType(type, field);
        final Skip skip = field.getAnnotation(Skip.class);
        if (skip != null && skip.n() != 0) {
            return new Extractor<>(propertyType, skip.split(), skip.n());
        }
        return propertyType;
    }

    @SuppressWarnings("unchecked")
    private static PropertyType<?> getPropertyType(Class<?> type, Field field) {
        if (type.isEnum()) {
            final EnumFormatType enumFormatType;
            final EnumFormat enumFormat = field.getAnnotation(EnumFormat.class);
            if (enumFormat == null) {
                enumFormatType = EnumFormat.DEFAULT;
            } else {
                enumFormatType = enumFormat.value();
            }
            return new EnumPropertyType<>((Class<? extends Enum>) type, enumFormatType);
        } else if (type == Integer.TYPE || type == Integer.class) {
            final NumberFormat numberFormat = field.getAnnotation(NumberFormat.class);
            if (numberFormat == null) {
                return NumberFormat.DEFAULT;
            } else {
                return numberFormat.value();
            }
        }
        return new StringPropertyType();
    }

    public T parse(Stream<String> lines) {
        return parse(lines.iterator());
    }

    public T parse(Iterable<String> lines) {
        return parse(lines.iterator());
    }

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
            setProperty(t, propertyName, readValue);
        });
        return t;
    }

    private void setProperty(T t, String name, Object value) {
        try {
            BeanUtils.setProperty(t, name, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private T newInstance() {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InternalError(e);
        }
    }

    public static void main(String[] args) {
        final LineParser<ServiceInfo> parser = createForType(ServiceInfo.class);
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

    @Value
    private static class Property {
        PropertyDescriptor propertyDescriptor;
        Field field;

        String getName() {
            return propertyDescriptor.getName();
        }
    }

    private static Stream<Property> getProperties(Class<?> type) {
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
}
