package com.AutoMybatis.Builder;

import com.AutoMybatis.Utils.DateTimeUtils;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BuildComment {
    //构建类注释
    public static void generateClassComment(BufferedWriter br, String classComment) throws IOException {
        /**
         * @Description:
         * @date:
         */
        br.write("/**");
        br.newLine();
        br.write(" * @Description:" + classComment);
        br.newLine();
        br.write(" * @date:" + DateTimeUtils.getTimeStamp());
        br.newLine();
        br.write(" */");
        br.newLine();
    }

    //构建字段注释
    public static void generateFieldComment(BufferedWriter br, String fieldComment) throws IOException {
        br.write("\t/**");
        br.newLine();
        br.write("\t * " + (fieldComment == null ? "" : fieldComment));
        br.newLine();
        br.write("\t */");
        br.newLine();
    }

    //构建方法注释
    public static void generateMethodComment(BufferedWriter br, String methodComment) throws IOException {
        br.write("\t/**");
        br.newLine();
        br.write("\t * @date:" + DateTimeUtils.getTimeStamp());
        br.newLine();
        br.write("\t * " + (methodComment == null ? "" : methodComment));
        br.newLine();
        br.write("\t */");
        br.newLine();

    }

    @Test
    public void test() {
        System.out.println();
    }
}
