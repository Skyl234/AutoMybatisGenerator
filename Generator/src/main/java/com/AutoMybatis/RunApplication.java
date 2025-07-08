package com.AutoMybatis;

import com.AutoMybatis.Bean.TableInfo;
import com.AutoMybatis.Builder.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;

public class RunApplication {
    public static void main(String[] args) {
        BuildBase.execute();
        List<TableInfo> tableInfoList =BuildTable.getTables();
        for (TableInfo tableInfo : tableInfoList){
            BuildPo.execute(tableInfo);
            BuildQuery.execute(tableInfo);
            BuildMapper.execute(tableInfo);
            BuildMapperXML.execute(tableInfo);
            BuildService.execute(tableInfo);
            BuildServiceImpl.execute(tableInfo);
            BuildController.execute(tableInfo);
        }
    }


}
