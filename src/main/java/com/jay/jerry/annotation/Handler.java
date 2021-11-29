package com.jay.jerry.annotation;

import com.jay.jerry.ioc.annotation.IOC;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@IOC
public @interface Handler {
    String value();
}
