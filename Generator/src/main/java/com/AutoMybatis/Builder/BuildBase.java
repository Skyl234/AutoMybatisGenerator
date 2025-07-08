package com.AutoMybatis.Builder;

import com.AutoMybatis.Bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;


//工具类或通用代码生成
public class BuildBase {

    private static Logger logger = LoggerFactory.getLogger(BuildBase.class);

    public static void execute() {
        //生成BaseMapper
        List<String> packageInfoList = new ArrayList<>();
        packageInfoList.add("package " + Constants.PACKAGE_MAPPER);
        build(packageInfoList, "BaseMapper", Constants.PATH_MAPPER);

        //生成PageSize枚举
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_ENUM);
        build(packageInfoList, "PageSize", Constants.PATH_ENUM);

        //生成PageHelper
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_PARAM);
        packageInfoList.add("import " + Constants.PACKAGE_ENUM+".PageSize");
        build(packageInfoList, "PageHelper", Constants.PATH_PARAM);

        //生成BaseQuery
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_PARAM);
        build(packageInfoList, "BaseQuery", Constants.PATH_PARAM);

        //生成分页VO对象
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_VO);
        build(packageInfoList, "PaginationResultVO", Constants.PATH_VO);

        //生成响应VO对象
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_VO);
        build(packageInfoList, "ResponseVO", Constants.PATH_VO);

        //生成自定义异常对象
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_EXCEPTION);
        packageInfoList.add("import " + Constants.PACKAGE_ENUM+".ResponseCodeEnum");
        build(packageInfoList, "BusinessException", Constants.PATH_EXCEPTION);

        //状态码枚举
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_ENUM);
        build(packageInfoList, "ResponseCodeEnum", Constants.PATH_ENUM);

        //生成基础controller
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        packageInfoList.add("import " + Constants.PACKAGE_ENUM+".ResponseCodeEnum");
        packageInfoList.add("import " + Constants.PACKAGE_VO+".ResponseVO");
        build(packageInfoList, "BaseController", Constants.PATH_CONTROLLER);

        //生成异常处理Controller
        packageInfoList.clear();
        packageInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        packageInfoList.add("import " + Constants.PACKAGE_ENUM+".ResponseCodeEnum");
        packageInfoList.add("import " + Constants.PACKAGE_VO+".ResponseVO");
        packageInfoList.add("import " + Constants.PACKAGE_EXCEPTION+".BusinessException");
        build(packageInfoList, "GlobalExceptionHandlerController", Constants.PATH_CONTROLLER);

    }

    private static void build(List<String> packageInfoList, String fileName, String outputPath) {
        File folder = new File(outputPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File javaFile = new File(outputPath, fileName + ".java");
        //输出流
        OutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        //输入流
        InputStream in = null;
        InputStreamReader reader = null;
        BufferedReader br = null;

        try {
            out = new FileOutputStream(javaFile);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            bw = new BufferedWriter(writer);

            String templatePath = Objects.requireNonNull
                            (BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt"))
                    .getPath();

            in=new FileInputStream(templatePath);
            reader=new InputStreamReader(in, StandardCharsets.UTF_8);
            br=new BufferedReader(reader);

            for (String packageInfo: packageInfoList){
                bw.write(packageInfo+";");
                bw.newLine();
                if(packageInfo.contains("package")){
                    bw.newLine();
                }
            }
            String buffer=null;
            while ((buffer=br.readLine())!=null){
                bw.write(buffer);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Base失败", e);
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
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(reader!=null){
                try {
                    reader.close();
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
