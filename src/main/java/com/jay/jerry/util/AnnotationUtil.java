package com.jay.jerry.util;

import java.lang.annotation.Annotation;

/**
 * <p>
 *  注解工具
 * </p>
 *
 * @author Jay
 * @date 2021/11/29
 **/
public class AnnotationUtil {
    /**
     * 找到类上的注解
     * @param clazz 类
     * @param annotationType 注解
     * @param <A> A
     * @return Annotation
     */
    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        if (annotationType == null) {
            return null;
        }
        else {
            A annotation = clazz.getDeclaredAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            } else {
                Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();
                for (Annotation declaredAnnotation : declaredAnnotations) {
                    // 排除java注解，比如@Retention会导致死递归
                    if(!isJavaAnnotation(declaredAnnotation.annotationType())){
                        A temp = findAnnotation(declaredAnnotation.annotationType(), annotationType);
                        if(temp != null){
                            return temp;
                        }
                    }
                }
                return null;
            }
        }
    }

    /**
     * 判断注解是否是java注解
     * 比如@Retention，@Target
     * @param annotation a
     * @return boolean
     */
    public static boolean isJavaAnnotation(Class<? extends Annotation> annotation){
        return annotation.getName().startsWith("java");
    }
}
