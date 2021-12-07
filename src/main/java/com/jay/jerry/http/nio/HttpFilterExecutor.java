package com.jay.jerry.http.nio;

import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpResponse;
import com.jay.jerry.exception.ForbiddenException;
import com.jay.jerry.filter.AbstractFilter;
import com.jay.jerry.filter.FilterContainer;
import com.jay.jerry.http.nio.pipeline.ChannelContext;
import com.jay.jerry.http.nio.pipeline.PipelineTask;

import java.util.List;

/**
 * <p>
 *  过滤器执行器
 *  流水线任务，在decode解析完成后执行过滤器链
 * </p>
 *
 * @author Jay
 * @date 2021/12/7
 **/
public class HttpFilterExecutor extends PipelineTask {

    @Override
    public boolean run(ChannelContext context) {
        HttpRequest request = (HttpRequest)context.get("request");
        List<AbstractFilter> filters = FilterContainer.getFilters();
        for(AbstractFilter filter : filters){
            // 过滤器状态
            if(!filter.filter(request)){
                // 不放行，上下文存储异常response
                context.put("response", HttpResponse.errorResponse(new ForbiddenException("request blocked by filter: " + filter.getName())));
                // 不执行下级pipeline任务
                return false;
            }
        }
        // 所有过滤器都放行
        return true;
    }
}
