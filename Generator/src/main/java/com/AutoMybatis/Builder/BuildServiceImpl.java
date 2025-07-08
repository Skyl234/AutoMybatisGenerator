package com.AutoMybatis.Builder;

import com.AutoMybatis.Bean.Constants;
import com.AutoMybatis.Bean.FieldInfo;
import com.AutoMybatis.Bean.TableInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class BuildServiceImpl {


    private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);

    public static void execute(TableInfo tableInfo) {
        //创建impl包
        File folder = new File(Constants.PATH_SERVICE_IMPL);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String serviceName=tableInfo.getBeanName()+"Service";
        String serviceImplName=tableInfo.getBeanName()+"ServiceImpl";
        String mapperName=tableInfo.getBeanName()+Constants.SUFFIX_MAPPERS;
        String mapperBeanName=StringUtils.uncapitalize(mapperName);
        File proFile = new File(folder, serviceImplName + ".java");

        OutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter br = null;


        try {
            out = new FileOutputStream(proFile);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            br = new BufferedWriter(writer);
            br.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
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
            br.write("import "+Constants.PACKAGE_PARAM+"."+"PageHelper;");
            br.newLine();
            br.write("import "+Constants.PACKAGE_ENUM+"."+"PageSize;");
            br.newLine();
            br.write("import "+Constants.PACKAGE_MAPPER+"."+mapperName+";");
            br.newLine();
            br.write("import "+Constants.PACKAGE_SERVICE+"."+serviceName+";");
            br.newLine();
            br.write("import org.springframework.stereotype.Service;");
            br.newLine();
            br.write("import jakarta.annotation.Resource;");
            br.newLine();


            BuildComment.generateClassComment(br,tableInfo.getComment()+"ServiceImpl");
            br.write(String.format("@Service(\"%s\")",StringUtils.uncapitalize(serviceName)));
            br.newLine();
            br.write("public class "+serviceImplName+" implements "+tableInfo.getBeanName()+"Service {");
            br.newLine();
            br.newLine();

            //装配mapper
            br.write("\t@Resource");
            br.newLine();

            br.write(String.format("\tprivate %s <%s,%s> %s;",mapperName,tableInfo.getBeanName(),tableInfo.getBeanParamName(),StringUtils.uncapitalize(mapperName)));
            br.newLine();
            BuildComment.generateFieldComment(br,"根据条件查询列表");
            br.write(String.format("\tpublic List<%s> findListByParam(%s query){",tableInfo.getBeanName(),tableInfo.getBeanParamName()));
            br.newLine();
            br.write(String.format("\t\treturn this.%s.selectList(query);",mapperBeanName));
            br.newLine();
            br.write("\t}");
            br.newLine();
            BuildComment.generateFieldComment(br,"根据条件查询数量");
            br.write(String.format("\tpublic Long findCountByParam(%s query){",tableInfo.getBeanParamName()));
            br.newLine();
            br.write(String.format("\t\treturn this.%s.selectCount(query);",mapperBeanName));
            br.newLine();
            br.write("\t}");
            br.newLine();
            BuildComment.generateFieldComment(br,"分页查询");
            br.write(String.format("\tpublic PaginationResultVO<%s> findListByPage(%s query){",tableInfo.getBeanName(),tableInfo.getBeanParamName()));
            br.newLine();
            br.write("\t\tLong count = this.findCountByParam(query);");
            br.newLine();
            br.write("\t\tint pageSize = query.getPageSize()==null?PageSize.SIZE20.getSize() : query.getPageSize();");
            br.newLine();
            br.write("\t\tPageHelper pageHelper = new PageHelper(query.getPageNo(), count,pageSize);");
            br.newLine();
            br.write("\t\tquery.setPageHelper(pageHelper);");
            br.newLine();
            br.write(String.format("\t\tList<%s> list = this.findListByParam(query);",tableInfo.getBeanName()));
            br.newLine();
            br.write("\t\treturn new PaginationResultVO<>(count, pageHelper.getPageSize(), pageHelper.getPageNo(), pageHelper.getPageTotal(), list);");
            br.newLine();
            br.write("\t}");
            br.newLine();
            BuildComment.generateFieldComment(br,"新增");
            br.write(String.format("\tpublic Integer add(%s bean){",tableInfo.getBeanName()));
            br.newLine();
            br.write("\t\treturn this."+mapperBeanName+".insert(bean);");
            br.newLine();
            br.write("\t}");
            br.newLine();
            BuildComment.generateFieldComment(br,"批量新增");
            br.write(String.format("\tpublic Integer addBatch(List<%s> listBean){",tableInfo.getBeanName()));
            br.newLine();
            br.write("\t\tif (listBean == null || listBean.isEmpty()) {");
            br.newLine();
            br.write("\t\treturn 0;");
            br.newLine();
            br.write("\t\t}");
            br.newLine();
            br.write("\t\treturn this."+mapperBeanName+".insertBatch(listBean);");
            br.newLine();
            br.write("\t}");
            br.newLine();
            br.newLine();

            BuildComment.generateFieldComment(br,"批量新增或修改");
            br.write(String.format("\tpublic Integer addBatchOrUpdateBatch(List<%s> listBean){",tableInfo.getBeanName()));
            br.newLine();
            br.write("\t\tif (listBean == null || listBean.isEmpty()) {");
            br.newLine();
            br.write("\t\treturn 0;");
            br.newLine();
            br.write("\t\t}");
            br.newLine();
            br.write("\t\treturn this."+mapperBeanName+".insertOrUpdateBatch(listBean);");
            br.newLine();
            br.write("\t}");
            br.newLine();
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();


            for (Map.Entry<String,List<FieldInfo>> entry : keyIndexMap.entrySet()){
                //方法名
                StringBuilder methodName = new StringBuilder();
                //参数列表
                StringBuilder paramsName = new StringBuilder();
                //参数名列表
                StringBuilder paramsBuilder=new StringBuilder();
                List<FieldInfo> keyFieldInfoList = entry.getValue();
                int l=1;
                for(FieldInfo fieldInfo : keyFieldInfoList){
                    methodName.append(StringUtils.capitalize(fieldInfo.getPropertyName()));
                    paramsName.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName());
                    paramsBuilder.append(fieldInfo.getPropertyName());
                    if(l++<keyFieldInfoList.size()){
                        methodName.append("And");
                        paramsName.append(", ");
                        paramsBuilder.append(", ");
                    }

                }

                //查询
                BuildComment.generateMethodComment(br,"根据"+methodName+"查询");
                br.write("\tpublic "+tableInfo.getBeanName()+" get"+tableInfo.getBeanName()+"By"+methodName+"("+paramsName+"){");
                br.newLine();
                br.write(String.format("\t\treturn this."+mapperBeanName+".selectBy"+methodName+"(%s);",paramsBuilder));
                br.newLine();
                br.write("\t}");
                br.newLine();
                br.newLine();

                //更新
                BuildComment.generateMethodComment(br,"根据"+methodName+"更新");
                br.write("\tpublic Integer update"+tableInfo.getBeanName()+"By"+methodName+"("+tableInfo.getBeanName()+" bean, "+paramsName+"){");
                br.newLine();
                br.write(String.format("\t\treturn this."+mapperBeanName+".updateBy"+methodName+"(bean,%s);",paramsBuilder));
                br.newLine();
                br.write("\t}");
                br.newLine();
                br.newLine();
                //删除
                BuildComment.generateMethodComment(br,"根据"+methodName+"删除");
                br.write("\tpublic Integer delete"+tableInfo.getBeanName()+"By"+methodName+"("+paramsName+"){");
                br.newLine();
                br.write(String.format("\t\treturn this."+mapperBeanName+".deleteBy"+methodName+"(%s);",paramsBuilder));
                br.newLine();
                br.write("\t}");
                br.newLine();
                br.newLine();

            }
            br.newLine();
            br.write("}");
            br.flush();

        } catch (Exception e) {
            logger.error("创建ServiceImpl失败", e);
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
