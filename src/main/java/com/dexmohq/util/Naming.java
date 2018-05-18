package com.dexmohq.util;

import com.google.common.base.CaseFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Naming {
    CaseFormat format();

    CaseFormat DEFAULT_FORMAT = CaseFormat.LOWER_CAMEL;
}
