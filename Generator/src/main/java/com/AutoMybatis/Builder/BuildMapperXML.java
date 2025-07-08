package com.AutoMybatis.Builder;

import com.AutoMybatis.Bean.Constants;
import com.AutoMybatis.Bean.FieldInfo;
import com.AutoMybatis.Bean.TableInfo;
import com.AutoMybatis.Utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

//mapperXML构建
public class BuildMapperXML {
    private static Logger logger = LoggerFactory.getLogger(BuildMapperXML.class);

    private static final String base_column_list = "base_column_list";

    private static final String base_query_condition = "base_query_condition";

    private static final String base_query_condition_extend = "base_query_condition_extend";

    private static final String query_condition = "query_condition";

    public static void execute(TableInfo tableInfo) {

        File folder = new File(Constants.PATH_MAPPERS_XML);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;

        File proFile = new File(folder, className + ".xml");

        OutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter br = null;

        try {
            out = new FileOutputStream(proFile);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            br = new BufferedWriter(writer);

            br.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            br.newLine();
            br.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            br.newLine();
            br.write(String.format("<mapper namespace=\"%s\">\n", Constants.PACKAGE_MAPPER + "." + className));
            br.newLine();

            br.write("\t<!--属性映射-->");
            br.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            //主键字段
            List<FieldInfo> primaryKeyFields = keyIndexMap.getOrDefault("PRIMARY", Collections.emptyList());

            String poClass = Constants.PACKAGE_POJO + "." + tableInfo.getBeanName();
            br.write(String.format("\t<resultMap id=\"base_result_map\" type=\"%s\">", poClass));
            br.newLine();

            //处理主键字段（标记为 id）
            for (FieldInfo field : primaryKeyFields) {
                br.write("\t\t<!--" + field.getComment() + "-->");
                br.newLine();
                br.write("\t\t<id column=\"" + field.getFieldName() + "\" property=\"" + field.getPropertyName() + "\" />");
                br.newLine();
            }
            //处理普通字段（标记为 result）
            for (FieldInfo field : tableInfo.getFieldInfoList()) {
                // 跳过已作为主键的字段（避免重复）
                if (!primaryKeyFields.contains(field)) {
                    br.write("\t\t<!--" + field.getComment() + "-->");
                    br.newLine();
                    br.write("\t\t<result column=\"" + field.getFieldName() + "\" property=\"" + field.getPropertyName() + "\" />");
                    br.newLine();
                }
            }
            br.write("\t</resultMap>");
            br.newLine();
            br.newLine();

            //通用查询列
            br.write("\t<!--通用查询列-->");
            br.newLine();

            br.write("\t<sql id=\"" + base_column_list + "\">");
            br.newLine();

            StringBuilder sb = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                sb.append(fieldInfo.getFieldName()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            br.write("\t\t" + sb);
            br.newLine();
            br.write("\t</sql>");
            br.newLine();
            br.newLine();
            //基础条件查询
            br.write("\t<!--基础条件查询-->");
            br.newLine();

            br.write("\t<sql id=\"" + base_query_condition + "\">");
            br.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                String extensionCase = "";
                if (fieldInfo.getJavaType().equals("String")) {
                    extensionCase = String.format(" and query.%s != ''", fieldInfo.getPropertyName());
                }

                br.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + "!= null" + extensionCase + "\">");
                br.newLine();
                br.write(String.format("\t\t\tand %s = #{query.%s}",fieldInfo.getFieldName(), fieldInfo.getPropertyName()));
                br.newLine();
                br.write("\t\t</if>");
                br.newLine();
            }
            br.write("\t</sql>");
            br.newLine();
            br.newLine();

            //扩展条件查询(模糊，日期起止)
            br.write("\t<!--扩展条件查询-->");
            br.newLine();

            br.write("\t<sql id=\"" + base_query_condition_extend + "\">");
            br.newLine();
            for (FieldInfo fieldInfo : tableInfo.getExtensionFields()) {
                String extensionCase = "";
                if (DateTimeUtils.isTimeTypeBySQLType(fieldInfo.getSqlType())) {
                    //区分起止时间
                    if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_PARAM_TIME_START)) {
                        extensionCase = "\t\tAND " + fieldInfo.getFieldName() + " &gt;= #{query." + fieldInfo.getPropertyName() + "}";
                    } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_PARAM_TIME_END)) {
                        extensionCase = "\t\tAND " + fieldInfo.getFieldName() + " &lt;= #{query." + fieldInfo.getPropertyName() + "}";
                    }
                } else if (fieldInfo.getJavaType().equals("String")) {
                    extensionCase = "\t\t\tAND " + fieldInfo.getFieldName() + " LIKE CONCAT('%', #{query." + fieldInfo.getPropertyName() + "}, '%')";
                }

                br.write(String.format("\t\t<if test=\"query.%s != null and " +
                        "query.%s != ''\">", fieldInfo.getPropertyName(), fieldInfo.getPropertyName()));
                br.newLine();
                br.write("\t\t\t" + extensionCase);
                br.newLine();
                br.write("\t\t</if>");
                br.newLine();
            }
            br.write("\t</sql>");
            br.newLine();
            br.newLine();

            //组合条件
            br.write("\t<!--组合条件-->");
            br.newLine();

            br.write("\t<sql id=\"" + query_condition + "\">");
            br.newLine();
            br.write("\t\t<where>");
            br.newLine();
            br.write("\t\t\t<include refid=\"" + base_query_condition + "\"/>");
            br.newLine();
            br.write("\t\t\t<include refid=\"" + base_query_condition_extend + "\"/>");
            br.newLine();
            br.write("\t\t</where>");
            br.newLine();
            br.write("\t</sql>");
            br.newLine();
            br.newLine();

            //查询列表
            br.write("\t<!--查询列表-->");
            br.newLine();

            br.write(String.format("\t<select id=\"%s\" resultMap=\"%s\">",
                    "selectList", "base_result_map"));
            br.newLine();
            br.write(String.format("\t\tSELECT <include refid=\"%s\"/> " +
                    "FROM %s <include refid=\"%s\"/>", base_column_list, tableInfo.getTableName(), query_condition));
            br.newLine();
            br.write("\t\t<if test=\"query.orderBy != null\">order by ${query.orderBy}</if>");
            br.newLine();
            br.write("\t\t<if test=\"query.pageHelper != null\">limit #{query.pageHelper.start},#{query.pageHelper.end}</if>");
            br.newLine();
            br.write("\t</select>");
            br.newLine();

            //查询数量
            br.write("\t<!--查询数量-->");
            br.newLine();

            br.write(String.format("\t<select id=\"%s\" resultType=\"%s\">",
                    "selectCount", "java.lang.Long"));
            br.newLine();
            br.write(String.format("\t\tSELECT count(1) " +
                    "FROM %s <include refid=\"%s\"/>", tableInfo.getTableName(), query_condition));
            br.newLine();
            br.write("\t</select>");
            br.newLine();

            //单条插入
            br.write("\t<!--单条插入(匹配有值的字段)-->");
            br.newLine();
            br.write(String.format("\t<insert id=\"%s\" parameterType=\"%s\">",
                    "insert", poClass));
            br.newLine();
            //拿到自增id字段
            FieldInfo autoIncrField = null;
            ArrayList<FieldInfo> nonAutoFields = new ArrayList<>();//非自增字段
            ArrayList<FieldInfo> unPrimaryFields = new ArrayList<>();//非索引字段
            //索引字段（不允许修改）
            HashSet<String> keyFields = new HashSet<>();
            tableInfo.getKeyIndexMap().values().forEach((list) -> {
                list.forEach((fieldInfo -> {
                    keyFields.add(fieldInfo.getFieldName());
                }));
            });
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                if (fieldInfo.getAutoIncrement() != null && !fieldInfo.getAutoIncrement()) {
                    nonAutoFields.add(fieldInfo);
                } else if (fieldInfo.getAutoIncrement() != null) {
                    autoIncrField = fieldInfo;
                } else {
                    nonAutoFields.add(fieldInfo);
                }
                if (!keyFields.contains(fieldInfo.getFieldName())) {
                    unPrimaryFields.add(fieldInfo);
                }
            }
            //获取下一个自增值
            if (autoIncrField != null) {
                br.write(String.format("\t\t<selectKey keyProperty=\"bean.%s\" resultType=\"%s\" order=\"%s\">",
                        autoIncrField.getFieldName(), autoIncrField.getJavaType(), "AFTER"));
                br.newLine();
                br.write("\t\t\tSELECT LAST_INSERT_ID()");
                br.newLine();
                br.write("\t\t</selectKey>");
                br.newLine();
            }
            br.write("\t\t\tINSERT INTO " + tableInfo.getTableName() + " ");
            br.newLine();
            //列名
            br.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            br.newLine();
            for (FieldInfo fieldInfo : nonAutoFields) {
                br.write(String.format("\t\t\t<if test=\"bean.%s != null\">", fieldInfo.getPropertyName()));
                br.newLine();
                br.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                br.newLine();
                br.write("\t\t\t</if>");
                br.newLine();
            }
            br.write("\t\t</trim>");
            br.newLine();
            //值
            br.write("\t\t<trim prefix=\"values(\" suffix=\")\" suffixOverrides=\",\">");
            br.newLine();
            for (FieldInfo fieldInfo : nonAutoFields) {
                br.write(String.format("\t\t\t<if test=\"bean.%s != null\">", fieldInfo.getPropertyName()));
                br.newLine();
                br.write(String.format("\t\t\t\t#{bean.%s},", fieldInfo.getPropertyName()));
                br.newLine();
                br.write("\t\t\t</if>");
                br.newLine();
            }
            br.write("\t\t</trim>");
            br.newLine();

            br.write("\t</insert>");
            br.newLine();

            //插入或更新
            br.write("\t<!--插入或更新-->");
            br.newLine();
            br.write(String.format("\t<insert id=\"%s\" parameterType=\"%s\">",
                    "insertOrUpdate", poClass));
            br.newLine();

            br.write("\t\t\tINSERT INTO " + tableInfo.getTableName() + " ");
            br.newLine();
            //列名
            br.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            br.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                br.write(String.format("\t\t\t<if test=\"bean.%s != null\">", fieldInfo.getPropertyName()));
                br.newLine();
                br.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                br.newLine();
                br.write("\t\t\t</if>");
                br.newLine();
            }
            br.write("\t\t</trim>");
            br.newLine();
            //值
            br.write("\t\t<trim prefix=\"values(\" suffix=\")\" suffixOverrides=\",\">");
            br.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                br.write(String.format("\t\t\t<if test=\"bean.%s != null\">", fieldInfo.getPropertyName()));
                br.newLine();
                br.write(String.format("\t\t\t\t#{bean.%s},", fieldInfo.getPropertyName()));
                br.newLine();
                br.write("\t\t\t</if>");
                br.newLine();
            }
            br.write("\t\t</trim>");
            br.newLine();

            br.write("\t\t\ton DUPLICATED key update");
            br.newLine();

            br.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            br.newLine();
            for (FieldInfo fieldInfo : unPrimaryFields) {
                br.write(String.format("\t\t\t<if test=\"bean.%s != null\">", fieldInfo.getPropertyName()));
                br.newLine();
                br.write(String.format("\t\t\t\t%s = VALUES(%s),", fieldInfo.getFieldName(), fieldInfo.getFieldName()));
                br.newLine();
                br.write("\t\t\t</if>");
                br.newLine();
            }

            br.write("\t\t</trim>");
            br.newLine();

            br.write("\t</insert>");
            br.newLine();
            br.newLine();

            //批量插入
            br.write("\t<!--批量插入-->");
            br.newLine();
            br.write("");
            br.write(String.format("\t<insert id=\"%s\" parameterType=\"%s\" >",
                    "insertBatch", poClass));
            br.newLine();
            StringBuilder fieldsBuilder = new StringBuilder();
            StringBuilder paramsBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : nonAutoFields) {
                fieldsBuilder.append(fieldInfo.getFieldName()).append(",");
                paramsBuilder.append(String.format("#{item.%s},", fieldInfo.getPropertyName()));
            }
            fieldsBuilder.deleteCharAt(fieldsBuilder.length() - 1);
            br.write(String.format("\t\tINSERT INTO %s (%s) values ", tableInfo.getTableName(), fieldsBuilder));
            br.newLine();
            br.write(String.format("\t\t<foreach collection=\"%s\" item=\"%s\" separator=\"%s\">",
                    "list", "item", ","));
            br.newLine();
            paramsBuilder.deleteCharAt(paramsBuilder.length() - 1);
            br.write("\t\t\t(" + paramsBuilder+")");
            br.newLine();
            br.write("\t\t</foreach>");
            br.newLine();
            br.write("\t\t</insert>");
            br.newLine();
            br.newLine();

            //批量插入或更新
            br.write("\t<!--批量插入或更新-->");
            br.newLine();
            br.write("");
            br.write(String.format("\t<insert id=\"%s\" parameterType=\"%s\" >",
                    "insertOrUpdateBatch", poClass));
            br.newLine();
            br.write(String.format("\t\tINSERT INTO %s (%s) values ", tableInfo.getTableName(), fieldsBuilder));
            br.newLine();
            br.write(String.format("\t\t<foreach collection=\"%s\" item=\"%s\" separator=\"%s\" >",
                    "list", "item", ","));
            br.newLine();
            br.write("\t\t\t(" + paramsBuilder+")");
            br.newLine();
            br.write("\t\t</foreach>");
            br.newLine();
            br.write("\t\t\ton DUPLICATE key update");
            br.newLine();
            sb = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                sb.append(String.format("\t\t\t\t%s = VALUES (%s),\n", fieldInfo.getFieldName(), fieldInfo.getFieldName()));
            }
            sb.deleteCharAt(sb.length() - 2);
            br.write(sb.toString());
            br.write("\t\t</insert>");
            br.newLine();
            br.newLine();

            //根据主键增删改查

            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                //方法名
                StringBuilder methodName = new StringBuilder();
                //参数名
                StringBuilder paramsName= new StringBuilder();
                List<FieldInfo> keyFieldInfoList = entry.getValue();
                int l = 1;
                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    methodName.append(StringUtils.capitalize(fieldInfo.getPropertyName()));
                    paramsName.append(String.format("%s =#{%s}",fieldInfo.getFieldName(),fieldInfo.getPropertyName()));
                    if (l++ < keyFieldInfoList.size()) {
                        methodName.append("And");
                        paramsName.append(" and ");
                    }
                }

                //查询
                br.write("\t<!--根据"+ methodName +"查询-->");
                br.newLine();
                br.write(String.format("\t<select id=\"selectBy%s\" resultMap=\"%s\">",methodName,"base_result_map"));
                br.newLine();

                br.write(String.format("\t\tselect <include refid=\"%s\"/> from %s where %s",base_column_list,tableInfo.getTableName(),paramsName));
                br.newLine();
                br.write("\t</select>");
                br.newLine();

                //更新
                br.write("\t<!--根据"+ methodName +"更新-->");
                br.newLine();
                br.write(String.format("\t<update id=\"updateBy%s\" parameterType=\"%s\">",methodName,poClass));
                br.newLine();
                br.write(String.format("\t\tupdate %s ",tableInfo.getTableName()));
                br.newLine();
                br.write("\t\t\t<set>");
                br.newLine();
                for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                    br.write(String.format("\t\t\t<if test=\"bean.%s != null\">", fieldInfo.getPropertyName()));
                    br.newLine();
                    br.write(String.format("\t\t\t\t%s = #{bean.%s},", fieldInfo.getFieldName(), fieldInfo.getPropertyName()));
                    br.newLine();
                    br.write("\t\t\t</if>");
                    br.newLine();
                }
                br.write("\t\t\t</set>");
                br.newLine();
                br.write(" \t\t\twhere "+paramsName);
                br.newLine();
                br.write("\t</update>");
                br.newLine();

                //删除
                br.write("\t<!--根据"+ methodName +"删除-->");
                br.newLine();
                br.write(String.format("\t<delete id=\"deleteBy%s\">",methodName));
                br.newLine();
                br.write(String.format("\t\tdelete from %s where %s",tableInfo.getTableName(),paramsName));
                br.newLine();
                br.write("\t</delete>");
                br.newLine();

            }

            br.write("</mapper>");
            br.flush();

        } catch (Exception e) {
            logger.error("创建Mapper XML失败", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
