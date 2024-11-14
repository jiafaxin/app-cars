package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @ Author     ：lvming
 * @ Date       ：Created in 15:54 2020/11/2
 * @ Description：1
 * @ Modified By：
 * @Version: $
 */
public class SpecParamConfigPicTipDto implements Serializable {
    /**
     * 车系id
     */
    @JsonIgnore
    private Integer seriesid;
    /**
     * 车型id
     */
    @JsonIgnore
    private Integer specid;
    /**
     *数据业务类型：1参数、2配置
     */
    private Integer datatype;
    /**
     *参数或配置项id
     */
    private Integer itemid;
    /**
     * 新参数、配置id datatype=1 时用 ParamItem.paramId, datatype=2 时用 ParamConfig.configId
     */
    private Integer newItemId;
    /**
     *参数配置项名
     */
    @JsonIgnore
    private String itemname;
    /**
     *配置子项id
     */
    @JsonIgnore
    private Integer subitemid;
    /**
     *配置项名称
     */
    @JsonIgnore
    private String subitemname;
    /**
     *图片id
     */
    @JsonIgnore
    private Integer picid;
    /**
     *图片地址
     */
    private String picurl ;

    public Integer getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(Integer seriesid) {
        this.seriesid = seriesid;
    }

    public Integer getSpecid() {
        return specid;
    }

    public void setSpecid(Integer specid) {
        this.specid = specid;
    }

    public Integer getDatatype() {
        return datatype;
    }

    public void setDatatype(Integer datatype) {
        this.datatype = datatype;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public Integer getSubitemid() {
        return subitemid;
    }

    public void setSubitemid(Integer subitemid) {
        this.subitemid = subitemid;
    }

    public String getSubitemname() {
        return subitemname;
    }

    public void setSubitemname(String subitemname) {
        this.subitemname = subitemname;
    }

    public Integer getPicid() {
        return picid;
    }

    public void setPicid(Integer picid) {
        this.picid = picid;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public Integer getNewItemId() {
        return newItemId;
    }

    public void setNewItemId(Integer newItemId) {
        this.newItemId = newItemId;
    }
}
