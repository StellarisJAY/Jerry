package com.jay.jerry.exception;

import com.jay.jerry.constant.HttpStatus;

/**
 * <p>
 *  NotFound
 *  404
 * </p>
 *
 * @author Jay
 * @date 2021/12/2
 **/
public class NotFoundException extends HttpException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
