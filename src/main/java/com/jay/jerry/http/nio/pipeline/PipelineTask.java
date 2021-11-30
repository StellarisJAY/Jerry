package com.jay.jerry.http.nio.pipeline;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public abstract class PipelineTask {
    /**
     * pipeline任务执行过程
     * 返回值表示是否继续流水线
     * @param context 上下文
     * @return boolean
     */
    public abstract boolean run(ChannelContext context);
}
