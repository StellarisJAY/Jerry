package com.jay.jerry.http.nio;

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
public class HttpEncoder extends PipelineTask {
    @Override
    public boolean run(ChannelContext context) {

        return false;
    }
}
