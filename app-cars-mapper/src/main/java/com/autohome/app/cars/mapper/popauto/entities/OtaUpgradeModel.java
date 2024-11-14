package com.autohome.app.cars.mapper.popauto.entities;

import java.util.Date;

/**
 * ota升级信息表
 */
public class OtaUpgradeModel {

    private int id;

    private String version;

    private Date pushTime;

    private int level;

    private String levelTag;

    private String summary;

    private String content;

    private int brandId;

    private String brandName;

    private String brandLogo;

    private int brandSort;

    private int seriesId;

    private String seriesName;

    private String seriesImg;

    private int userId;

    private String remark;

    /**
     * 订单创建时间
     */
    private Date createdStime;

    /**
     * 订单修改时间
     */
    private Date modifiedStime;

    /**
     * 记录是否已经被删除
     */
    private int isDel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelTag() {
        return levelTag;
    }

    public void setLevelTag(String levelTag) {
        this.levelTag = levelTag;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedStime() {
        return createdStime;
    }

    public void setCreatedStime(Date createdStime) {
        this.createdStime = createdStime;
    }

    public Date getModifiedStime() {
        return modifiedStime;
    }

    public void setModifiedStime(Date modifiedStime) {
        this.modifiedStime = modifiedStime;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesImg() {
        return seriesImg;
    }

    public void setSeriesImg(String seriesImg) {
        this.seriesImg = seriesImg;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getBrandSort() {
        return brandSort;
    }

    public void setBrandSort(int brandSort) {
        this.brandSort = brandSort;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }
}
