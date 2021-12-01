package com.jay.jerry.entity;

import com.jay.jerry.constant.HttpStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
@Getter
@Builder
@ToString
public class HttpResponse {
    private String protocol;
    @Setter
    private HttpStatus status;

    private Map<String, String> headers = new HashMap<>();

    private byte[] content;

    public void setHeader(String name, String value){
        headers.put(name, value);
    }

    public String getHeader(String name){
        return headers.get(name);
    }
}
