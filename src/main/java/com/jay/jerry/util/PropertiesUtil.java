package com.jay.jerry.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
public class PropertiesUtil {
    private static Properties properties;

    static {
        try(InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("server.properties")){
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key){
        return properties.getProperty(key);
    }

    public static Object getObject(String key){
        return properties.get(key);
    }

    public static int getInt(String key){
        return Integer.parseInt(properties.getProperty(key));
    }
}
