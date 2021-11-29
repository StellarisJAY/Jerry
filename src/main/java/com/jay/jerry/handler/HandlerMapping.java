package com.jay.jerry.handler;

import com.jay.jerry.annotation.Handler;
import com.jay.jerry.ioc.BeanRegistry;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  HandlerMapping
 *  请求路径与处理器的映射
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
public class HandlerMapping {
    private static final ConcurrentHashMap<String, Class<?>> HANDLERS = new ConcurrentHashMap<>();

    public static void registerHandler(Class<?> handlerClazz){
        Handler annotation = handlerClazz.getDeclaredAnnotation(Handler.class);
        String path = null;
        if(annotation != null && !(path = annotation.value()).isEmpty()){
            HANDLERS.putIfAbsent(path, handlerClazz);
        }
    }

    public static void registerAll(Collection<Class<?>> handlers){
        for (Class<?> handler : handlers) {
            registerHandler(handler);
        }
    }

    public static HttpHandler getHandler(String url){
        Class<?> handlerClazz = HANDLERS.get(url);
        if(handlerClazz != null){
            return (HttpHandler)BeanRegistry.getInstance(handlerClazz);
        }
        return null;
    }
}
