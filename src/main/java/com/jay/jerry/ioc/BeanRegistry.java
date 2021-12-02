package com.jay.jerry.ioc;

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
 *  IOC容器，单例池
 *  目前Jerry的IOC容器仅支持单例对象，并且没有@Autowired的依赖注入。
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
public class BeanRegistry {
    /**
     * 单例池
     * 通过class找单例
     */
    private static final ConcurrentHashMap<Class<?>, Object> singletons = new ConcurrentHashMap<>(256);

    /**
     * 注册单例对象
     * @param clazz 类
     */
    public static void register(Class<?> clazz){
        synchronized (singletons){
            if(singletons.containsKey(clazz)){
                throw new IllegalAccessError("class already has an instance");
            }
            // 自动创建单例
            singletons.put(clazz, createInstance(clazz));
        }
    }

    /**
     * 获取单例
     * @param clazz 类型
     * @param <T> T
     * @return instance
     */
    public static <T> T getInstance(Class<T> clazz){
        return (T)singletons.get(clazz);
    }

    /**
     * 构造方法选择
     * JerryIOC优先选择有@Construct注解的构造方法。
     * 如果没有，则选择空参构造方法。
     * JerryIOC会对有@Value注解的参数自动注入值
     * @param clazz 类
     * @return Constructor
     */
    private static Constructor<?> chooseConstructor(Class<?> clazz){
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> chosenConstructor = null, noArgsConstructor = null;
        for(Constructor<?> constructor : constructors){
            // 找到@Construct
            if(constructor.isAnnotationPresent(Construct.class)){
                chosenConstructor = constructor;
                break;
            }
            // 空参
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

    /**
     * 寻找拥有Annotation的单例
     * 该方法会遍历整个单例池，时间复杂度为O(N)。
     * 所以请不要在业务逻辑中过多使用该方法。
     * @param annotation annotation
     * @return List
     */
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
