package com.jay.jerry.ioc;

import com.jay.jerry.annotation.Handler;
import com.jay.jerry.ioc.annotation.Construct;
import com.jay.jerry.ioc.annotation.Value;
import com.jay.jerry.util.PropertiesUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
public class BeanRegistry {
    /**
     * 单例池
     */
    private static final ConcurrentHashMap<Class<?>, Object> singletons = new ConcurrentHashMap<>(256);

    public static void register(Class<?> clazz){
        synchronized (singletons){
            if(singletons.containsKey(clazz)){
                throw new IllegalAccessError("class already has an instance");
            }
            singletons.put(clazz, createInstance(clazz));
        }
    }

    public static <T> T getInstance(Class<T> clazz){
        return (T)singletons.get(clazz);
    }

    private static Constructor<?> chooseConstructor(Class<?> clazz){
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> chosenConstructor = null, noArgsConstructor = null;
        for(Constructor<?> constructor : constructors){
            if(constructor.isAnnotationPresent(Construct.class)){
                chosenConstructor = constructor;
                break;
            }
            if(constructor.getParameterCount() == 0){
                noArgsConstructor = constructor;
            }
        }
        if(chosenConstructor == null && noArgsConstructor == null){
            throw new RuntimeException("can not find constructor for class: " + clazz);
        }
        chosenConstructor = chosenConstructor == null ? noArgsConstructor : chosenConstructor;

        return chosenConstructor;
    }

    /**
     * 执行构造方法
     * @param constructor constructor
     * @return Object
     * @throws IllegalAccessException e
     * @throws InvocationTargetException e
     * @throws InstantiationException e
     */
    private static Object invokeConstructor(Constructor<?> constructor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        constructor.setAccessible(true);
        // 参数列表
        Parameter[] declaredParameters = constructor.getParameters();
        Object[] constructorParams = new Object[declaredParameters.length];
        int index = 0;
        for(Parameter parameter : declaredParameters){
            // 参数没有@Value注解，无法得到值
            if(!parameter.isAnnotationPresent(Value.class)){
                throw new RuntimeException("can find value for constructor parameter : " + parameter.getName() + ", consider add an @Value annotation");
            }
            String key;
            // @Value没有值
            if((key = parameter.getAnnotation(Value.class).value()).isEmpty()){
                throw new RuntimeException("missing value attribute in @Value annotation");
            }
            // 注入参数
            if(parameter.getType() == Integer.class){
                constructorParams[index++] = PropertiesUtil.getInt(key);
            }
            else{
                constructorParams[index++] = PropertiesUtil.get(key);
            }
        }
        // 执行构造方法
        return constructor.newInstance(constructorParams);
    }


    /**
     * 注入属性
     * @param fields 属性列表
     * @param instance 实例
     * @throws IllegalAccessException Exception
     */
    private static void populateFields(Field[] fields, Object instance) throws IllegalAccessException {
        for(Field field : fields){
            // 是否有@Value
            if(field.isAnnotationPresent(Value.class)){
                String propertyKey;
                // 获取key
                if((propertyKey = field.getAnnotation(Value.class).value()).isEmpty()){
                    throw new RuntimeException("missing value attribute in @Value");
                }
                field.setAccessible(true);
                if(field.getType() == Integer.class){
                    field.set(instance, PropertiesUtil.getInt(propertyKey));
                }
                else{
                    field.set(instance, PropertiesUtil.get(propertyKey));
                }
            }
        }
    }

    /**
     * 创建类实例
     * @param clazz 类
     * @return Object
     */
    private static Object createInstance(Class<?> clazz){
        // 选择构造方法
        Constructor<?> constructor = chooseConstructor(clazz);
        try {
            // 执行构造方法
            Object instance = invokeConstructor(constructor);

            // 注入属性
            Field[] fields = clazz.getDeclaredFields();
            populateFields(fields, instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("unable to create instance for class: " + clazz, e);
        }
    }

    public static List<Class<?>> getClazzWithAnnotation(Class<? extends Annotation> annotation){
        Set<Map.Entry<Class<?>, Object>> entries = singletons.entrySet();
        List<Class<?>> result = new ArrayList<>();
        for(Map.Entry<Class<?>, Object> entry : entries){
            if(entry.getKey().isAnnotationPresent(annotation)){
                result.add(entry.getKey());
            }
        }
        return result;
    }
}
