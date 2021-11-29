package com.jay.jerry.util;

import java.lang.annotation.Annotation;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/29
 **/
public class AnnotationUtil {
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

    public static boolean isJavaAnnotation(Class<? extends Annotation> annotation){
        return annotation.getName().startsWith("java");
    }
}
