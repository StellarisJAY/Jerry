package com.jay.jerry.http.nio.pipeline;

import java.io.IOException;
import java.util.LinkedList;

/**
 * <p>
 *  处理流水线
 *  请求按照流水线的任务顺序被处理.
 *  任务之间通过上下文传递数据
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
