package com.AutoMybatis.Utils;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;


public class DateTimeUtils {
    //类型
    public static List<String> timeTypeList = Arrays.asList("LocalDate", "LocalTime", "LocalDateTime", "Instant");

    public static List<String> SQLTimeTypeList=Arrays.asList("date","datetime","time","timestamp","year");
    //日期
    public static String DATE_EXP= "YYYY年MM月dd日";

    //时间
    public static String TIME_EXP="HH时mm分";

    //日期时间
    public static String DATE_TIME_EXP="yyyy年MM月dd日 HH时mm分";

    //获取现在的时间戳
    public static String getTimeStamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分");
        return LocalDateTime.now().format(formatter);
    }
    //是否是日期时间类型
    public static boolean isTimeType(String javaType){
        return timeTypeList.contains(javaType);
    }
    //根据sqlType判断是否是日期时间类型
    public static boolean isTimeTypeBySQLType(String sqlType){
        return SQLTimeTypeList.contains(sqlType);
    }

    @Test
    public void test(){
    }
}
