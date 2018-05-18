package com.dexmohq.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface EnumFormat {

    EnumFormatType value();

    EnumFormatType DEFAULT = EnumFormatType.VALUE;

}
