package com.jay.jerry.entity;

import com.jay.jerry.constant.JerryConstants;
import com.jay.jerry.session.SessionContainer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  HTTP请求
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
@Builder
@Getter
@ToString
public class HttpRequest {
    private String method="GET";
    private String protocol;
    private String requestUrl;
    private Map<String,String> params;
    private Map<String, String> headers;

    private Map<String, Cookie> cookies;
    private String sessionId;

    private List<MultipartFile> files;

    public String getHeader(String name){
        return headers.get(name);
    }

    public void setHeader(String name, String value){
        headers.put(name, value);
    }

    public String getParameter(String name){
        if(params == null){
            return null;
        }
        return params.get(name);
    }

    public void setParameter(String name, String value){
        if(params == null){
            params = new HashMap<>();
        }
        params.put(name, value);
    }

    public void addFile(MultipartFile file){
        if(files == null){
            files = new ArrayList<>();
        }
        files.add(file);
    }

    public void setCookie(Cookie cookie){
        cookies.put(cookie.getName(), cookie);
    }

    public Cookie getCookie(String name){
        return cookies.get(name);
    }

    /**
     * 获取session
     * @return HttpSession
     */
    public HttpSession getSession(){
        Cookie sessionIdCookie = cookies.get(JerryConstants.COOKIES_SESSION_TAG);
        if(sessionIdCookie == null){
            return null;
        }
        String sessionId = sessionIdCookie.getValue();
        HttpSession session = SessionContainer.getSession(sessionId);
        // session不存在，创建新session
        if(session == null){
            session = SessionContainer.newSession();
            // sessionId记录在cookie中
            setCookie(Cookie.builder().name(JerryConstants.COOKIES_SESSION_TAG).value(session.getSessionId()).build());
        }
        return session;
    }
}
