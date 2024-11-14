package com.autohome.app.cars.apiclient.che168.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class GetRecommendCarResult {

    private int carcount;
    private int pagecount;

    private List<SUsedCarItem> cars = new ArrayList<>();
    private String moreurl;
    private String replacetitle;
    private String replaceurl;
    private String pricerange;

//    private String pvareaid;
//
//    private String bpvareaid;
//
//    public String getBpvareaid() {
//        return bpvareaid;
//    }
//
//    public void setBpvareaid(String bpvareaid) {
//        this.bpvareaid = bpvareaid;
//    }
//
//    public String getPvareaid() {
//        return pvareaid;
//    }
//
//    public void setPvareaid(String pvareaid) {
//        this.pvareaid = pvareaid;
//    }

    public int getCarcount() {
        return carcount;
    }

    public void setCarcount(int carcount) {
        this.carcount = carcount;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public List<SUsedCarItem> getCars() {
        return cars;
    }

    public void setCars(List<SUsedCarItem> cars) {
        this.cars = cars;
    }

    public String getMoreurl() {
        return moreurl;
    }

    public void setMoreurl(String moreurl) {
        this.moreurl = moreurl;
    }

    public String getReplacetitle() {
        return replacetitle;
    }

    public void setReplacetitle(String replacetitle) {
        this.replacetitle = replacetitle;
    }

    public String getReplaceurl() {
        return replaceurl;
    }

    public void setReplaceurl(String replaceurl) {
        this.replaceurl = replaceurl;
    }

    public String getPricerange() {
        return pricerange;
    }

    public void setPricerange(String pricerange) {
        this.pricerange = pricerange;
    }

    public static class SUsedCarItem {
        private Integer infoid;
        private String carname;
        private String mileage;
        private String registeyear;
        private String dealerlevel;
        private String price;
        private String saveprice;
        private List<String> tags = new ArrayList<>();
        private String detailurl;
        private String imageurl;
        private String replacetitle;
        private String cstencryptinfo;

//        private  String pvareaid;
//
//        public String getPvareaid() {
//            return pvareaid;
//        }
//
//        public void setPvareaid(String pvareaid) {
//            this.pvareaid = pvareaid;
//        }

        public Integer getInfoid() {
            return infoid;
        }

        public void setInfoid(Integer infoid) {
            this.infoid = infoid;
        }

        public String getCarname() {
            return carname;
        }

        public void setCarname(String carname) {
            this.carname = carname;
        }

        public String getMileage() {
            return mileage;
        }

        public void setMileage(String mileage) {
            this.mileage = mileage;
        }

        public String getRegisteyear() {
            return registeyear;
        }

        public void setRegisteyear(String registeyear) {
            this.registeyear = registeyear;
        }

        public String getDealerlevel() {
            return dealerlevel;
        }

        public void setDealerlevel(String dealerlevel) {
            this.dealerlevel = dealerlevel;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getSaveprice() {
            return saveprice;
        }

        public void setSaveprice(String saveprice) {
            this.saveprice = saveprice;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getDetailurl() {
            return detailurl;
        }

        public void setDetailurl(String detailurl) {
            this.detailurl = detailurl;
        }

        public String getImageurl() {
            return imageurl;
        }

        public void setImageurl(String imageurl) {
            this.imageurl = imageurl;
        }

        public String getReplacetitle() {
            return replacetitle;
        }

        public void setReplacetitle(String replacetitle) {
            this.replacetitle = replacetitle;
        }

        public String getCstencryptinfo() {
            return cstencryptinfo;
        }

        public void setCstencryptinfo(String cstencryptinfo) {
            this.cstencryptinfo = cstencryptinfo;
        }
    }
}
