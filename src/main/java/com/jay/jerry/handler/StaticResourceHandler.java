package com.jay.jerry.handler;

import com.jay.jerry.constant.ContentTypes;
import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.constant.HttpHeaders;
import com.jay.jerry.constant.HttpStatus;
import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpResponse;
import com.jay.jerry.exception.HttpException;
import com.jay.jerry.exception.InternalErrorException;
import com.jay.jerry.exception.MethodNotAllowedException;
import com.jay.jerry.exception.NotFoundException;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *  静态资源Handler，返回静态资源
 *  目前支持：html、png、gif
 * </p>
 *
 * @author Jay
 * @date 2021/12/2
 **/
public abstract class StaticResourceHandler implements HttpHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response) throws HttpException {
        if(HttpConstants.METHOD_GET.equalsIgnoreCase(request.getMethod())){
            // 获取静态资源路径
            String path = getStaticResource(request);
            // 静态资源目录
            path = "static/" + path;
            // 读取静态资源
            try(InputStream inputStream = StaticResourceHandler.class.getClassLoader().getResourceAsStream(path)){
                if(inputStream == null){
                    throw new NotFoundException("no resource at path: " + path);
                }
                byte[] buffer = new byte[1024];
                while((inputStream.read(buffer, 0, buffer.length)) != -1){
                    // 写入outputStream
                    response.out().write(buffer);
                }
            }catch (IOException e){
                throw new InternalErrorException(e.getMessage());
            }

            // 写入返回头，Content-Type、Content-Length
            response.setHeader(HttpHeaders.CONTENT_TYPE, getContentType(path));
            response.setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(response.out().size()));
        }
        else if(HttpConstants.METHOD_OPTIONS.equalsIgnoreCase(request.getMethod())){
            handleOptions(request, response);
        }
        else{// 其他方法都返回405
            throw new MethodNotAllowedException("method: " + request.getMethod() + " not allowed for " + request.getRequestUrl());
        }
    }

    /**
     * 获取静态资源相对路径
     * 即在/static/下的路径
     * @param request 请求
     * @return String
     */
    public abstract String getStaticResource(HttpRequest request);

    @Override
    public final void handleGet(HttpRequest request, HttpResponse response) {

    }

    @Override
    public final void handlePost(HttpRequest request, HttpResponse response) {

    }

    @Override
    public final void handleOptions(HttpRequest request, HttpResponse response) {
        /*
            写入OPTIONS的返回头部
         */
        response.setHeader(HttpHeaders.ALLOW, "OPTIONS, GET");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, "0");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain;charset=utf-8");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGINS, "*");
        response.setStatus(HttpStatus.OK);
    }

    private String getContentType(String path){
        int prefixIndex = path.lastIndexOf(".");
        if(prefixIndex == -1){
            throw new RuntimeException("unknown content type for resource: " + path);
        }
        String suffix = path.substring(prefixIndex + 1);
        ContentTypes contentType = ContentTypes.getContentType(suffix);
        if(contentType == null){
            throw new RuntimeException("unknown content type " + suffix);
        }
        return contentType.getContentType();
    }
}
