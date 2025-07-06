package com.AutoMybatis.Bean;

import com.AutoMybatis.Utils.PropertiesUtils;

import java.util.HashMap;
import java.util.Stack;

//读取配置参数
public class Constants {

    public static Boolean IGNORE_TABLE_PREFIX;
    //Query后缀
    public static String SUFFIX_BEAN_PARAM;
    //模糊搜索后缀
    public static String SUFFIX_BEAN_PARAM_FUZZY;
    //mapper后缀
    public static String SUFFIX_MAPPERS;
    //参数日期起止
    public static String SUFFIX_BEAN_PARAM_TIME_START;
    public static String SUFFIX_BEAN_PARAM_TIME_END;

    //需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FIELD;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;
    //日期序列号，反序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    public static String BEAN_DATE_PARSEJSON_EXPRESSION;
    public static String BEAN_DATE_PARSEJSON_CLASS;

    //相关路径
    public static String PATH_BASE;

    public static String PACKAGE_BASE;

    private static final String PATH_JAVA = "java";

    private static final String PATH_RESOURCE = "resources";

    public static String PACKAGE_POJO;

    public static String PACKAGE_PARAM;

    public static String PACKAGE_MAPPER;

    public static String PACKAGE_ENUM;

    public static String PACKAGE_SERVICE;

    public static String PACKAGE_VO;

    public static String PACKAGE_SERVICE_IMPL;

    public static String PACKAGE_EXCEPTION;

    public static String PACKAGE_CONTROLLER;

    public static String PATH_POJO;

    public static String PATH_PARAM;

    public static String PATH_MAPPER;

    public static String PATH_MAPPERS_XML;

    public static String PATH_ENUM;

    public static String PATH_SERVICE;

    public static String PATH_SERVICE_IMPL;

    public static String PATH_VO;

    public static String PATH_EXCEPTION;

    public static String PATH_CONTROLLER;

    public static HashMap<String, String> TYPE_MAP = new HashMap<>();

