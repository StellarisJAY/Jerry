package com.jay.jerry.http.nio;

import com.jay.jerry.constant.HttpHeaders;
import com.jay.jerry.constant.HttpStatus;
import com.jay.jerry.constant.JerryConstants;
import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpResponse;
import com.jay.jerry.exception.HttpException;
import com.jay.jerry.exception.InternalErrorException;
import com.jay.jerry.exception.NotFoundException;
import com.jay.jerry.handler.HandlerMapping;
import com.jay.jerry.handler.HttpHandler;
import com.jay.jerry.http.nio.pipeline.ChannelContext;
import com.jay.jerry.http.nio.pipeline.PipelineTask;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;

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
        try{
            HttpRequest request = (HttpRequest)context.get("request");
            if(request == null || request.getRequestUrl() == null){
                throw new InternalError("request parsing failed");
            }

            // 获取handler
            HttpHandler handler = HandlerMapping.getHandler(request.getRequestUrl());
            // NOT FOUND
            if(handler == null){
                throw new NotFoundException("no handler for " + request.getMethod() + " " + request.getRequestUrl());
            }
            HttpResponse response = HttpResponse.builder().protocol(request.getProtocol()).headers(new HashMap<>()).build();

            handler.handle(request, response);
            // response写入sessionId的Cookie
            response.setCookie(request.getCookie(JerryConstants.COOKIES_SESSION_TAG));
            response.setHeader(HttpHeaders.DATE, LocalDateTime.now().toString());
            response.setStatus(HttpStatus.OK);
            context.put("response", response);
            return false;
        }catch (HttpException e){
            log.error(e.getMessage());
            context.put("response", HttpResponse.errorResponse(e));
            return false;
        }catch (Exception e){
            log.error("error ", e);
            context.put("response", HttpResponse.errorResponse(new InternalErrorException(e.getMessage())));
            return false;
        }
    }


}
