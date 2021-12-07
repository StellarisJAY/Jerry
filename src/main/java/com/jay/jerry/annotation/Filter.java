package com.jay.jerry.annotation;

import com.jay.jerry.ioc.annotation.IOC;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jay
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@IOC
public @interface Filter {
}
