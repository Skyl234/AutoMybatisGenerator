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
import java.util.List;
import java.util.Map;

//mapper构建
public class BuildMapper {
    private static Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    public static void execute(TableInfo tableInfo){

        File folder = new File(Constants.PATH_MAPPER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className=tableInfo.getBeanName()+Constants.SUFFIX_MAPPERS;
        File proFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter br = null;

        try {
            out = new FileOutputStream(proFile);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            br = new BufferedWriter(writer);
            br.write("package " + Constants.PACKAGE_MAPPER + ";");
            br.newLine();
            br.write("import org.apache.ibatis.annotations.Param;");
            br.newLine();
            br.newLine();

            //类注释
            BuildComment.generateClassComment(br, tableInfo.getComment()+" Mapper");
            br.write("public interface " + className + "<T, P> extends BaseMapper {");
            br.newLine();

            //根据索引生成
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();


            for (Map.Entry<String,List<FieldInfo>> entry : keyIndexMap.entrySet()){
                //方法名
                StringBuilder methodName = new StringBuilder();
                //参数列表
                StringBuilder paramsName = new StringBuilder();
                List<FieldInfo> keyFieldInfoList = entry.getValue();
                int l=1;
                for(FieldInfo fieldInfo : keyFieldInfoList){
                    methodName.append(StringUtils.capitalize(fieldInfo.getPropertyName()));
                    paramsName.append(String.format("@Param(\"%s\") ",fieldInfo.getPropertyName()))
                            .append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName());
                    if(l++<keyFieldInfoList.size()){
                        methodName.append("And");
                        paramsName.append(", ");
                    }

                }

                //查询
                BuildComment.generateMethodComment(br,"根据"+methodName+"查询");
                br.write("\tT selectBy"+methodName+"("+paramsName+");");
                br.newLine();
                br.newLine();
                //更新
                BuildComment.generateMethodComment(br,"根据"+methodName+"更新");
                br.write("\tInteger updateBy"+methodName+"(@Param(\"bean\") T t, "+paramsName+");");
                br.newLine();
                br.newLine();
                //删除
                BuildComment.generateMethodComment(br,"根据"+methodName+"删除");
                br.write("\tInteger deleteBy"+methodName+"("+paramsName+");");
                br.newLine();
                br.newLine();

            }


            br.write("}");
            br.flush();

        } catch (Exception e) {
            logger.error("创建Mappers失败", e);
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
