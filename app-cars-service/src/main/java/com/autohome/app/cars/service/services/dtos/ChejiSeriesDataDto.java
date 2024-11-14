package com.autohome.app.cars.service.services.dtos;

public class ChejiSeriesDataDto {
    /**
     * seriesid : 6576
     * seriesname : 理想汽车-理想L9
     * content : 你好，理想同学
     * iconurl : https://app2.autoimg.cn/g30/M00/83/29/ChsFJ2LNMyyACjpVAAAQQqrbBms224.png
     * linkurl : autohome://insidebrowserwk?url=http%3A%2F%2Fzt.autohome.com.cn%2Fcheji%2Fmercedes-c
     */

    private int seriesid;
    private String seriesname;
    private String content;
    private String iconurl;
    private String linkurl;
    private String browserparam;

    public String getBrowserparam() {
        return browserparam;
    }

    public void setBrowserparam(String browserparam) {
        this.browserparam = browserparam;
    }

    public int getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(int seriesid) {
        this.seriesid = seriesid;
    }

    public String getSeriesname() {
        return seriesname;
    }

    public void setSeriesname(String seriesname) {
        this.seriesname = seriesname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }
}
