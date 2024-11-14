package com.autohome.app.cars.mapper.popauto.entities;

import com.autohome.app.cars.common.utils.CarSettings;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author : zzli
 * @description : 车型差异--配置项
 * @date : 2024/5/14 20:26
 */
@Data
public class SpecConfigItemRelaPic {
    /**
     * 车型id
     */
    private int specid;

    //配置分类id
    private int typeid;
    private String typename;
    /**
     * D.sort
     */
    private int typeSort;
    /**
     * 配置主项id
     */
    private int itemid;
    /**
     * 配置主项名称
     */
    private String itemname;

    /**
     * 配置主项值(0无,1标配,2选装)
     */
    private String itemvalue = "";
    /**
     * 配置主项默认logo
     */
    private String itemlogo;
    /**
     * 配置主项后台排序--B.Sort
     */
    private int itemorder;

    /**
     * 配置子项id
     */
    private int subitemid;
    /**
     * 配置子项名
     */
    private String subitemname;
    /**
     * 配置子项值(0无,1标配,2选装)
     */
    private int subitemvalue;
    /**
     * 配置子项默认logo
     */
    private String subitemlogo;
    /**
     * 配置子项后台排序
     */
    private int subitemorder;
    /**
     * 配置子项后台别名
     */
    private String subitemalias;

    /**
     * 配置主项或者子项编辑关联的图片id
     */
    private String picid;
    /**
     * 配置主项或者子项编辑关联的图片地址
     */
    private String picurl;

    private int dataType = 2; //2为配置，1为参数

    public String getPicurl() {
        if (StringUtils.isNotEmpty(this.picurl)) {
            if (this.picurl.indexOf("http") > -1) {
                return this.picurl;
            } else {
                return CarSettings.getInstance().GetFullImagePathByPrefix(this.picurl, "");
            }
        }
        return this.picurl;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecConfigItemRelaPic that = (SpecConfigItemRelaPic) o;
        return itemid == that.itemid
                && subitemid == that.subitemid
                && subitemvalue == that.subitemvalue
                && dataType == that.dataType
                && itemvalue.equals(that.itemvalue);
    }
    @Override
    public int hashCode() {
        return Objects.hash(this.dataType, this.itemid, this.itemvalue, this.subitemid, this.subitemvalue);
    }
}
