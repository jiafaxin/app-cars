package com.autohome.app.cars.mapper.popauto.entities;


import java.util.Date;

public class ChargeStationModel {
    private int id;

    private String name;

    private int brandId;

    private int seriesId;

    private String seriesIds;

    private String seriesName;

    private String seriesImg;

    /**
     * 0全系免费 1全系自费 2部分免费
     */
    private int payType;

    private String payTypeName;
    private String payTypeStr;

    private String payInfo;

    private int price;

    private String payItemInfo;

    private int sort;

    private int userId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getPayInfo() {
        if (this.payInfo == null) {
            return "";
        }
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPayItemInfo() {
        return payItemInfo;
    }

    public void setPayItemInfo(String payItemInfo) {
        this.payItemInfo = payItemInfo;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getSeriesIds() {
        return seriesIds;
    }

    public void setSeriesIds(String seriesIds) {
        this.seriesIds = seriesIds;
    }

    public String getPayTypeStr() {
        return payTypeStr;
    }

    public void setPayTypeStr(String payTypeStr) {
        this.payTypeStr = payTypeStr;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }
}
