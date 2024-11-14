package com.autohome.app.cars.mapper.popauto.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ParamItemPo implements Serializable {

    @JsonProperty("paramid")
    private Integer paramId;
    //参数名称
    @JsonProperty("paramname")
    private String paramName;

    @JsonProperty("typeid")
    private Integer typeId;

    @JsonProperty("paramsort")
    private Integer paramSort;
    //值类型：1整数，2小数，3字符串，4多项值
    @JsonProperty("datatype")
    private Integer dataType;
    //    乘用车是否外显
    @JsonProperty("isshow")
    private Integer isShow;
    //    商用车参数
    @JsonProperty("iscvcarparam")
    private Integer isCvcarParam;
    //    乘用车参数
    @JsonProperty("iscarparam")
    private Integer isCarParam;
    //    房车参数
    @JsonProperty("ishosecarparam")
    private Integer isHoseCarParam;

    //    卡车货车参数
    @JsonProperty("istruckcarparam")
    private Integer isTruckCarParam;
    //    显示方式，1竖排，0横排
    @JsonProperty("displaytype")
    private Integer displayType;

    //是否动态外显
    private Integer dynamicShow;

    public Integer getParamId() {
        return paramId;
    }

    public void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getParamSort() {
        return paramSort;
    }

    public void setParamSort(Integer paramSort) {
        this.paramSort = paramSort;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Integer getIsCvcarParam() {
        return isCvcarParam;
    }

    public void setIsCvcarParam(Integer isCvcarParam) {
        this.isCvcarParam = isCvcarParam;
    }

    public Integer getIsCarParam() {
        return isCarParam;
    }

    public void setIsCarParam(Integer isCarParam) {
        this.isCarParam = isCarParam;
    }

    public Integer getIsHoseCarParam() {
        return isHoseCarParam;
    }

    public void setIsHoseCarParam(Integer isHoseCarParam) {
        this.isHoseCarParam = isHoseCarParam;
    }

    public Integer getIsTruckCarParam() {
        return isTruckCarParam;
    }

    public void setIsTruckCarParam(Integer isTruckCarParam) {
        this.isTruckCarParam = isTruckCarParam;
    }

    public Integer getDisplayType() {
        return displayType;
    }

    public void setDisplayType(Integer displayType) {
        this.displayType = displayType;
    }

    public Integer getDynamicShow() {
        return dynamicShow;
    }

    public void setDynamicShow(Integer dynamicShow) {
        this.dynamicShow = dynamicShow;
    }
}