    static {
        IGNORE_TABLE_PREFIX = Boolean.
                valueOf(PropertiesUtils.getString("ignore.table.prefix"));

        SUFFIX_BEAN_PARAM = String.valueOf(PropertiesUtils.getString("suffix.bean.param"));

        PATH_BASE = String.valueOf(PropertiesUtils.getString("path.base"));

        PACKAGE_BASE = String.valueOf(PropertiesUtils.getString("package.base"));
        //PO
        PACKAGE_POJO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.pojo");
        //Param
        PACKAGE_PARAM = PACKAGE_BASE + "." + PropertiesUtils.getString("package.param");
        //Mapper
        PACKAGE_MAPPER=PACKAGE_BASE+"."+PropertiesUtils.getString("package.mappers");
        //Enums
        PACKAGE_ENUM=PACKAGE_BASE+"."+PropertiesUtils.getString("package.enums");
        //VO
        PACKAGE_VO=PACKAGE_BASE+"."+PropertiesUtils.getString("package.vo");
        //Service
        PACKAGE_SERVICE=PACKAGE_BASE+"."+PropertiesUtils.getString("package.service");
        //impl
        PACKAGE_SERVICE_IMPL=PACKAGE_BASE+"."+PropertiesUtils.getString("package.service.impl");
        //Exception
        PACKAGE_EXCEPTION=PACKAGE_BASE+"."+PropertiesUtils.getString("package.exception");
        //Controller
        PACKAGE_CONTROLLER=PACKAGE_BASE+"."+PropertiesUtils.getString("package.controller");

        SUFFIX_BEAN_PARAM_FUZZY = PropertiesUtils.getString("suffix.bean.param.fuzzy");
        SUFFIX_BEAN_PARAM_TIME_START = PropertiesUtils.getString("suffix.bean.param.time.start");
        SUFFIX_BEAN_PARAM_TIME_END = PropertiesUtils.getString("suffix.bean.param.time.end");
        SUFFIX_MAPPERS=PropertiesUtils.getString("suffix.mappers");

        PATH_BASE = (PATH_BASE + "/" + PATH_JAVA + "/" + PACKAGE_BASE).replace(".", "/");

        PATH_POJO = PATH_BASE + "/" + PropertiesUtils.getString("package.pojo").replace(".", "/");

        PATH_PARAM = PATH_BASE + "/" + PropertiesUtils.getString("package.param").replace(".", "/");

        PATH_MAPPER=PATH_BASE + "/" + PropertiesUtils.getString("package.mappers").replace(".", "/");

        PATH_MAPPERS_XML=PropertiesUtils.getString("path.base") +PATH_RESOURCE+ "/"+
                PACKAGE_MAPPER.replace(".","/");

        PATH_ENUM=PATH_BASE + "/" + PropertiesUtils.getString("package.enums").replace(".", "/");;

        PATH_SERVICE=PATH_BASE + "/" + PropertiesUtils.getString("package.service").replace(".", "/");
        PATH_SERVICE_IMPL=PATH_BASE + "/" + PropertiesUtils.getString("package.service.impl").replace(".", "/");

        PATH_VO=PATH_BASE+"/"+PropertiesUtils.getString("package.vo").replace(".", "/");

        PATH_EXCEPTION=PATH_BASE+"/"+PropertiesUtils.getString("package.exception").replace(".", "/");

        PATH_CONTROLLER=PATH_BASE+"/"+PropertiesUtils.getString("package.controller").replace(".", "/");
        //忽略字段
        IGNORE_BEAN_TOJSON_FIELD = PropertiesUtils.getString("ignore.bean.tojson.field");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");
        //日期序列化
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        //日期反序列化
        BEAN_DATE_PARSEJSON_EXPRESSION = PropertiesUtils.getString("bean.date.parsejson.expression");
        BEAN_DATE_PARSEJSON_CLASS = PropertiesUtils.getString("bean.date.parsejson.class");

        // 数值类型
        TYPE_MAP.put("TINYINT", "Integer");
        TYPE_MAP.put("SMALLINT", "Integer");
        TYPE_MAP.put("MEDIUMINT", "Integer");
        TYPE_MAP.put("INT", "Integer");
        TYPE_MAP.put("INTEGER", "Integer");
        TYPE_MAP.put("BIGINT", "Long");
        TYPE_MAP.put("FLOAT", "BigDecimal");
        TYPE_MAP.put("DOUBLE", "BigDecimal");
        TYPE_MAP.put("DECIMAL", "BigDecimal");
        TYPE_MAP.put("NUMERIC", "BigDecimal");

        // 字符串类型
        TYPE_MAP.put("CHAR", "String");
        TYPE_MAP.put("VARCHAR", "String");
        TYPE_MAP.put("TINYTEXT", "String");
        TYPE_MAP.put("TEXT", "String");
        TYPE_MAP.put("MEDIUMTEXT", "String");
        TYPE_MAP.put("LONGTEXT", "String");
        TYPE_MAP.put("ENUM", "String");
        TYPE_MAP.put("SET", "String");

        // 日期时间类型 采用Java8日期时间API
        TYPE_MAP.put("DATE", "LocalDate");      // 仅日期（年月日）
        TYPE_MAP.put("TIME", "LocalTime");      // 仅时间（时分秒）
        TYPE_MAP.put("DATETIME", "LocalDateTime");  // 日期 + 时间（年月日时分秒）
        TYPE_MAP.put("TIMESTAMP", "Instant");   // 时间戳（UTC 时间，适合数据库 TIMESTAMP）
        TYPE_MAP.put("YEAR", "Integer");                  // 年份（如 2024）

        // 二进制类型
        TYPE_MAP.put("BIT", "Boolean");
        TYPE_MAP.put("BINARY", "byte[]");
        TYPE_MAP.put("VARBINARY", "byte[]");
        TYPE_MAP.put("TINYBLOB", "byte[]");
        TYPE_MAP.put("BLOB", "byte[]");
        TYPE_MAP.put("MEDIUMBLOB", "byte[]");
        TYPE_MAP.put("LONGBLOB", "byte[]");

        // 其他类型
        TYPE_MAP.put("JSON", "String");
        TYPE_MAP.put("GEOMETRY", "Object");
    }


}
