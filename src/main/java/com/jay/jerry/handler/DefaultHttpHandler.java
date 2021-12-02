package com.jay.jerry.handler;

import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.constant.HttpHeaders;
import com.jay.jerry.constant.HttpStatus;
import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpResponse;
import com.jay.jerry.exception.HttpException;
import com.jay.jerry.exception.MethodNotAllowedException;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/12/2
 **/
public abstract class DefaultHttpHandler implements HttpHandler {

    @Override
    public final void handle(HttpRequest request, HttpResponse response) throws HttpException {
        String method = request.getMethod();
        // 根据方法执行handler
        if(HttpConstants.METHOD_OPTIONS.equalsIgnoreCase(method)){
            this.handleOptions(request, response);
        }
        else if(HttpConstants.METHOD_GET.equalsIgnoreCase(method)){
            this.handleGet(request, response);
        }
        else if(HttpConstants.METHOD_POST.equalsIgnoreCase(method)){
            this.handlePost(request, response);
        }
        else{
            throw new MethodNotAllowedException("method: " + method + " not allowed for " + request.getRequestUrl());
        }
    }

    @Override
    public final void handleOptions(HttpRequest request, HttpResponse response) {
        response.setHeader(HttpHeaders.ALLOW, "OPTIONS, GET, POST");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, "0");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain;charset=utf-8");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGINS, "*");
        response.setStatus(HttpStatus.OK);
    }

    @Override
    public abstract void handleGet(HttpRequest request, HttpResponse response);

    @Override
    public abstract void handlePost(HttpRequest request, HttpResponse response);
}
