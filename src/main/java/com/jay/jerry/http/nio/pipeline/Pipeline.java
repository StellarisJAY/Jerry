package com.jay.jerry.http.nio.pipeline;

import java.util.LinkedList;

/**
 * <p>
 *  任务管线
 *  以流水线形式处理数据。
 *  数据在流水线中通过上下文缓存传递。
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class Pipeline {
    private LinkedList<PipelineTask> tasks = new LinkedList<>();

    public void process(ChannelContext context) {
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
