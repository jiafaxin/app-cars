package com.autohome.app.cars.apiclient.user.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wbs
 * @date 2024/6/6
 */
public class UserAuthSeriesResult {

    private int userid;
    private List<AuthseriesResult> list = new ArrayList<AuthseriesResult>();


    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public List<AuthseriesResult> getList() {
        return list;
    }

    public void setList(List<AuthseriesResult> list) {
        this.list = list;
    }

    public static class AuthseriesResult{
        private int id;

        private int userId;

        private int brandId;

        private String brandName;

        private int seriesId;

        private String seriesName;

        private int specId;

        private String specName;

        private int levels;

        private int carscore;

        private int carLevels;

        private String iconpc;

        private String iconpcsvg;

        private String iconm;

        private String iconmsvg;
        private int tsrzc;//是否为入参车系id的认证车主,包装逻辑用

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getBrandId() {
            return brandId;
        }

        public void setBrandId(int brandId) {
            this.brandId = brandId;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
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

        public int getSpecId() {
            return specId;
        }

        public void setSpecId(int specId) {
            this.specId = specId;
        }

        public String getSpecName() {
            return specName;
        }

        public void setSpecName(String specName) {
            this.specName = specName;
        }

        public int getLevels() {
            return levels;
        }

        public void setLevels(int levels) {
            this.levels = levels;
        }

        public int getCarscore() {
            return carscore;
        }

        public void setCarscore(int carscore) {
            this.carscore = carscore;
        }

        public int getCarLevels() {
            return carLevels;
        }

        public void setCarLevels(int carLevels) {
            this.carLevels = carLevels;
        }

        public String getIconpc() {
            return iconpc;
        }

        public void setIconpc(String iconpc) {
            this.iconpc = iconpc;
        }

        public String getIconpcsvg() {
            return iconpcsvg;
        }

        public void setIconpcsvg(String iconpcsvg) {
            this.iconpcsvg = iconpcsvg;
        }

        public String getIconm() {
            return iconm;
        }

        public void setIconm(String iconm) {
            this.iconm = iconm;
        }

        public String getIconmsvg() {
            return iconmsvg;
        }

        public void setIconmsvg(String iconmsvg) {
            this.iconmsvg = iconmsvg;
        }

        public int getTsrzc() {
            return tsrzc;
        }

        public void setTsrzc(int tsrzc) {
            this.tsrzc = tsrzc;
        }

    }
}
