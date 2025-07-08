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

public class BuildController {
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    public static void execute(TableInfo tableInfo) {
        //创建impl包
        File folder = new File(Constants.PATH_CONTROLLER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String controllerName = tableInfo.getBeanName() + "Controller";
        String serviceName=tableInfo.getBeanName()+"Service";
        String serviceBeanName=StringUtils.uncapitalize(tableInfo.getBeanName()+"Service");
        File proFile = new File(folder, controllerName + ".java");

        OutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter br = null;


        try {
            out = new FileOutputStream(proFile);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            br = new BufferedWriter(writer);
            br.write("package " + Constants.PACKAGE_CONTROLLER + ";");
            br.newLine();
            //导包
            br.write("import " + Constants.PACKAGE_POJO + "." + tableInfo.getBeanName() + ";");
            br.newLine();
            br.write("import " + Constants.PACKAGE_PARAM + "." + tableInfo.getBeanParamName() + ";");
            br.newLine();
            br.write("import java.util.List;");
            br.newLine();
            br.write("import " + Constants.PACKAGE_VO + "." + "ResponseVO;");
            br.newLine();

            br.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName+";");
            br.newLine();
            br.write("import org.springframework.web.bind.annotation.RestController;");
            br.newLine();
            br.write("import org.springframework.web.bind.annotation.RequestMapping;");
            br.newLine();
            br.write("import org.springframework.web.bind.annotation.RequestBody;");
            br.newLine();
            br.write("import jakarta.annotation.Resource;");
            br.newLine();



            BuildComment.generateClassComment(br, tableInfo.getComment() + "Controller");
            br.write("@RestController()");
            br.newLine();
            br.write(String.format("@RequestMapping(\"%s\")",StringUtils.uncapitalize(tableInfo.getBeanName())));
            br.newLine();
            br.write("public class " + controllerName +  " extends BaseController{");
            br.newLine();
            br.newLine();

            //装配service
            br.write("\t@Resource");
            br.newLine();

            br.write(String.format("\tprivate %s %s;", serviceName,serviceBeanName));
            br.newLine();

            BuildComment.generateMethodComment(br,"");
            br.write(String.format("\t@RequestMapping(\"%s\")","loadDataList"));
            br.newLine();
            br.write(String.format("\tpublic ResponseVO %s (%s query) {","loadDataList",tableInfo.getBeanParamName()));
            br.newLine();
            br.write(String.format("\t\treturn getSuccessResponseVO(%s.findListByParam(%s));",serviceBeanName,"query"));
            br.newLine();
            br.write("\t}");
            br.newLine();


            BuildComment.generateFieldComment(br, "新增");
            br.write(String.format("\t@RequestMapping(\"%s\")","add"));
            br.newLine();
            br.write(String.format("\tpublic ResponseVO add(%s bean){", tableInfo.getBeanName()));
            br.newLine();
            br.write("\t\tthis." + serviceBeanName + ".add(bean);");
            br.newLine();
            br.write("\t\treturn getSuccessResponseVO(null);");
            br.newLine();
            br.write("\t}");
            br.newLine();
            BuildComment.generateFieldComment(br, "批量新增");
            br.write(String.format("\t@RequestMapping(\"%s\")","addBatch"));
            br.newLine();
            br.write(String.format("\tpublic ResponseVO addBatch(@RequestBody List<%s> listBean){", tableInfo.getBeanName()));
            br.newLine();
            br.write("\t\tthis." + serviceBeanName + ".addBatch(listBean);");
            br.newLine();
            br.write("\t\treturn getSuccessResponseVO(null);");
            br.newLine();
            br.write("\t}");
            br.newLine();
            br.newLine();

            BuildComment.generateFieldComment(br, "批量新增或修改");
            br.write(String.format("\t@RequestMapping(\"%s\")","addBatchOrUpdateBatch"));
            br.newLine();
            br.write(String.format("\tpublic ResponseVO addBatchOrUpdateBatch(@RequestBody List<%s> listBean){", tableInfo.getBeanName()));
            br.newLine();
            br.write("\t\tthis." + serviceBeanName + ".addBatchOrUpdateBatch(listBean);");
            br.newLine();
            br.write("\t\treturn getSuccessResponseVO(null);");
            br.newLine();
            br.write("\t}");
            br.newLine();
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();


            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                //方法名
                StringBuilder methodName = new StringBuilder();
                //参数列表
                StringBuilder paramsName = new StringBuilder();
                //参数名列表
                StringBuilder paramsBuilder = new StringBuilder();
                List<FieldInfo> keyFieldInfoList = entry.getValue();
                int l = 1;
                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    methodName.append(StringUtils.capitalize(fieldInfo.getPropertyName()));
                    paramsName.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName());
                    paramsBuilder.append(fieldInfo.getPropertyName());
                    if (l++ < keyFieldInfoList.size()) {
                        methodName.append("And");
                        paramsName.append(", ");
                        paramsBuilder.append(", ");
                    }

                }

                //查询
                BuildComment.generateMethodComment(br, "根据" + methodName + "查询");
                br.write(String.format("\t@RequestMapping(\"%s\")","get" + tableInfo.getBeanName() + "By" + methodName));
                br.newLine();
                br.write("\tpublic ResponseVO get" + tableInfo.getBeanName() + "By" + methodName + "(" + paramsName + "){");
                br.newLine();
                br.write(String.format("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".get" + tableInfo.getBeanName() + "By" + methodName + "(%s));", paramsBuilder));
                br.newLine();
                br.write("\t}");
                br.newLine();
                br.newLine();

                //更新
                BuildComment.generateMethodComment(br, "根据" + methodName + "更新");
                br.write(String.format("\t@RequestMapping(\"%s\")","update" + tableInfo.getBeanName() + "By" + methodName));
                br.newLine();
                br.write("\tpublic ResponseVO update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + paramsName + "){");
                br.newLine();
                br.write(String.format("\t\tthis." + serviceBeanName + ".update" + tableInfo.getBeanName() + "By" + methodName + "(bean,%s);", paramsBuilder));
                br.newLine();
                br.write("\t\treturn getSuccessResponseVO(null);");
                br.newLine();
                br.write("\t}");
                br.newLine();
                br.newLine();
                //删除
                BuildComment.generateMethodComment(br, "根据" + methodName + "删除");
                br.write(String.format("\t@RequestMapping(\"%s\")","delete" + tableInfo.getBeanName() + "By" + methodName));
                br.newLine();
                br.write("\tpublic ResponseVO delete" + tableInfo.getBeanName() + "By" + methodName + "(" + paramsName + "){");
                br.newLine();
                br.write(String.format("\t\tthis." + serviceBeanName + ".delete" + tableInfo.getBeanName() + "By" + methodName + "(%s);", paramsBuilder));
                br.newLine();
                br.write("\t\treturn getSuccessResponseVO(null);");
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
