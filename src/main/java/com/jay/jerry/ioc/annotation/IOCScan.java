package com.jay.jerry.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启IOC扫描
 * @author Jay
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IOCScan {
    /**
     * 扫描路径
     * @return String
     */
    String basePackage();
}
