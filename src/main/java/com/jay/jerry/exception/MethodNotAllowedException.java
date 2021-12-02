package com.jay.jerry.exception;

import com.jay.jerry.constant.HttpStatus;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/12/2
 **/
public class MethodNotAllowedException extends HttpException {
    public MethodNotAllowedException(String message) {
        super(message, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
