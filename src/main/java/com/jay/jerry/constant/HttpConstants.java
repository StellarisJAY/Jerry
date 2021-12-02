package com.jay.jerry.constant;

/**
 * <p>
 *  HTTP协议常量
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class HttpConstants {

    /**
     * CR
     */
    public static final byte CR = (byte)'\r';
    /**
     * LF
     */
    public static final byte LF = (byte)'\n';
    /**
     * CRLF
     */
    public static final String CRLF = "\r\n";


    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    public static final String SPACE = " ";

    /**
     * 最大头部长度，避免过大的请求头
     */
    public static final int MAX_HEADER_LENGTH = 8 * 1024;

    /**
     * 默认协议版本
     */
    public static final String HTTP_1_1 = "HTTP/1.1";
}
