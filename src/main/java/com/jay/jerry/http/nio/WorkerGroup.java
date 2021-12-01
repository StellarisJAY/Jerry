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

    private Pipeline inputPipeline;
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
                    ChannelContext context = new ChannelContext(channel);
                    inputPipeline.process(context);
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
