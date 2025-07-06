package com.AutoMybatis.Utils;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

//读取数据库配置
public class PropertiesUtils {

    private static Properties properties=new Properties();

    private static Map<String,String> properMap= new ConcurrentHashMap<>();

    static {
        InputStream inputStream=null;

        try {
            inputStream=PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(inputStream);

            properties.keySet().forEach((e)->{
                properMap.put((String) e, (String) properties.get(e));
            });

        }catch (Exception e){

        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getString(String key){
        return properMap.get(key);
    }

    @Test
    public void test(){
        System.out.println(getString("db.driver.name"));
    }
}
