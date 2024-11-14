package com.autohome.app.cars.service.services.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 车系横栏配置
 * @date : 2024/4/2 11:03
 */
public class OperatePositionConfig {

    private Integer seriesid;
    private List<OperatePosition> list = new ArrayList<>();

    public Integer getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(Integer seriesid) {
        this.seriesid = seriesid;
    }

    public List<OperatePosition> getList() {
        return list;
    }

    public void setList(List<OperatePosition> list) {
        this.list = list;
    }

    public static class OperatePosition {
        private String iconurl;
        private String bgurl;
        private String content;
        private String linkurl;
        private int typeid;

        public String getBgurl() {
            return bgurl;
        }

        public void setBgurl(String bgurl) {
            this.bgurl = bgurl;
        }

        public String getIconurl() {
            return iconurl;
        }

        public void setIconurl(String iconurl) {
            this.iconurl = iconurl;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
        }

        public int getTypeid() {
            return typeid;
        }

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }
    }
}
