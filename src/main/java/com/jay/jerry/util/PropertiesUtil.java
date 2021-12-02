package com.jay.jerry.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>
 *  配置文件工具
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
public class PropertiesUtil {
    private static Properties properties;

    /*
        加载默认配置文件
     */
    static {
        try(InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("server.properties")){
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get
     * @param key key
     * @return value String
     */
    public static String get(String key){
        return properties.getProperty(key);
    }

    /**
     * get
     * @param key key
     * @return value int
     */
    public static int getInt(String key){
        return Integer.parseInt(properties.getProperty(key));
    }
}
