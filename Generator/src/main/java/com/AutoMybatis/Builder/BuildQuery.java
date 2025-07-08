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

public class BuildQuery {

    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);


    public static void execute(TableInfo tableInfo) {
        //创建pojo包
        File folder = new File(Constants.PATH_PARAM);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName()+Constants.SUFFIX_BEAN_PARAM;

        File proFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter br = null;


        try {
            out = new FileOutputStream(proFile);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            br = new BufferedWriter(writer);
            br.write("package " + Constants.PACKAGE_PARAM + ";");
            br.newLine();
            //导包
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                br.write("import java.time.*;");
                br.newLine();
                br.write("import java.time.format.DateTimeFormatter;");
                br.newLine();
            }
            if (tableInfo.getHavaBigDecimal()) {
                br.write("import java.math.BigDecimal;");
                br.newLine();
            }

            //类注释
            BuildComment.generateClassComment(br, tableInfo.getComment()+"查询对象");
            br.write("public class " + className + " extends BaseQuery {");
            br.newLine();
            //扩展字段信息
            List<FieldInfo> extensionFields=new ArrayList<>();

            //属性构建
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                //注释
                BuildComment.generateFieldComment(br, fieldInfo.getComment());

                //属性
                br.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                br.newLine();
                br.newLine();
                //模糊查询
                if(fieldInfo.getJavaType().equals("String")){
                    extensionFields.add(new FieldInfo(fieldInfo.getPropertyName()+Constants.SUFFIX_BEAN_PARAM_FUZZY, fieldInfo.getJavaType(),fieldInfo.getSqlType(), fieldInfo.getFieldName()));
                    br.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName()+
                            Constants.SUFFIX_BEAN_PARAM_FUZZY+ ";");
                    br.newLine();
                    br.newLine();
                }
                //日期时间类型
                if(DateTimeUtils.isTimeType(fieldInfo.getJavaType())){
                    extensionFields.add(new FieldInfo(fieldInfo.getPropertyName()+Constants.SUFFIX_BEAN_PARAM_TIME_START, "String",fieldInfo.getSqlType(),fieldInfo.getFieldName()));
                    br.write("\tprivate String "  + fieldInfo.getPropertyName()+
                            Constants.SUFFIX_BEAN_PARAM_TIME_START+ ";");
                    br.newLine();
                    br.newLine();
                    extensionFields.add(new FieldInfo(fieldInfo.getPropertyName()+Constants.SUFFIX_BEAN_PARAM_TIME_END, "String",fieldInfo.getSqlType(), fieldInfo.getFieldName()));
                    br.write("\tprivate String "  + fieldInfo.getPropertyName()+
                            Constants.SUFFIX_BEAN_PARAM_TIME_END+ ";");
                    br.newLine();
                    br.newLine();
                }
            }
            tableInfo.setExtensionFields(extensionFields);
            ArrayList<FieldInfo> newList = new ArrayList<>(extensionFields);
            newList.addAll(tableInfo.getFieldInfoList());
            //属性 setter and getter
            for(FieldInfo fieldInfo : newList){
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


            br.write("}");
            br.flush();

        } catch (Exception e) {
            logger.error("创建Query失败", e);
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
