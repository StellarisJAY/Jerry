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
public class InternalErrorException extends HttpException {
    public InternalErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
