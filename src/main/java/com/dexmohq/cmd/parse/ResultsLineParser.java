package com.dexmohq.cmd.parse;

import com.dexmohq.util.ReflectionUtils;
import com.dexmohq.win.service.ServiceInfos;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author Henrik Drefs
 */
public class ResultsLineParser<T> implements LineParser<T> {

    private final Constructor<T> constructor;

    private final Field field;

    private final ResultsType resultsType;

    private final LineParser<?> componentLineParser;

    private final Supplier<? extends Collection> bufferSupplier;

    private ResultsLineParser(Class<T> type, Field field, ResultsType<?> resultsType) {
        this.constructor = ReflectionUtils.getDefaultConstructor(type);
        this.field = field;
        this.field.trySetAccessible();
        this.resultsType = resultsType;
        this.componentLineParser = BeanLineParser.createForType(resultsType.getComponentType(field));
        this.bufferSupplier = resultsType.getBufferSupplier(field.getType());
    }

    public static <T> ResultsLineParser<T> createForResultsType(Class<T> type) {
        final Field[] fields = type.getDeclaredFields();
        if (fields.length != 1) {
            throw new IllegalArgumentException("Results class must only have one field");
        }
        final Field field = fields[0];
        final ResultsType<?> resultsType;
        if (field.getType().isArray()) {
            resultsType = ARRAY_RESULTS_TYPE;
        } else {
            resultsType = COLLECTION_RESULTS_TYPE;
        }
        final DelimiterType delimiterType = ReflectionUtils.getAnnotationValueOrDefault(field,
                Delimiter.class, Delimiter::value, DelimiterType.EMPTY_LINE);//todo
        return new ResultsLineParser<>(type, field, resultsType);
    }

    private List<List<String>> splitLines(Iterator<String> lines) {
        final List<List<String>> elements = new ArrayList<>();
        List<String> currentLines = new ArrayList<>();
        while (lines.hasNext()) {
            final String line = lines.next().trim();
            if (line.isEmpty()) {
                if (currentLines.isEmpty()) {
                    continue;
                }
                elements.add(currentLines);
                currentLines = new ArrayList<>();
            } else {
                currentLines.add(line);
            }
        }
        return elements;
    }

    @Override
    public T parse(Iterator<String> lines) {
        final T t = ReflectionUtils.newDefaultInstance(constructor);
        final Collection collection = bufferSupplier.get();
        final List<List<String>> elements = splitLines(lines);
        for (List<String> element : elements) {
            final Object parsed = componentLineParser.parse(element);
            collection.add(parsed);
        }
        try {
            field.set(t, resultsType.postProcess(collection));
        } catch (IllegalAccessException e) {
            throw new InternalError(e);
        }
        return t;
    }

    private interface ResultsType<B extends Collection> {
        Class<?> getComponentType(Field field);

        Supplier<B> getBufferSupplier(Class<?> fieldType);

        Object postProcess(B collection);
    }

    private static final ArrayResultsType ARRAY_RESULTS_TYPE = new ArrayResultsType();

    private static class ArrayResultsType implements ResultsType<ArrayList> {

        @Override
        public Class<?> getComponentType(Field field) {
            return field.getType().getComponentType();
        }

        @Override
        public Supplier<ArrayList> getBufferSupplier(Class<?> fieldType) {
            return ArrayList::new;
        }

        @Override
        public Object postProcess(ArrayList collection) {
            return collection.toArray();
        }
    }

    private static final CollectionResultsType COLLECTION_RESULTS_TYPE = new CollectionResultsType();

    private static class CollectionResultsType implements ResultsType<Collection> {

        @Override
        public Class<?> getComponentType(Field field) {
            final Class<?> type = field.getType();
            if (!Collection.class.isAssignableFrom(type)) {
                throw new IllegalStateException("Field must be a collection type");
            }
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }

        @Override
        public Supplier<Collection> getBufferSupplier(Class<?> fieldType) {
            if (fieldType == Set.class || fieldType == HashSet.class) {
                return HashSet::new;
            } else if (fieldType == TreeSet.class) {
                return TreeSet::new;
            } else if (fieldType == List.class || fieldType == ArrayList.class) {
                return ArrayList::new;
            } else if (fieldType == LinkedList.class) {
                return LinkedList::new;
            }
            return ArrayList::new;
        }

        @Override
        public Object postProcess(Collection collection) {
            return collection;
        }
    }

    public static void main(String[] args) {
        final Field field = ServiceInfos.class.getDeclaredFields()[0];
        System.out.println(field.getType());
        System.out.println(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
    }
}
