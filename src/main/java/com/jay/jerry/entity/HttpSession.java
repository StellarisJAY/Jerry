package com.jay.jerry.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/12/6
 **/
@Builder
public class HttpSession {
    private String sessionId;
    private Map<String, Object> storage;

    /**
     * session创建时间
     */
    private long activeTime;

    public void put(String name, Object value){
        storage.put(name, value);
    }

    public Object get(String name){
        return storage.get(name);
    }

    public boolean contains(String name){
        return storage.containsKey(name);
    }

    public Object remove(String name){
        return storage.remove(name);
    }

    public long getActiveTime(){
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public String getSessionId(){
        return sessionId;
    }
}
