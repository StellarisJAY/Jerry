package com.jay.jerry.entity;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
public class HttpRequest {
    private String method="GET";

    private String protocol;

    private String requestURI;

    private String requestURL;

    private String relativeURI;

    private Map<String, String> header;

    private InputStream inputStream;

    private String sessionId;

    private boolean isGzip = false;

    private boolean isSessionCread = false;

    private String scheme = "http";

    private String basePath;

    private Map<String, List<Object>> params;

    private String queryString = "";

    private Integer contextLength = 0;


}
