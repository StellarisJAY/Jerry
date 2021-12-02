package com.jay.jerry.exception;

import com.jay.jerry.constant.HttpStatus;

/**
 * <p>
 *  Http异常
 *  所有Http异常父类
 * </p>
 *
 * @author Jay
 * @date 2021/12/2
 **/
public class HttpException extends Exception {
    /**
     * 绑定的返回状态
     */
    private HttpStatus status;

    public HttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
