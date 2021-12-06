package com.jay.jerry.session;

import com.jay.jerry.constant.JerryConstants;
import com.jay.jerry.entity.Cookie;
import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpSession;
import com.jay.jerry.util.PropertiesUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/12/6
 **/
public class SessionContainer {
    private static ConcurrentHashMap<String, HttpSession> container = new ConcurrentHashMap<>();

    public static HttpSession getSession(String sessionId){
        HttpSession session = container.get(sessionId);
        if(session == null){
            return null;
        }
        long currentTime = System.currentTimeMillis();
        String timeoutProperty = PropertiesUtil.get("session-timeout");
        int sessionTimeout = timeoutProperty == null ? JerryConstants.SESSION_TIMEOUT : Integer.parseInt(timeoutProperty);

        if(currentTime - session.getActiveTime() > sessionTimeout){
            container.remove(sessionId);
            return null;
        }
        session.setActiveTime(System.currentTimeMillis());
        return session;
    }

    public static boolean containsSession(String sessionId){
        return container.containsKey(sessionId);
    }

    public static void putSession(String sessionId, HttpSession session){
        container.put(sessionId, session);
    }

    public static HttpSession newSession(){
        String sessionId = UUID.randomUUID().toString();
        HttpSession session = HttpSession.builder()
                .sessionId(sessionId)
                .storage(new HashMap<>())
                .activeTime(System.currentTimeMillis())
                .build();

        SessionContainer.putSession(sessionId, session);
        return session;
    }
}
