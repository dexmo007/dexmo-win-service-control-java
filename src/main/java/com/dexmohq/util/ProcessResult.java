package com.dexmohq.util;

import lombok.Value;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Henrik Drefs
 */
@Value
public class ProcessResult<T> {

    private final Process process;
    private final boolean isError;
    private T result;

    public <R> ProcessResult<R> map(Function<T, R> mapper) {
        return new ProcessResult<>(process, isError, mapper.apply(result));
    }

}
