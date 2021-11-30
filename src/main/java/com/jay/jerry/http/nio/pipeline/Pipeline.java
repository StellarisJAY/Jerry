package com.jay.jerry.http.nio.pipeline;

import java.io.IOException;
import java.util.LinkedList;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class Pipeline {
    private LinkedList<PipelineTask> tasks = new LinkedList<>();

    public void process(ChannelContext context) throws IOException {
        for (PipelineTask task : tasks) {
            if(!task.run(context)){
                context.channel().close();
                break;
            }
        }
    }

    public void addLast(PipelineTask task){
        if(task != null){
            tasks.addLast(task);
        }
    }
}
