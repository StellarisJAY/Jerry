package com.jay.jerry.exception;

import com.jay.jerry.constant.HttpStatus;

/**
 * <p>
 *  403 forbidden
 * </p>
 *
 * @author Jay
 * @date 2021/12/7
 **/
public class ForbiddenException extends HttpException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
