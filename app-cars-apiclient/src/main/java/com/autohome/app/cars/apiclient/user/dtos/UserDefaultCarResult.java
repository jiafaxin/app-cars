package com.autohome.app.cars.apiclient.user.dtos;

/**
 * @author wbs
 * @date 2024/6/6
 */
public class UserDefaultCarResult {
    private int autoid;
    private int userid;
    private int brandid;
    private int seriesid;
    private int specid;
    private String carnumber;
    private int isdefault;

    public int getAutoid() {
        return autoid;
    }

    public void setAutoid(int autoid) {
        this.autoid = autoid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getBrandid() {
        return brandid;
    }

    public void setBrandid(int brandid) {
        this.brandid = brandid;
    }

    public int getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(int seriesid) {
        this.seriesid = seriesid;
    }

    public int getSpecid() {
        return specid;
    }

    public void setSpecid(int specid) {
        this.specid = specid;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public int getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(int isdefault) {
        this.isdefault = isdefault;
    }
}
