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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class BuildPo {

    private static final Logger logger = LoggerFactory.getLogger(BuildPo.class);

    private static Set<String> ignoreSet=new HashSet<>();

    public static void execute(TableInfo tableInfo) {
        //创建pojo包
        File folder = new File(Constants.PATH_POJO);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File proFile = new File(folder, tableInfo.getBeanName() + ".java");

        OutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter br = null;

        //构建忽略字段
        ignoreSet.addAll(Arrays.asList(Constants.IGNORE_BEAN_TOJSON_FIELD.split(",")));
        logger.info(ignoreSet.toString());

        try {
            out = new FileOutputStream(proFile);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            br = new BufferedWriter(writer);
            br.write("package " + Constants.PACKAGE_POJO + ";");
            br.newLine();
            br.write("import java.io.Serializable;");
            br.newLine();
            //导包
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                br.write("import java.time.*;");
                br.write("import java.time.format.DateTimeFormatter;");
                br.newLine();
                //序列化包
                br.write(Constants.BEAN_DATE_FORMAT_CLASS);
                br.newLine();
                br.write(Constants.BEAN_DATE_PARSEJSON_CLASS);
                br.newLine();
            }
            if (tableInfo.getHavaBigDecimal()) {
                br.write("import java.math.BigDecimal;");
                br.newLine();
            }
            //忽略包
            if(!ignoreSet.isEmpty()){
                br.write(Constants.IGNORE_BEAN_TOJSON_CLASS);
                br.newLine();
            }

            //类注释
            BuildComment.generateClassComment(br, tableInfo.getComment());
            br.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            br.newLine();

            //属性构建
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                //注释
                BuildComment.generateFieldComment(br, fieldInfo.getComment());
                //是否是日期时间，需要添加序列化和反序列化处理
                if (fieldInfo.getJavaType().equals("LocalDate")) {
                    br.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateTimeUtils.DATE_EXP));
                    br.newLine();
                    br.write("\t"+String.format(Constants.BEAN_DATE_PARSEJSON_EXPRESSION, DateTimeUtils.DATE_EXP));
                    br.newLine();
                }
                if (fieldInfo.getJavaType().equals("LocalTime")) {
                    br.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateTimeUtils.TIME_EXP));
                    br.newLine();
                    br.write("\t"+String.format(Constants.BEAN_DATE_PARSEJSON_EXPRESSION, DateTimeUtils.TIME_EXP));
                    br.newLine();
                }
                if (fieldInfo.getJavaType().equals("LocalDateTime")) {
                    br.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateTimeUtils.DATE_TIME_EXP));
                    br.newLine();
                    br.write("\t"+String.format(Constants.BEAN_DATE_PARSEJSON_EXPRESSION, DateTimeUtils.DATE_TIME_EXP));
                    br.newLine();
                }
                if (fieldInfo.getJavaType().equals("Instant")) {
                    br.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateTimeUtils.DATE_TIME_EXP));
                    br.newLine();
                    br.write("\t"+String.format(Constants.BEAN_DATE_PARSEJSON_EXPRESSION, DateTimeUtils.DATE_TIME_EXP));
                    br.newLine();
                }
                //是否忽略
                if(ignoreSet.contains(fieldInfo.getPropertyName())){
                    br.write("\t"+Constants.IGNORE_BEAN_TOJSON_EXPRESSION);
                    br.newLine();
                }
                //属性
                br.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                br.newLine();
                br.newLine();
            }

            //属性 setter and getter
            for(FieldInfo fieldInfo : tableInfo.getFieldInfoList()){
                String tempField= StringUtils.capitalize(fieldInfo.getPropertyName());
                br.write("\tpublic void set"+tempField+"("+fieldInfo.getJavaType()+" "
                +fieldInfo.getPropertyName()+") {");
                br.newLine();
                br.write("\t\tthis."+fieldInfo.getPropertyName()+" = "+fieldInfo.getPropertyName()+";");
                br.newLine();
                br.write("\t}");
                br.newLine();
                br.newLine();


                br.write("\tpublic "+fieldInfo.getJavaType()+" get"+tempField+"() {");
                br.newLine();
                br.write("\t\treturn this."+fieldInfo.getPropertyName()+";");
                br.newLine();
                br.write("\t}");
                br.newLine();
                br.newLine();
            }

            //pojo toString()重写
            br.write("\t@Override");
            br.newLine();
            br.write("\tpublic String toString() {");
            br.newLine();
            br.write("\t\treturn \"" + tableInfo.getComment() + "{\" +");  // 类名开头

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tableInfo.getFieldInfoList().size(); i++) {
                FieldInfo fieldInfo = tableInfo.getFieldInfoList().get(i);
                String fieldName = fieldInfo.getPropertyName();
                String javaType = fieldInfo.getJavaType();

                // 处理时间类型的特殊格式化
                String fieldValue = switch (javaType) {
                    case "LocalDateTime" ->
                            "(" + fieldName + " != null ? " + fieldName + ".format(DateTimeFormatter.ofPattern(\"" + DateTimeUtils.DATE_TIME_EXP + "\")) : \"空\")";
                    case "LocalDate" ->
                            "(" + fieldName + " != null ? " + fieldName + ".format(DateTimeFormatter.ofPattern(\"" + DateTimeUtils.DATE_EXP + "\")) : \"空\")";
                    case "LocalTime" ->
                            "(" + fieldName + " != null ? " + fieldName + ".format(DateTimeFormatter.ofPattern(\"" + DateTimeUtils.TIME_EXP + "\")) : \"空\")";
                    case "Instant" ->
                            "(" + fieldName + " != null ? " + fieldName + ".atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(\"" + DateTimeUtils.DATE_TIME_EXP + "\")) : \"空\")";
                    default ->
                            "(" + fieldName + " != null ? String.valueOf(" + fieldName + ") : \"空\")";
                };

                // 处理每个字段的拼接
                if (i == 0) {
                    sb.append("\n\t\t\t\"").append(fieldInfo.getComment()).append("=\" + ").append(fieldValue).append(" + '\\''");
                } else {
                    sb.append(" +\n\t\t\t\", ").append(fieldInfo.getComment()).append("=\" + ").append(fieldValue).append(" + '\\''");
                }
            }

            br.write(sb.toString());
            br.write(" + '}';");  // 闭合大括号
            br.newLine();
            br.write("\t}");
            br.newLine();

            br.write("}");
            br.flush();

        } catch (Exception e) {
            logger.error("创建POJO失败", e);
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
