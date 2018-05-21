package com.dexmohq.cmd.parse;

import lombok.experimental.UtilityClass;

/**
 * @author Henrik Drefs
 */
@UtilityClass
public class LineParsing {

    public static <T> LineParser<T> createLineParser(Class<T> type) {
        if (type.isAnnotationPresent(Results.class)) {
            return ResultsLineParser.createForResultsType(type);
        }
        return BeanLineParser.createForType(type);
    }

}
