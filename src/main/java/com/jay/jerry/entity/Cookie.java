package com.jay.jerry.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/12/6
 **/
@Builder
@Getter
public class Cookie {
    private String name;
    private String value;

    private int version;
    private String domain;
    private String path;
    private int maxAge = -1;
}
