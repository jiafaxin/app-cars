package com.autohome.app.cars.apiclient.dealer.dtos;

import java.util.List;

public class DealerSeriesCanAskPriceResult {

    private int dealerId;
    private int seriesId;
    private int specId;
    private int newsPrice;
    private int saleType;
    private long endTime;
    private int cityId;
    private List<LBSAskPrice_MallInfo> mallEntrance;
    private BuyCarSubSubsidy seriesMainButton;
    private List<BuyCarSubSubsidy> seriesMainButtonList;

    private int isLowCity;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getIsLowCity() {
        return isLowCity;
    }

    public void setIsLowCity(int isLowCity) {
        this.isLowCity = isLowCity;
    }

    public List<BuyCarSubSubsidy> getSeriesMainButtonList() {
        return seriesMainButtonList;
    }

    public void setSeriesMainButtonList(List<BuyCarSubSubsidy> seriesMainButtonList) {
        this.seriesMainButtonList = seriesMainButtonList;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public BuyCarSubSubsidy getSeriesMainButton() {
        return seriesMainButton;
    }

    public void setSeriesMainButton(BuyCarSubSubsidy seriesMainButton) {
        this.seriesMainButton = seriesMainButton;
    }

    public List<LBSAskPrice_MallInfo> getMallEntrance() {
        return mallEntrance;
    }

    public void setMallEntrance(List<LBSAskPrice_MallInfo> mallEntrance) {
        this.mallEntrance = mallEntrance;
    }

    public int getDealerId() {
        return dealerId;
    }

    public void setDealerId(int dealerId) {
        this.dealerId = dealerId;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public int getSpecId() {
        return specId;
    }

    public void setSpecId(int specId) {
        this.specId = specId;
    }

    public int getNewsPrice() {
        return newsPrice;
    }

    public void setNewsPrice(int newsPrice) {
        this.newsPrice = newsPrice;
    }

    public int getSaleType() {
        return saleType;
    }

    public void setSaleType(int saleType) {
        this.saleType = saleType;
    }

    public static class BuyCarSubSubsidy {
        private String mainText;
        private String subText;
        private int type;
        private int biztype;
        private String amount;
        private String cornerText;
        private int abTag;

        public int getAbTag() {
            return abTag;
        }

        public void setAbTag(int abTag) {
            this.abTag = abTag;
        }

        public String getCornerText() {
            return cornerText;
        }

        public void setCornerText(String cornerText) {
            this.cornerText = cornerText;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public int getBiztype() {
            return biztype;
        }

        public void setBiztype(int biztype) {
            this.biztype = biztype;
        }

        public String getMainText() {
            return mainText;
        }

        public void setMainText(String mainText) {
            this.mainText = mainText;
        }

        public String getSubText() {
            return subText;
        }

        public void setSubText(String subText) {
            this.subText = subText;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }


    }

    public static class LBSAskPrice_MallInfo {
        private int specId;
        private String labelTxt;
        private String position;
        private String url;

        public int getSpecId() {
            return specId;
        }

        public void setSpecId(int specId) {
            this.specId = specId;
        }

        public String getLabelTxt() {
            return labelTxt;
        }

        public void setLabelTxt(String labelTxt) {
            this.labelTxt = labelTxt;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}
