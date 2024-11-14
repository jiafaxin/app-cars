package com.autohome.app.cars.mapper.popauto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @description:
 * @author: 范海涛
 * @time: 2021-12-02 15:50
 */
public class ParamTypePo implements Serializable {

    @JsonProperty("paramtypeid")
    private Integer paramTypeId;
    @JsonProperty("paramtypename")
    private String paramTypeName;
    @JsonIgnore
    private Integer typeSort;

    public Integer getParamTypeId() {
        return paramTypeId;
    }

    public void setParamTypeId(Integer paramTypeId) {
        this.paramTypeId = paramTypeId;
    }

    public String getParamTypeName() {
        return paramTypeName;
    }

    public void setParamTypeName(String paramTypeName) {
        this.paramTypeName = paramTypeName;
    }

    public Integer getTypeSort() {
        return typeSort;
    }

    public void setTypeSort(Integer typeSort) {
        this.typeSort = typeSort;
    }
}
