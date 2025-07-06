package com.AutoMybatis.Bean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//表字段信息
public class TableInfo {

    //表名
    private String tableName;
    //bean名称
    private String beanName;
    //参数名称
    private String beanParamName;
    //表注释
    private String comment;
    //字段信息
    private List<FieldInfo> fieldInfoList;
    //唯一索引集合
    private Map<String,List<FieldInfo> >keyIndexMap=new LinkedHashMap<>();
    //扩展字段信息
    private List<FieldInfo> extensionFields;
    //是否有date类型
    private Boolean haveDate;
    //是否有时间类型
    private Boolean haveDateTime;
    //是否有BigDecimal类型
    private Boolean havaBigDecimal;

    @Override
    public String toString() {
        return "TableInfo{" +
                "tableName='" + tableName + '\'' +
                ", beanName='" + beanName + '\'' +
                ", beanParamName='" + beanParamName + '\'' +
                ", comment='" + comment + '\'' +
                ", fieldInfoList=" + fieldInfoList +
                ", keyIndexMap=" + keyIndexMap +
                ", extensionFields=" + extensionFields +
                ", haveDate=" + haveDate +
                ", haveDateTime=" + haveDateTime +
                ", havaBigDecimal=" + havaBigDecimal +
                '}';
    }

    public List<FieldInfo> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(List<FieldInfo> extensionFields) {
        this.extensionFields = extensionFields;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanParamName() {
        return beanParamName;
    }

    public void setBeanParamName(String beanParamName) {
        this.beanParamName = beanParamName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return keyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        this.keyIndexMap = keyIndexMap;
    }

    public Boolean getHaveDate() {
        return haveDate;
    }

    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
    }

    public Boolean getHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(Boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public Boolean getHavaBigDecimal() {
        return havaBigDecimal;
    }

    public void setHavaBigDecimal(Boolean havaBigDecimal) {
        this.havaBigDecimal = havaBigDecimal;
    }
}
