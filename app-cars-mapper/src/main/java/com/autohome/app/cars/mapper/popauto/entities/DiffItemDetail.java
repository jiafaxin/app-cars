package com.autohome.app.cars.mapper.popauto.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @projectName: microservice
 * @package: com.autohome.maintain.model.diffconfig.v2
 * @className: DiffItemDetail
 * @author: lvming
 * @description:
 * @date: 2022/11/17 14:39
 */
public class DiffItemDetail {
    //主配置项id
    private Integer id;
    //子配置项id,无子项时为0
    @JsonProperty("subid")
    private Integer subId;
    @JsonProperty("specid")
    private Integer specId;
    private String name;
    //无子项显示主项logo，否则子项logo
    private String logo;
    @JsonProperty("itemname")
    private String itemName;
    @JsonProperty("subitemname")
    private String subItemName;
    @JsonProperty("aliasname")
    private String aliasName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubId() {
        return subId;
    }

    public void setSubId(Integer subId) {
        this.subId = subId;
    }

    public Integer getSpecId() {
        return specId;
    }

    public void setSpecId(Integer specId) {
        this.specId = specId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSubItemName() {
        return subItemName;
    }

    public void setSubItemName(String subItemName) {
        this.subItemName = subItemName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public DiffItemDetail(Integer specId) {
        this.specId = specId;
    }

    public DiffItemDetail(Integer id, Integer subId, Integer specId) {
        this.id = id;
        this.subId = subId;
        this.specId = specId;
    }

    public DiffItemDetail() {
    }
}
