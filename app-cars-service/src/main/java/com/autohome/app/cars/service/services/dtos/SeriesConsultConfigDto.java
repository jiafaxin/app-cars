package com.autohome.app.cars.service.services.dtos;

import java.util.ArrayList;
import java.util.List;

public class SeriesConsultConfigDto {


    /**
     * seriesblacklist : [692]
     * abtestlist : [{"id":1,"abtest":"B","title":"免费咨询","subtitle":"到店时间 款式售价"},{"id":2,"abtest":"C","title":"立即预约","subtitle":"看真车 抢优惠"},{"id":3,"abtest":"D","title":"抢先试驾","subtitle":"免费体验 预约看车"}]
     * basedata : {"interval":10,"bgurl":"http://nfiles3.autohome.com.cn/zrjcpk10/bg_car_spec_zixun_container_part_0326.png","datalist":[{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"在哪买车更便宜？全款落地价？","btntitle":"立即咨询","position":1},{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"我要现场看车，预约试驾","btntitle":"立即预约","position":2},{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"获取最新优惠政策/底价/裸车价","btntitle":"立即咨询","position":3},{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"免费升级VIP，定制选车","btntitle":"一键升级","position":4},{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"参配看不懂？顾问免费帮你解读","btntitle":"立即咨询","position":5}]}
     */

    private BasedataBean basedata = new BasedataBean();
    private List<Integer> seriesblacklist = new ArrayList<>();
    private List<AbtestlistBean> abtestlist = new ArrayList<>();
    private List<BasedataBean> seriesdatalist = new ArrayList<>();
    private List<SeriesAbtestlistBean> seriesabtestlist = new ArrayList<>();

    public List<SeriesAbtestlistBean> getSeriesabtestlist() {
        return seriesabtestlist;
    }

    public void setSeriesabtestlist(List<SeriesAbtestlistBean> seriesabtestlist) {
        this.seriesabtestlist = seriesabtestlist;
    }
    public List<BasedataBean> getSeriesdatalist() {
        return seriesdatalist;
    }

    public void setSeriesdatalist(List<BasedataBean> seriesdatalist) {
        this.seriesdatalist = seriesdatalist;
    }

    public BasedataBean getBasedata() {
        return basedata;
    }

    public void setBasedata(BasedataBean basedata) {
        this.basedata = basedata;
    }

    public List<Integer> getSeriesblacklist() {
        return seriesblacklist;
    }

    public void setSeriesblacklist(List<Integer> seriesblacklist) {
        this.seriesblacklist = seriesblacklist;
    }

    public List<AbtestlistBean> getAbtestlist() {
        return abtestlist;
    }

    public void setAbtestlist(List<AbtestlistBean> abtestlist) {
        this.abtestlist = abtestlist;
    }

    public static class BasedataBean {
        /**
         * interval : 10
         * bgurl : http://nfiles3.autohome.com.cn/zrjcpk10/bg_car_spec_zixun_container_part_0326.png
         * datalist : [{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"在哪买车更便宜？全款落地价？","btntitle":"立即咨询","position":1},{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"我要现场看车，预约试驾","btntitle":"立即预约","position":2},{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"获取最新优惠政策/底价/裸车价","btntitle":"立即咨询","position":3},{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"免费升级VIP，定制选车","btntitle":"一键升级","position":4},{"icon":"http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png","title":"参配看不懂？顾问免费帮你解读","btntitle":"立即咨询","position":5}]
         */

        private int seriesid;
        private int interval;
        private String bgurl;
        private List<DatalistBean> datalist = new ArrayList<>();

        public int getSeriesid() {
            return seriesid;
        }

        public void setSeriesid(int seriesid) {
            this.seriesid = seriesid;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public String getBgurl() {
            return bgurl;
        }

        public void setBgurl(String bgurl) {
            this.bgurl = bgurl;
        }

        public List<DatalistBean> getDatalist() {
            return datalist;
        }

        public void setDatalist(List<DatalistBean> datalist) {
            this.datalist = datalist;
        }

        public static class DatalistBean {
            /**
             * icon : http://nfiles3.autohome.com.cn/zrjcpk10/spec_consult_icon_0326_1.png
             * title : 在哪买车更便宜？全款落地价？
             * btntitle : 立即咨询
             * position : 1
             */

            private String icon;
            private String title;
            private String btntitle;
            private int position;

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getBtntitle() {
                return btntitle;
            }

            public void setBtntitle(String btntitle) {
                this.btntitle = btntitle;
            }

            public int getPosition() {
                return position;
            }

            public void setPosition(int position) {
                this.position = position;
            }
        }
    }

    public static class AbtestlistBean {
        /**
         * id : 1
         * abtest : B
         * title : 免费咨询
         * subtitle : 到店时间 款式售价
         */

        private int id;
        private String abtest;
        private String title;
        private String subtitle;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAbtest() {
            return abtest;
        }

        public void setAbtest(String abtest) {
            this.abtest = abtest;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }
    }

    public static class SeriesAbtestlistBean {

        /**
         * seriesid : 692
         * abtestlist : [{"id":1,"abtest":"B","title":"新车揭秘","subtitle":"车图售价 配置解读"},{"id":2,"abtest":"C","title":"首发福利","subtitle":"新车亮相 预售权益"},{"id":3,"abtest":"D","title":"试驾有礼","subtitle":"新车体验 降价预定"}]
         */

        private int seriesid;
        private List<SeriesConsultConfigDto.AbtestlistBean> abtestlist = new ArrayList<>();

        public int getSeriesid() {
            return seriesid;
        }

        public void setSeriesid(int seriesid) {
            this.seriesid = seriesid;
        }

        public List<AbtestlistBean> getAbtestlist() {
            return abtestlist;
        }

        public void setAbtestlist(List<AbtestlistBean> abtestlist) {
            this.abtestlist = abtestlist;
        }
    }
}