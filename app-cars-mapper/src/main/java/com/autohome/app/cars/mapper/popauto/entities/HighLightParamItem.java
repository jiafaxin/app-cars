package com.autohome.app.cars.mapper.popauto.entities;

import java.io.Serializable;

/**
 * @projectName: microservice
 * @package: com.autohome.maintain.model.highlight
 * @className: ParamItem
 * @author: lvming
 * @description:
 * @date: 2023/6/12 17:33
 */
public class HighLightParamItem implements Serializable {

    private Integer specId;
    private String paramName;
    private Integer paramId;
    private String paramValue;
    private Integer subParamId;
    private String subParamName;

    public Integer getSpecId() {
        return specId;
    }

    public void setSpecId(Integer specId) {
        this.specId = specId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Integer getParamId() {
        return paramId;
    }

    public void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Integer getSubParamId() {
        return subParamId;
    }

    public void setSubParamId(Integer subParamId) {
        this.subParamId = subParamId;
    }

    public String getSubParamName() {
        return subParamName;
    }

    public void setSubParamName(String subParamName) {
        this.subParamName = subParamName;
    }
}
