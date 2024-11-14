package com.autohome.app.cars.service.services.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ShuangZhiEntryConfig {

    /**
     * seriesid : 7207
     * linkurl : http://fs.cupid.autohome.com.cn/ug_spa/intelligenttest
     * vractiveinfo : {"imgurl":"http://nfiles3.autohome.com.cn/zrjcpk10/cjsj_top_230811.png","imgwidth":68,"imgheight":46}
     * winnertestinfo : {}
     * configtitle : 双智入口
     */

    private int isopen;
    private int seriesid;
    private String cfgscheme;
    private String seriesscheme;
    private VractiveinfoBean vractiveinfo;
    private WinnertestinfoBean winnertestinfo;
    private String configtitle;

    @Data
    public static class VractiveinfoBean {
        /**
         * imgurl : http://nfiles3.autohome.com.cn/zrjcpk10/cjsj_top_230811.png
         * imgwidth : 68
         * imgheight : 46
         */

        private String imgurl;
        private int imgwidth;
        private int imgheight;
    }

    @Data
    public static class WinnertestinfoBean {

        private String leftimg;
        private List<DatainfoBean> datainfo;

        @Data
        public static class DatainfoBean {
            /**
             * title : title1
             * subtitle : subtitle1
             */

            private String title;
            private String subtitle;
        }
    }
}
