package com.autohome.app.cars.apiclient.dealer.dtos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HomeDealerListResult {
    /**
     * rowcount : 27
     * pagecount : 2
     * pageindex : 1
     * list : [{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g14/M11/8F/55/autohomedealer__wKgH1VggE-WASfXlAAITTp7ETn8304.jpg","dealerid":78109,"dealername":"北京奥嘉世茂","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=78109&pvareaid=&channel=2","dealerlinktel":"4009723340","dealeraddress":"北京市朝阳区金盏乡东苇路金港汽车公园C区19号（蟹岛对面）","distance":1671.9,"reviewdealerscore":4,"reviewcount":28,"reviewdealerid":44304,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g30/M02/6A/23/autohomedealer__ChwFlF_cbM-AB8yGAAVS9w-yuoM109.jpg","dealerid":2027552,"dealername":"北京奥吉通国门机场新建店","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=2027552&pvareaid=&channel=2","dealerlinktel":"4009682967","dealeraddress":"北京市首都机场T3航站楼南李天路南半壁店14号","distance":1677.9,"reviewdealerscore":3.5,"reviewcount":42,"reviewdealerid":56695,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g25/M08/5A/AD/autohomedealer__ChxkqWJ04C6AcF-VAAExoVLxqKY005.jpg","dealerid":1,"dealername":"运通博奥东四环奥迪","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=1&pvareaid=&channel=2","dealerlinktel":"4009684360","dealeraddress":"北京市朝阳区东四环南路366号（小武基桥西南）","distance":1655.8,"reviewdealerscore":4,"reviewcount":40,"reviewdealerid":36638,"tags":[],"brands":["33"],"comboactivitylist":[{"id":10,"type":1,"title":"收费-测试123","jumpurl":"http://dealer4s.m.yz.test.autohome.com.cn/benefitDetail?dealerId=1&benefitId=10&pvareaid=6858911&channel=2&channelCode=PALM_DEALER_4S","createdtime":"2023-03-20 17:15:15"}],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g15/M01/8F/15/autohomedealer__wKjByFgZXD6AfGyEAAJY-2KmASI935.jpg","dealerid":84993,"dealername":"北京华阳奥通","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=84993&pvareaid=&channel=2","dealerlinktel":"4008729804","dealeraddress":"北京市朝阳区北苑东路顾家庄桥北300米路西","distance":1664.3,"reviewdealerscore":4,"reviewcount":37,"reviewdealerid":45355,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g8/M0F/A7/97/autohomedealer__wKgH3lYbiwuAaDIHAAMbXxG_C2g013.jpg","dealerid":2016564,"dealername":"北京博瑞祥星奥迪国贸店","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=2016564&pvareaid=&channel=2","dealerlinktel":"4008685190","dealeraddress":"北京市朝阳区西大望路20号","distance":1658.1,"reviewdealerscore":3.5,"reviewcount":21,"reviewdealerid":51817,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g13/M04/48/91/autohomedealer__ChwEn2Db7YCAMAqmAADV13G7Htk413.jpg","dealerid":2,"dealername":"北京博瑞祥云奥迪","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=2&pvareaid=&channel=2","dealerlinktel":"4009720067","dealeraddress":"北京市朝阳区花虎沟2号京藏高速出京方向辅路清河收费站南50米路东","distance":1658.2,"reviewdealerscore":4.5,"reviewcount":555,"reviewdealerid":36639,"tags":[],"brands":["33"],"comboactivitylist":[{"id":28,"type":1,"title":"测试-车主服务","jumpurl":"http://dealer4s.m.yz.test.autohome.com.cn/benefitDetail?dealerId=2&benefitId=28&pvareaid=6858911&channel=2&channelCode=PALM_DEALER_4S","createdtime":"2023-04-06 13:42:03"}],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g26/M0B/09/00/autohomedealer__ChwFkF6WsTiAY4x_ABsUOFhLHZQ956.jpg","dealerid":1347,"dealername":"朝阳北路奥吉通奥迪中心","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=1347&pvareaid=&channel=2","dealerlinktel":"4009724235","dealeraddress":"北京市朝阳区朝阳北路甲45号","distance":1665.4,"reviewdealerscore":4,"reviewcount":26,"reviewdealerid":36970,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g1/M04/FA/6D/autohomedealer__ChsEj1zg1SuACUiBAAGh8AES1WA440.jpg","dealerid":129193,"dealername":"北京新丰泰奥迪中心","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=129193&pvareaid=&channel=2","dealerlinktel":"4001526985","dealeraddress":"北京市顺义区杜杨北街21号（机场东路与杜杨北街交汇处）","distance":1682.3,"reviewdealerscore":4,"reviewcount":31,"reviewdealerid":49931,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer0.autoimg.cn/shop/1903/20110506105208528.jpg","dealerid":1903,"dealername":"北京首汽腾迪","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=1903&pvareaid=&channel=2","dealerlinktel":"4008724310","dealeraddress":"北京市海淀区紫竹院南路5号","distance":1650.1,"reviewdealerscore":5,"reviewcount":5020,"reviewdealerid":37158,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g10/M0E/DD/82/autohomedealer__wKgH0Vhp5zuAUhQ9AAJ-Um734OU346.jpg","dealerid":127130,"dealername":"北京兴奥晟通","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=127130&pvareaid=&channel=2","dealerlinktel":"4009970802","dealeraddress":"北京市大兴区金时大街7号院10号楼","distance":1644,"reviewdealerscore":4,"reviewcount":31,"reviewdealerid":49464,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g3/M03/6F/3F/autohomedealer__ChsEm19Z8-aAblY2AAZYg3nm3_s825.jpg","dealerid":1900,"dealername":"北京中润发","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=1900&pvareaid=&channel=2","dealerlinktel":"4009683179","dealeraddress":"北京市丰台区菜户营东街乙168号","distance":1647.1,"reviewdealerscore":4.5,"reviewcount":120,"reviewdealerid":37156,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g30/M03/0D/36/autohomedealer__ChxknGK-nZaAHT3uAAIDOEp_v2Q197.jpg","dealerid":123482,"dealername":"安洋伟业西南五环","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=123482&pvareaid=&channel=2","dealerlinktel":"4009722920","dealeraddress":"北京市大兴区黄村镇狼垡芦花路临52号（狼垡东桥北200米）","distance":1637.7,"reviewdealerscore":4,"reviewcount":22,"reviewdealerid":48144,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g12/M0E/12/5C/autohomedealer__wKjBy1e-WVCAAzXIAAH1_FGZqHE060.jpg","dealerid":125587,"dealername":"北京百得利海淀奥迪中心","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=125587&pvareaid=&channel=2","dealerlinktel":"4009729101","dealeraddress":"北京市海淀区西四环定慧北桥向西20米路北","distance":1646.5,"reviewdealerscore":4,"reviewcount":32,"reviewdealerid":48974,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g26/M03/52/52/autohomedealer__ChcCP12IHEWAfFOHAARoXcZm6S8853.jpg","dealerid":103244,"dealername":"北京运通嘉奥","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=103244&pvareaid=&channel=2","dealerlinktel":"4009728836","dealeraddress":"京承高速11号出口（昌金路出口）向东500米，运通京承国际汽车广场内","distance":1681.4,"reviewdealerscore":3.5,"reviewcount":26,"reviewdealerid":46374,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g28/M02/E6/CD/autohomedealer__ChwFkmOMZ76AOUUmAAehHlsEM_A000.jpg","dealerid":127120,"dealername":"北京国服信奥众","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=127120&pvareaid=&channel=2","dealerlinktel":"4009971037","dealeraddress":"北京市丰台区南四环中路260号新发地汽车市场东区1号展厅","distance":1650.6,"reviewdealerscore":4,"reviewcount":33,"reviewdealerid":49458,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g26/M00/3A/77/autohomedealer__ChxkjmIIZTeAYLLoAAa-Q4Bsjl0441.jpg","dealerid":1905,"dealername":"北京寰宇恒通奥迪中心","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=1905&pvareaid=&channel=2","dealerlinktel":"4009684428","dealeraddress":"北京市海淀区西北旺百旺绿谷汽车园F2","distance":1653.6,"reviewdealerscore":4,"reviewcount":296,"reviewdealerid":37159,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g30/M03/99/FF/autohomedealer__ChxknGIUl36Ad31ZAAHwQ50KQfk527.jpg","dealerid":2099476,"dealername":"北京诚行万奥","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=2099476&pvareaid=&channel=2","dealerlinktel":"4001542024","dealeraddress":"北京市昌平区城南街道西关郝庄路105号","distance":1662.4,"reviewdealerscore":0,"reviewcount":0,"reviewdealerid":0,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g19/M0E/F7/32/autohomedealer__wKjBxFizudSAIiuvABa9wVUJ25I406.jpg","dealerid":313,"dealername":"百得利奥迪中心","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=313&pvareaid=&channel=2","dealerlinktel":"4009721733","dealeraddress":"北京市经济技术开发区东环北路1号","distance":1654.6,"reviewdealerscore":4,"reviewcount":40,"reviewdealerid":36674,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g26/M09/7B/62/autohomedealer__ChwFkGFs81aAaf0GAAWzx9_cWYk680.jpg","dealerid":10595,"dealername":"北京国服信奥兴","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=10595&pvareaid=&channel=2","dealerlinktel":"4009721182","dealeraddress":"北京市石景山区古城大街国际汽车贸易服务园区F区9号","distance":1639.5,"reviewdealerscore":4,"reviewcount":34,"reviewdealerid":40187,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""},{"dealerimage":"http://dealer2.autoimg.cn/dealerdfs/g25/M06/23/A0/autohomedealer__ChxkqWOv_cyAevnaAADtZh1EigU178.jpg","dealerid":68122,"dealername":"北京名尊奥翔奥迪中心","dealerurl":"https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=68122&pvareaid=&channel=2","dealerlinktel":"4009724921","dealeraddress":"北京市朝阳区王四营桥马房寺路331号（京沈高速五环内）","distance":1660.4,"reviewdealerscore":3.5,"reviewcount":34,"reviewdealerid":43013,"tags":[],"brands":["33"],"comboactivitylist":[],"choiceness":1,"zxdp":0,"zxdpurl":""}]
     */

    private int rowcount;
    private int pagecount;
    private int pageindex;
    private List<DealerInfoBean> list;
    private String jumpdealerlisturl;

    public int getRowcount() {
        return rowcount;
    }

    public void setRowcount(int rowcount) {
        this.rowcount = rowcount;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public int getPageindex() {
        return pageindex;
    }

    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }

    public List<DealerInfoBean> getList() {
        return list;
    }

    public void setList(List<DealerInfoBean> list) {
        this.list = list;
    }

    public String getJumpdealerlisturl() {
        return jumpdealerlisturl;
    }

    public void setJumpdealerlisturl(String jumpdealerlisturl) {
        this.jumpdealerlisturl = jumpdealerlisturl;
    }

    public static class DealerInfoBean {

        /**
         * dealerimage : http://dealer2.autoimg.cn/dealerdfs/g13/M04/48/91/autohomedealer__ChwEn2Db7YCAMAqmAADV13G7Htk413.jpg
         * dealerid : 2
         * dealername : 北京博瑞祥云奥迪
         * dealerurl : https://dealer4s.m.autohome.com.cn/dealer4S/home?dealerId=2&pvareaid=&channel=2
         * dealerlinktel : 4009720067
         * dealeraddress : 北京市朝阳区花虎沟2号京藏高速出京方向辅路清河收费站南50米路东
         * distance : 1658.2
         * reviewdealerscore : 4.5
         * reviewcount : 555
         * reviewdealerid : 36639
         * tags : []
         * brands : ["33"]
         * comboactivitylist : [{"id":28,"type":1,"title":"测试-车主服务","jumpurl":"http://dealer4s.m.yz.test.autohome.com.cn/benefitDetail?dealerId=2&benefitId=28&pvareaid=6858911&channel=2&channelCode=PALM_DEALER_4S","createdtime":"2023-04-06 13:42:03"}]
         * choiceness : 1
         * zxdp : 0
         * zxdpurl :
         */

        private String dealerimage;
        private int dealerid;
        private String dealername;
        private String dealerurl;
        private String dealerlinktel;
        private String dealeraddress;
        private double distance;
        private double reviewdealerscore;
        private int reviewcount;
        private int reviewdealerid;
        private int choiceness;
        private int zxdp;
        private String zxdpurl;
        private List<?> tags;
        private List<String> brands;
        private List<ComboactivitylistBean> comboactivitylist;
        private BigDecimal aftersalesscore;
        private List<TagItem> taglist = new ArrayList<>();
        private boolean isRepair = false;

        private CornerTagBean cornertag;

        private List<SmallTagsBean> smalltags;

        public CornerTagBean getCornertag() {
            return cornertag;
        }

        public void setCornertag(CornerTagBean cornertag) {
            this.cornertag = cornertag;
        }

        public List<SmallTagsBean> getSmalltags() {
            return smalltags;
        }

        public void setSmalltags(List<SmallTagsBean> smalltags) {
            this.smalltags = smalltags;
        }

        public String getDealerimage() {
            return dealerimage;
        }

        public void setDealerimage(String dealerimage) {
            this.dealerimage = dealerimage;
        }

        public int getDealerid() {
            return dealerid;
        }

        public void setDealerid(int dealerid) {
            this.dealerid = dealerid;
        }

        public String getDealername() {
            return dealername;
        }

        public void setDealername(String dealername) {
            this.dealername = dealername;
        }

        public String getDealerurl() {
            return dealerurl;
        }

        public void setDealerurl(String dealerurl) {
            this.dealerurl = dealerurl;
        }

        public String getDealerlinktel() {
            return dealerlinktel;
        }

        public void setDealerlinktel(String dealerlinktel) {
            this.dealerlinktel = dealerlinktel;
        }

        public String getDealeraddress() {
            return dealeraddress;
        }

        public void setDealeraddress(String dealeraddress) {
            this.dealeraddress = dealeraddress;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getReviewdealerscore() {
            return reviewdealerscore;
        }

        public void setReviewdealerscore(double reviewdealerscore) {
            this.reviewdealerscore = reviewdealerscore;
        }

        public int getReviewcount() {
            return reviewcount;
        }

        public void setReviewcount(int reviewcount) {
            this.reviewcount = reviewcount;
        }

        public int getReviewdealerid() {
            return reviewdealerid;
        }

        public void setReviewdealerid(int reviewdealerid) {
            this.reviewdealerid = reviewdealerid;
        }

        public int getChoiceness() {
            return choiceness;
        }

        public void setChoiceness(int choiceness) {
            this.choiceness = choiceness;
        }

        public int getZxdp() {
            return zxdp;
        }

        public void setZxdp(int zxdp) {
            this.zxdp = zxdp;
        }

        public String getZxdpurl() {
            return zxdpurl;
        }

        public void setZxdpurl(String zxdpurl) {
            this.zxdpurl = zxdpurl;
        }

        public List<?> getTags() {
            return tags;
        }

        public void setTags(List<?> tags) {
            this.tags = tags;
        }

        public List<String> getBrands() {
            return brands;
        }

        public void setBrands(List<String> brands) {
            this.brands = brands;
        }

        public List<ComboactivitylistBean> getComboactivitylist() {
            return comboactivitylist;
        }

        public void setComboactivitylist(List<ComboactivitylistBean> comboactivitylist) {
            this.comboactivitylist = comboactivitylist;
        }

        public BigDecimal getAftersalesscore() {
            return aftersalesscore;
        }

        public void setAftersalesscore(BigDecimal aftersalesscore) {
            this.aftersalesscore = aftersalesscore;
        }

        public List<TagItem> getTaglist() {
            return taglist;
        }

        public void setTaglist(List<TagItem> taglist) {
            this.taglist = taglist;
        }

        public boolean isRepair() {
            return isRepair;
        }

        public void setRepair(boolean repair) {
            isRepair = repair;
        }

        public static class ComboactivitylistBean {
            /**
             * id : 28
             * type : 1
             * title : 测试-车主服务
             * jumpurl : http://dealer4s.m.yz.test.autohome.com.cn/benefitDetail?dealerId=2&benefitId=28&pvareaid=6858911&channel=2&channelCode=PALM_DEALER_4S
             * createdtime : 2023-04-06 13:42:03
             */

            private int id;
            private int type;
            private String title;
            private String jumpurl;
            private String createdtime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getJumpurl() {
                return jumpurl;
            }

            public void setJumpurl(String jumpurl) {
                this.jumpurl = jumpurl;
            }

            public String getCreatedtime() {
                return createdtime;
            }

            public void setCreatedtime(String createdtime) {
                this.createdtime = createdtime;
            }
        }

        public static class TagItem {
            private String tag;

            private String coupon;

            private String linkurl;

            private String suffix;

            private String suffixcolor;

            public String getSuffix() {
                return suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }

            public String getSuffixcolor() {
                return suffixcolor;
            }

            public void setSuffixcolor(String suffixcolor) {
                this.suffixcolor = suffixcolor;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public String getCoupon() {
                return coupon;
            }

            public void setCoupon(String coupon) {
                this.coupon = coupon;
            }

            public String getLinkurl() {
                return linkurl;
            }

            public void setLinkurl(String linkurl) {
                this.linkurl = linkurl;
            }
        }
    }

    public static class CornerTagBean{
        private String imgurl;
        private Integer width;
        private Integer height;

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }
    public static class SmallTagsBean{
        private String text;
        private String bgcolor;
        private String textcolor;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getBgcolor() {
            return bgcolor;
        }

        public void setBgcolor(String bgcolor) {
            this.bgcolor = bgcolor;
        }

        public String getTextcolor() {
            return textcolor;
        }

        public void setTextcolor(String textcolor) {
            this.textcolor = textcolor;
        }
    }
}