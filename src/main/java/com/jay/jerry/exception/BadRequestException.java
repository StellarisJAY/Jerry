package com.jay.jerry.exception;

import com.jay.jerry.constant.HttpStatus;

/**
 * <p>
 *  BadRequest
 *  协议格式错误、请求头格式错误、数据类型错误、数据超长
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class BadRequestException extends HttpException {
    public BadRequestException(String message){
        super(message, HttpStatus.BAD_REQUEST);
    }
}
