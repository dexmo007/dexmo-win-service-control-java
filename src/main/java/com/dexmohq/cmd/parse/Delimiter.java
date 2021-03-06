package com.dexmohq.cmd.parse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Henrik Drefs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Delimiter {

    DelimiterType value();

    DelimiterType DEFAULT = DelimiterType.EMPTY_LINE;

}
