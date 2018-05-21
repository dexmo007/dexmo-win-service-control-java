package com.dexmohq.cmd.parse;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * @author Henrik Drefs
 */
public interface LineParser<T> {

    T parse(Iterator<String> lines);

    default T parse(Stream<String> lines) {
        return parse(lines.iterator());
    }

    default T parse(Iterable<String> lines){
        return parse(lines.iterator());
    }
}
