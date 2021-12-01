package com.jay.jerry.http.nio;

import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpResponse;
import com.jay.jerry.handler.HandlerMapping;
import com.jay.jerry.handler.HttpHandler;
import com.jay.jerry.http.nio.pipeline.ChannelContext;
import com.jay.jerry.http.nio.pipeline.PipelineTask;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *  HttpRequestDispatcher，类似SpringMVC的DispatcherServlet
 *  负责根据path找到对应的Handler处理
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
@Slf4j
public class HttpRequestDispatcher extends PipelineTask {
    @Override
    public boolean run(ChannelContext context) {
        HttpRequest request = (HttpRequest)context.get("request");
        if(request == null || request.getRequestUrl() == null){
            return false;
        }
        // 获取handler
        HttpHandler handler = HandlerMapping.getHandler(request.getRequestUrl());

        if(handler == null){
            log.info("no handler found for {} {}", request.getMethod(), request.getRequestUrl());
            context.put("error", new RuntimeException("no handler found"));
            return false;
        }

        String method = request.getMethod();
        HttpResponse response = new HttpResponse();

        // 根据方法执行handler
        if(HttpConstants.METHOD_GET.equalsIgnoreCase(method)){
            handler.handleGet(request, response);
        }
        else if(HttpConstants.METHOD_POST.equalsIgnoreCase(method)){
            handler.handlePost(request, response);
        }

        return false;
    }
}
