package com.jay.jerry.exception;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class BadRequestException extends Exception {
    public BadRequestException(String message){
        super(message);
    }
}
