package com.jay.jerry.filter;

import com.jay.jerry.ioc.BeanRegistry;

import java.util.*;

/**
 * <p>
 *  过滤器容器
 *  记录系统中所有的过滤器，并以优先级排序
 * </p>
 *
 * @author Jay
 * @date 2021/12/7
 **/
public class FilterContainer {
    /**
     * 过滤器容器
     * 以优先级排序，优先级越大越靠前。
     * 优先级相同时，通过过滤器名称排序。
     */
    private static SortedSet<AbstractFilter> container = new TreeSet<>(new Comparator<AbstractFilter>() {
        @Override
        public int compare(AbstractFilter o1, AbstractFilter o2) {
            return o1.getPriority() == o2.getPriority() ? o1.getName().compareTo(o2.getName()) : o1.getPriority() - o2.getPriority();
        }
    });

    /**
     * 获取过滤器集合副本
     * @return List
     */
    public static List<AbstractFilter> getFilters(){
        return new ArrayList<>(container);
    }

    /**
     * 注册过滤器
     * 从BeanRegistry获得单例对象，并注册到过滤器容器中
     * @param filterClazz class
     */
    public static void registerFilter(Class<?> filterClazz){
        com.jay.jerry.annotation.Filter filterAnnotation = filterClazz.getDeclaredAnnotation(com.jay.jerry.annotation.Filter.class);
        if(filterAnnotation != null){
            AbstractFilter instance = (AbstractFilter)BeanRegistry.getInstance(filterClazz);
            container.add(instance);
        }
    }

    /**
     * 注册所有过滤器类
     * @param filters filters
     */
    public static void registerAll(List<Class<?>> filters){
        for(Class<?> filter : filters){
            registerFilter(filter);
        }
    }
}
