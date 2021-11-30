package com.jay.jerry.constant;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class HttpConstants {
    public static final byte CR = (byte)'\r';
    public static final byte LF = (byte)'\n';
    public static final String CRLF = "\r\n";

    /**
     * double CRLF at the end of headers
      */
    public static final String HEADER_ENDING = "\r\n\r\n";

    public static final String SPACE = " ";
    public static final int MAX_LENGTH = 8 * 1024;
}
