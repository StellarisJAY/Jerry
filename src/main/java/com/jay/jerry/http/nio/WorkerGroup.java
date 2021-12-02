package com.jay.jerry.http.nio;

import com.jay.jerry.http.nio.pipeline.ChannelContext;
import com.jay.jerry.http.nio.pipeline.Pipeline;
import lombok.SneakyThrows;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *  Worker线程池
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class WorkerGroup {
    private ThreadPoolExecutor executor;
    private LinkedBlockingQueue<Runnable> blockingQueue;
    private int nThreads;

    /**
     * 输入管线
     * 处理客户端流入数据
     * 类似Netty的InboundHandler链
     */
    private Pipeline inputPipeline;
    /**
     * 输出管线
     * 处理服务端流出到客户端数据
     * 类似Netty的OutboundHandler链
     */
    private Pipeline outputPipeline;


    public WorkerGroup(int nThreads) {
        this.nThreads = nThreads;
        blockingQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
        executor = new ThreadPoolExecutor(nThreads, nThreads, 0, TimeUnit.MILLISECONDS, blockingQueue, new ThreadFactory() {
            final AtomicInteger idProvider = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "nio-worker-" + idProvider.getAndIncrement());
            }
        });
        inputPipeline = new Pipeline();
        outputPipeline = new Pipeline();
    }

    public WorkerGroup() {
        this(Runtime.getRuntime().availableProcessors() * 2);
    }

    public Pipeline getInputPipeline() {
        return inputPipeline;
    }

    public Pipeline getOutputPipeline() {
        return outputPipeline;
    }

    /**
     * Worker执行过程
     * @param channel channel
     */
    public void process(SocketChannel channel){
        executor.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                try {
                    /*
                        输入输出管线共用上下文
                        通过上下文缓存传递response等数据
                     */
                    ChannelContext context = new ChannelContext(channel);
                    // 执行输入管线
                    inputPipeline.process(context);
                    // 执行输出管线
                    outputPipeline.process(context);
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    channel.close();
                }
            }
        });
    }
}
