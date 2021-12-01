package com.jay.jerry.handler;

import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpResponse;

/**
 * Handler接口
 * @author Jay
 */
public interface HttpHandler {

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
}
