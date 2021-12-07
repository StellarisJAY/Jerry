package com.jay.jerry.filter;

import com.jay.jerry.entity.HttpRequest;

/**
 * <p>
 *  Filter抽象类
 * </p>
 *
 * @author Jay
 * @date 2021/12/7
 **/
public abstract class AbstractFilter {

    /**
     * 过滤器名称
     */
    private final String name;
    /**
     * 过滤器优先级，数值越大，优先级越高
     */
    private final int priority;

    /**
     * 排除路径
     */
    private final String[] excludePatterns;

    public AbstractFilter(String name, int priority, String[] excludePatterns) {
        this.name = name;
        this.priority = priority;
        this.excludePatterns = excludePatterns;
    }

    /**
     * pipelineTask通过调用该方法执行过滤器逻辑
     * 该方法主要完成对excludePattern的排除
     * 具体的过滤器逻辑在doFilter中实现
     * @param request request
     * @return boolean 是否放行
     */
    public final boolean filter(HttpRequest request){
        String url = request.getRequestUrl();
        if(excludePatterns != null && excludePatterns.length > 0){
            for(String pattern : excludePatterns){
                if(url.matches(pattern)){
                    return true;
                }
            }
        }
        return doFilter(request);
    }

    /**
     * 过滤逻辑
     * @param request request
     * @return 是否放行
     */
    public abstract boolean doFilter(HttpRequest request);

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }
}
