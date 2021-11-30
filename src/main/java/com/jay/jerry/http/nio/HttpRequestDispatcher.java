package com.jay.jerry.http.nio;

import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.http.nio.pipeline.ChannelContext;
import com.jay.jerry.http.nio.pipeline.PipelineTask;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class HttpRequestDispatcher extends PipelineTask {
    @Override
    public boolean run(ChannelContext context) {
        HttpRequest request = (HttpRequest)context.get("request");
        System.out.println(request);
        return false;
    }
}
