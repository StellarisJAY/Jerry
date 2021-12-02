package com.jay.jerry.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * <p>
 *  HTTP请求
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
@Builder
@Getter
@Setter
@ToString
public class HttpRequest {
    private String method="GET";
    private String protocol;
    private String requestUrl;
    private Map<String,String> params;

    private Map<String, String> headers;

    private byte[] content;
}
