package com.autohome.app.cars.apiclient.dealer.dtos;

import java.util.List;

public class RepairFactoryResult {

    private Integer cshDealerId;

    private String dealerShortName;

    private String appSellPhone;

    private Number distance;

    private Number mapLonBaidu;

    private Number mapLatBaidu;

    private String address;

    private String dealerImg;

    private String landingPageUrl;

    private List<Activities> activities;

    public Integer getCshDealerId() {
        return cshDealerId;
    }

    public void setCshDealerId(Integer cshDealerId) {
        this.cshDealerId = cshDealerId;
    }

    public String getDealerShortName() {
        return dealerShortName;
    }

    public void setDealerShortName(String dealerShortName) {
        this.dealerShortName = dealerShortName;
    }

    public String getAppSellPhone() {
        return appSellPhone;
    }

    public void setAppSellPhone(String appSellPhone) {
        this.appSellPhone = appSellPhone;
    }

    public Number getDistance() {
        return distance;
    }

    public void setDistance(Number distance) {
        this.distance = distance;
    }

    public Number getMapLonBaidu() {
        return mapLonBaidu;
    }

    public void setMapLonBaidu(Number mapLonBaidu) {
        this.mapLonBaidu = mapLonBaidu;
    }

    public Number getMapLatBaidu() {
        return mapLatBaidu;
    }

    public void setMapLatBaidu(Number mapLatBaidu) {
        this.mapLatBaidu = mapLatBaidu;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDealerImg() {
        return dealerImg;
    }

    public void setDealerImg(String dealerImg) {
        this.dealerImg = dealerImg;
    }

    public String getLandingPageUrl() {
        return landingPageUrl;
    }

    public void setLandingPageUrl(String landingPageUrl) {
        this.landingPageUrl = landingPageUrl;
    }

    public List<Activities> getActivities() {
        return activities;
    }

    public void setActivities(List<Activities> activities) {
        this.activities = activities;
    }

    public static class Activities {
        private String id;

        private Integer cshDealerId;

        private Integer activityCategoryId;

        private String activityCategoryName;

        private String title;

        private String headerImage;

        private String landingPageUrl;

        public String getActivityCategoryName() {
            return activityCategoryName;
        }

        public void setActivityCategoryName(String activityCategoryName) {
            this.activityCategoryName = activityCategoryName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getCshDealerId() {
            return cshDealerId;
        }

        public void setCshDealerId(Integer cshDealerId) {
            this.cshDealerId = cshDealerId;
        }

        public Integer getActivityCategoryId() {
            return activityCategoryId;
        }

        public void setActivityCategoryId(Integer activityCategoryId) {
            this.activityCategoryId = activityCategoryId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHeaderImage() {
            return headerImage;
        }

        public void setHeaderImage(String headerImage) {
            this.headerImage = headerImage;
        }

        public String getLandingPageUrl() {
            return landingPageUrl;
        }

        public void setLandingPageUrl(String landingPageUrl) {
            this.landingPageUrl = landingPageUrl;
        }
    }
}
