package com.jay.jerry.http.nio;

import com.jay.jerry.http.HttpServer;
import com.jay.jerry.http.nio.pipeline.Pipeline;
import com.jay.jerry.ioc.annotation.IOC;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
@IOC
@Slf4j
public class NioServer implements HttpServer {

    private Selector selector;

    private WorkerGroup workers;

    public NioServer(){
        workers = new WorkerGroup();
        Pipeline inputPipeline = workers.getInputPipeline();
        inputPipeline.addLast(new HttpDecoder());
        inputPipeline.addLast(new HttpRequestDispatcher());
        Pipeline outputPipeline = workers.getOutputPipeline();
        outputPipeline.addLast(new HttpEncoder());
    }

    @Override
    public void start(int port)  {
        try{
            selector = Selector.open();
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            channel.socket().bind(new InetSocketAddress(port));

        }catch (IOException e){

        }
    }

    @Override
    public void doService() {
        while(true){
            try{
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    try{
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        } else if (selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            workers.process(channel);
                            channel.register(selector, SelectionKey.OP_WRITE);
                        } else if(selectionKey.isWritable()){

                        }
                    }catch (Exception e){
                        selectionKey.channel().close();
                    }
                }
                selector.selectedKeys().clear();
            }catch (Exception e) {

            }
        }
    }
}
