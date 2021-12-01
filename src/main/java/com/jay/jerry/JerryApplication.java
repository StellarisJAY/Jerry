package com.jay.jerry;

import com.jay.jerry.annotation.Handler;
import com.jay.jerry.handler.HandlerMapping;
import com.jay.jerry.http.nio.NioServer;
import com.jay.jerry.ioc.BeanRegistry;
import com.jay.jerry.ioc.annotation.IOC;
import com.jay.jerry.ioc.annotation.IOCScan;
import com.jay.jerry.util.AnnotationUtil;
import com.jay.jerry.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
@Slf4j
public class JerryApplication {
    private static String basePackage;

    public static void run(Class<?> clazz, String...args) throws ClassNotFoundException, InterruptedException {
        if(clazz == null){
            throw new NullPointerException("parameter clazz missing");
        }
        printBanner();
        // 获取扫描路径
        IOCScan iocScan = clazz.getDeclaredAnnotation(IOCScan.class);
        if(iocScan == null || (basePackage = iocScan.basePackage()).isEmpty()){
            throw new IllegalArgumentException("can not find @IOCScan annotation and basePackage to scan");
        }
        // 组件扫描
        String path = basePackage.replace(".", "/");

        log.info("starting component scan, scan package: {}", basePackage);
        doScan(path);

        // Handler扫描
        List<Class<?>> handlerClazz = BeanRegistry.getClazzWithAnnotation(Handler.class);
        HandlerMapping.registerAll(handlerClazz);
        log.info("handler scanning finished, found {} handler classes", handlerClazz.size());

        // 启动服务器
        long serverStartBegin = System.currentTimeMillis();
        int port = PropertiesUtil.getInt("server.port");
        log.info("starting Jerry server at port: {}", port);
        NioServer server = BeanRegistry.getInstance(NioServer.class);
        server.start(port);
        log.info("Jerry server started, time used: {}ms", System.currentTimeMillis() - serverStartBegin);
        // 服务循环
        server.doService();
    }

    private static void doScan(String path) throws ClassNotFoundException {
        ClassLoader classLoader = JerryApplication.class.getClassLoader();

        URL resource = classLoader.getResource(path);
        File file;
        if(resource != null && (file = new File(resource.getFile())).exists()){
            File[] files;
            if(file.isDirectory() && (files = file.listFiles()) != null){
                String rootPath = path + "/";
                for(File ls : files){
                    doScan(rootPath + ls.getName());
                }
            }
            else if(file.getName().endsWith(".class")){
                String fullPath = path.replace("/", ".") + "." + file.getName();
                String className = fullPath.substring(0, fullPath.indexOf(".class"));
                Class<?> clazz = classLoader.loadClass(className);
                if(!clazz.isAnnotation() && !clazz.isInterface() && AnnotationUtil.findAnnotation(clazz, IOC.class) != null){
                    BeanRegistry.register(clazz);
                }
            }
        }
    }

    private static void printBanner(){
        try(InputStream inputStream = JerryApplication.class.getClassLoader().getResourceAsStream("banner")){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }
            reader.close();
            inputStreamReader.close();
        }catch (IOException e){

        }
    }
}
