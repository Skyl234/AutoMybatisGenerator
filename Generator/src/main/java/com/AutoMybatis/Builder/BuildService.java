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

public class BuildService {

    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo) {
        //创建pojo包
        File folder = new File(Constants.PATH_SERVICE);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String serviceName=tableInfo.getBeanName()+"Service";
        File proFile = new File(folder, serviceName + ".java");

        OutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter br = null;


        try {
            out = new FileOutputStream(proFile);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            br = new BufferedWriter(writer);
            br.write("package " + Constants.PACKAGE_SERVICE + ";");
            br.newLine();
            //导包
            br.write("import "+Constants.PACKAGE_POJO+"."+tableInfo.getBeanName()+";");
            br.newLine();
            br.write("import "+Constants.PACKAGE_PARAM+"."+tableInfo.getBeanParamName()+";");
            br.newLine();
            br.write("import java.util.List;");
            br.newLine();
            br.write("import "+Constants.PACKAGE_VO+"."+"PaginationResultVO;");
            br.newLine();


            BuildComment.generateClassComment(br,tableInfo.getComment()+"Service");
            br.write("public interface "+serviceName+" {");
            br.newLine();
            br.newLine();
            BuildComment.generateFieldComment(br,"根据条件查询列表");
            br.write(String.format("\tList<%s> findListByParam(%s param);",tableInfo.getBeanName(),tableInfo.getBeanParamName()));
            br.newLine();
            BuildComment.generateFieldComment(br,"根据条件查询数量");
            br.write(String.format("\tLong findCountByParam(%s param);",tableInfo.getBeanParamName()));
            br.newLine();
            BuildComment.generateFieldComment(br,"分页查询");
            br.write(String.format("\tPaginationResultVO<%s> findListByPage(%s query);",tableInfo.getBeanName(),tableInfo.getBeanParamName()));
            br.newLine();

            BuildComment.generateFieldComment(br,"新增");
            br.write(String.format("\tInteger add(%s bean);",tableInfo.getBeanName()));
            br.newLine();

            BuildComment.generateFieldComment(br,"批量新增");
            br.write(String.format("\tInteger addBatch(List<%s> listBean);",tableInfo.getBeanName()));
            br.newLine();

            BuildComment.generateFieldComment(br,"批量新增或修改");
            br.write(String.format("\tInteger addBatchOrUpdateBatch(List<%s> listBean);",tableInfo.getBeanName()));
            br.newLine();

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
                    paramsName.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName());
                    if(l++<keyFieldInfoList.size()){
                        methodName.append("And");
                        paramsName.append(", ");
                    }

                }

                //查询
                BuildComment.generateMethodComment(br,"根据"+methodName+"查询");
                br.write("\t"+tableInfo.getBeanName()+" get"+tableInfo.getBeanName()+"By"+methodName+"("+paramsName+");");
                br.newLine();
                br.newLine();
                //更新
                BuildComment.generateMethodComment(br,"根据"+methodName+"更新");
                br.write("\tInteger update"+tableInfo.getBeanName()+"By"+methodName+"("+tableInfo.getBeanName()+" bean, "+paramsName+");");
                br.newLine();
                br.newLine();
                //删除
                BuildComment.generateMethodComment(br,"根据"+methodName+"删除");
                br.write("\tInteger delete"+tableInfo.getBeanName()+"By"+methodName+"("+paramsName+");");
                br.newLine();
                br.newLine();

            }

            br.write("}");
            br.flush();

        } catch (Exception e) {
            logger.error("创建Service失败", e);
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
