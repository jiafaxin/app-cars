package com.autohome.app.cars.mapper.popauto.entities;

/**
 * @description: 参数查询po
 * @author: 范海涛
 * @time: 2021-12-01 18:56
 */
public class ParamUnionPo {

    private int specid;
    private String specName;
    private Integer paramid;
    private String paramName;
    private int displayType;
    private int datatype;
    private String paramValue;
    private String subParamName;
    private int subParamId;
    private String subParamTextValue;
    private int subParamValue;
    private int price;
    private int subParamSort;

    public int getSpecid() {
        return specid;
    }

    public void setSpecid(int specid) {
        this.specid = specid;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Integer getParamid() {
        return paramid;
    }

    public void setParamid(Integer paramid) {
        this.paramid = paramid;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public int getDatatype() {
        return datatype;
    }

    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getSubParamName() {
        return subParamName;
    }

    public void setSubParamName(String subParamName) {
        this.subParamName = subParamName;
    }

    public int getSubParamId() {
        return subParamId;
    }

    public void setSubParamId(int subParamId) {
        this.subParamId = subParamId;
    }

    public String getSubParamTextValue() {
        return subParamTextValue;
    }

    public void setSubParamTextValue(String subParamTextValue) {
        this.subParamTextValue = subParamTextValue;
    }

    public int getDisplayType() {
        return displayType;
    }

    public void setDisplayType(int displayType) {
        this.displayType = displayType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSubParamValue() {
        return subParamValue;
    }

    public void setSubParamValue(int subParamValue) {
        this.subParamValue = subParamValue;
    }


    public int getSubParamSort() {
        return subParamSort;
    }

    public void setSubParamSort(int subParamSort) {
        this.subParamSort = subParamSort;
    }
}
