package com.autohome.app.cars.mapper.popauto.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * @ Author     ：lvming
 * @ Date       ：Created in 17:25 2020/10/30
 * @ Description：车型参数配置关联图片实体
 * @ Modified By：
 * @Version: $
 */
public class SpecParamConfigPicInfoEntity implements Serializable {

    public SpecParamConfigPicInfoEntity(){
        super();
    }


    public SpecParamConfigPicInfoEntity(Integer seriesId, Integer specId, Integer dataType, Integer itemId, String itemName, String valu, Integer specOrdercls) {
        this.seriesid = seriesId;
        this.specid = specId;
        this.datatype = dataType;
        this.itemid = itemId;
        this.itemname = itemName;
        this.valu = valu;
        this.specordercls = specOrdercls;
    }

    /**
     * 车系id
     */
    private Integer seriesid;
    /**
     * 车型id
     */
    private Integer specid;
    /**
     * 数据业务类型：1参数、2配置
     */
    private Integer datatype;
    /**
     * 参数或配置项id
     */
    private Integer itemid;
    /**
     * 参数配置项名
     */
    private String itemname;
    /**
     * 参数值
     */
    private String valu;
    /**
     * 配置子项id
     */
    private Integer subitemid;
    /**
     * 配置项名称
     */
    private String subitemname;
    /**
     * 图片id
     */
    private Integer picid;
    /**
     * 图片地址
     */
    private String picurl;
    /**
     * 子项排序
     */
    private Integer subitemordercls;
    /**
     * 车型后台排序
     */
    private Integer specordercls;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecParamConfigPicInfoEntity)) return false;
        SpecParamConfigPicInfoEntity that = (SpecParamConfigPicInfoEntity) o;
        return getSpecid().equals(that.getSpecid()) &&
                getDatatype().equals(that.getDatatype()) &&
                getItemid().equals(that.getItemid()) &&
                getItemname().equals(that.getItemname()) &&
                getValu().equals(that.getValu()) &&
                getSpecordercls().equals(that.getSpecordercls());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpecid(), getDatatype(), getItemid(), getItemname(), getValu(), getSpecordercls());
    }

    public String getValu() {
        return valu;
    }

    public void setValu(String valu) {
        this.valu = valu;
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
        if (picurl.indexOf('~') > -1) {
            return picurl.replace("~", "https://car2.autoimg.cn");
        }
        return picurl.replace("/cardfs", "https://car2.autoimg.cn/cardfs");
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public Integer getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(Integer seriesid) {
        this.seriesid = seriesid;
    }

    public Integer getSubitemordercls() {
        return subitemordercls;
    }

    public void setSubitemordercls(Integer subitemordercls) {
        this.subitemordercls = subitemordercls;
    }

    public Integer getSpecordercls() {
        return specordercls;
    }

    public void setSpecordercls(Integer specordercls) {
        this.specordercls = specordercls;
    }

}
