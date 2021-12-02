package com.jay.jerry.handler;

import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpResponse;
import com.jay.jerry.exception.HttpException;

/**
 * Handler接口
 * @author Jay
 */
public interface HttpHandler {

    /**
     * handle request
     * @param request request
     * @param response response
     */
    void handle(HttpRequest request, HttpResponse response) throws HttpException;

    /**
     * 处理GET
     * @param request request
     * @param response response
     */
    void handleGet(HttpRequest request, HttpResponse response);

    /**
     * 处理POST
     * @param request request
     * @param response response
     */
    void handlePost(HttpRequest request, HttpResponse response);

    /**
     * 处理OPTIONS
     * @param request request
     * @param response response
     */
    void handleOptions(HttpRequest request, HttpResponse response);
}
