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
public class ExceptionPage {
    private HttpStatus status;
    private String message;

    public ExceptionPage(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }


    public String getHTML(){
        return
                "<html>" + "<title>" +  status.getMessage()  + "</title>" +
                "<h1>" + status.getCode() + " " + status.getMessage() + "</h1><p>" + message + "</p></html>";
    }
}
