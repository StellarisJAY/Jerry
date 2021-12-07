package com.jay.jerry.session;

import com.jay.jerry.constant.JerryConstants;
import com.jay.jerry.entity.HttpSession;
import com.jay.jerry.util.PropertiesUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  Session容器
 * </p>
 *
 * @author Jay
 * @date 2021/12/6
 **/
public class SessionContainer {
    private static ConcurrentHashMap<String, HttpSession> container = new ConcurrentHashMap<>();

    /**
     * 获取Session
     * 会判断session是否过期，过期将返回null
     * 如果没有过期，将会对session续约
     * @param sessionId sessionID
     * @return HttpSession
     */
    public static HttpSession getSession(String sessionId){
        HttpSession session = container.get(sessionId);
        if(session == null){
            return null;
        }
        long currentTime = System.currentTimeMillis();
        // 获取session超时时间
        String timeoutProperty = PropertiesUtil.get("session-timeout");
        int sessionTimeout = timeoutProperty == null ? JerryConstants.SESSION_TIMEOUT : Integer.parseInt(timeoutProperty);

        if(currentTime - session.getActiveTime() > sessionTimeout){
            // session 超时
            container.remove(sessionId);
            return null;
        }
        session.setActiveTime(System.currentTimeMillis());
        return session;
    }

    /**
     * 判断session是否存在
     * @param sessionId sessionID
     * @return boolean
     */
    public static boolean containsSession(String sessionId){
        return container.containsKey(sessionId);
    }

    /**
     * 记录session
     * @param sessionId sessionId
     * @param session session
     */
    public static void putSession(String sessionId, HttpSession session){
        container.put(sessionId, session);
    }

    /**
     * 新建session
     * @return HttpSession
     */
    public static HttpSession newSession(){
        // 用UUID生成sessionID
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
