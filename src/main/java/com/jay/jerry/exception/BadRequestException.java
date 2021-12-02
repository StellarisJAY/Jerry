package com.jay.jerry.exception;

import com.jay.jerry.constant.HttpStatus;

/**
 * <p>
 *
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
