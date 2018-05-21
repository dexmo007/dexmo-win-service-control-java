package com.dexmohq.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * @author Henrik Drefs
 */
@UtilityClass
public class StreamUtils {

    public static Stream<String> fromInputStream(InputStream in) {
        return new BufferedReader(new InputStreamReader(in)).lines();
    }

    public static class Collectors {

        static <T> Collector<T, Stream.Builder<T>, Stream<T>> streaming() {
            return new Collector<>() {
                @Override
                public Supplier<Stream.Builder<T>> supplier() {
                    return Stream::builder;
                }

                @Override
                public BiConsumer<Stream.Builder<T>, T> accumulator() {
                    return Stream.Builder::add;
                }

                @Override
                public BinaryOperator<Stream.Builder<T>> combiner() {
                    return (l, rs) -> {
                        rs.build().forEach(l::add);
                        return l;
                    };
                }

                @Override
                public Function<Stream.Builder<T>, Stream<T>> finisher() {
                    return Stream.Builder::build;
                }

                @Override
                public Set<Characteristics> characteristics() {
                    return Collections.emptySet();
                }
            };
        }
    }

}
