package com.AutoMybatis.Builder;

import com.AutoMybatis.Bean.Constants;
import com.AutoMybatis.Bean.FieldInfo;
import com.AutoMybatis.Bean.TableInfo;
import com.AutoMybatis.Utils.PropertiesUtils;
import org.junit.Test;
import org.slf4j.*;

import java.sql.*;
import java.util.*;

public class BuildTable {
    //slf4j日志
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);

    private static Connection connection = null;
    //表信息
    private static final String SQL_SHOW_TABLE_STATUS = "show table status";
    //字段信息
    private static final String SQL_SHOW_TABLE_Fields = "show full fields from %s";
    //索引信息
    private static final String SQL_SHOW_TABLE_INDEX = "show index from %s";


    static {
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String username = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            //JDBC方式连接数据库
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("连接失败:" + e);
        }

    }

    public static List<TableInfo>  getTables() {
        PreparedStatement ps = null;
        ResultSet tableResult = null;

        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            ps = connection.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            while (tableResult.next()) {
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");

                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    //如果忽略前缀，就去掉前面的tb_
                    beanName = tableName.substring(beanName.indexOf('_') + 1);
                }
                beanName = modifyField(beanName, true);
                //表基本信息
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARAM);
                //表字段信息
                List<FieldInfo> fieldInfoList = readFieldInfo(tableInfo);
                tableInfo.setFieldInfoList(fieldInfoList);

                if (tableInfo.getHavaBigDecimal() == null) {
                    tableInfo.setHavaBigDecimal(false);
                }
                if (tableInfo.getHaveDate() == null) {
                    tableInfo.setHaveDate(false);
                }
                if (tableInfo.getHaveDateTime() == null) {
                    tableInfo.setHaveDateTime(false);
                }
                //表索引信息
                setKeyIndexInfo(tableInfo);
                logger.info(String.valueOf(tableInfo));
                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            logger.error("读取失败" + e);
        } finally {
            //释放资源
            try {
                if (tableResult != null) {
                    tableResult.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tableInfoList;
    }

    //读取索引信息
    private static void setKeyIndexInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet keyIndexResult = null;

        try {
            Map<String,FieldInfo> tempMap=new HashMap<>();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()){
                tempMap.put(fieldInfo.getFieldName(),fieldInfo);
            }
            ps = connection.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableInfo.getTableName()));
            keyIndexResult = ps.executeQuery();
            while (keyIndexResult.next()) {
                //索引名字
                String keyName = keyIndexResult.getString("key_name");
                //索引类型
                Integer nonUnique = keyIndexResult.getInt("non_unique");
                //列名
                String columnName = keyIndexResult.getString("column_name");
                if (nonUnique == 1) {
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().getOrDefault(keyName, new ArrayList<>());
                tableInfo.getKeyIndexMap().put(keyName, keyFieldList);

                keyFieldList.add(tempMap.get(columnName));
            }
        } catch (Exception e) {
            logger.error("读取索引失败" + e);
        } finally {
            //释放资源
            try {
                if (keyIndexResult != null) {
                    keyIndexResult.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //读取字段信息
    private static List<FieldInfo> readFieldInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;

        List<FieldInfo> fieldInfoList = new ArrayList<>();
        try {
            ps = connection.prepareStatement(String.format(SQL_SHOW_TABLE_Fields, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String field = fieldResult.getString("field");
                String type = fieldResult.getString("type");
                String extra = fieldResult.getString("extra");
                String comment = fieldResult.getString("comment");
                //修改
                if (type.contains("(")) {
                    type = type.substring(0, type.indexOf("("));
                }
                String propertyName = modifyField(field, false);
                //构建FieldInfo
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(sqlTypeMapTOJavaType(type));

                if (type.equals("date")) {
                    tableInfo.setHaveDate(true);
                }
                if (type.equals("datetime")) {
                    tableInfo.setHaveDateTime(true);
                }
                if (fieldInfo.getJavaType().equals("BigDecimal")) {
                    tableInfo.setHavaBigDecimal(true);
                }
                fieldInfoList.add(fieldInfo);

            }
        } catch (Exception e) {
            logger.error("读取失败" + e);
        } finally {
            //释放资源
            try {
                if (fieldResult != null) {
                    fieldResult.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fieldInfoList;
    }


    //驼峰映射
    private static String modifyField(String input, Boolean firstLetterUpperCase) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }

        // 处理首字母大小写
        if (firstLetterUpperCase && !result.isEmpty()) {
            result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
        } else if (!firstLetterUpperCase && !result.isEmpty()) {
            result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        }

        return result.toString();
    }

    //sqlType到javaType的映射
    private static String sqlTypeMapTOJavaType(String sqlType) {
        String typeKey = sqlType.toUpperCase();
        // 处理无符号整数类型
        if (typeKey.endsWith(" UNSIGNED")) {
            typeKey = typeKey.replace(" UNSIGNED", "");
            switch (typeKey) {
                case "TINYINT" -> {
                    return "Short";
                }
                case "SMALLINT" -> {
                    return "Integer";
                }
                case "MEDIUMINT", "INT", "INTEGER" -> {
                    return "Long";
                }
                case "BIGINT" -> {
                    return "java.math.BigInteger";
                }
            }
        }

        return Constants.TYPE_MAP.getOrDefault(typeKey, "Object");
    }


    @Test
    public void test() {
        System.out.println(modifyField("user_info", false));
    }
}
