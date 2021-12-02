package com.jay.jerry.http.nio.pipeline;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  任务管线上下文
 *  流水线任务之间可以通过上下文的数据缓存传递数据。
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class ChannelContext {
    /**
     * 数据缓存
     */
    private Map<String, Object> contextCache = new HashMap<>();
    /**
     * channel
     */
    private SocketChannel channel;

    public ChannelContext(SocketChannel channel) {
        this.channel = channel;
    }

    public Object get(String name){
        return contextCache.get(name);
    }
    public void put(String name, Object object){
        contextCache.put(name, object);
    }
    public SocketChannel channel(){
        return channel;
    }
}
