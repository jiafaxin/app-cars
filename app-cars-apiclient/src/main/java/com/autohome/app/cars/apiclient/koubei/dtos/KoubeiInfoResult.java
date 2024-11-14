package com.autohome.app.cars.apiclient.koubei.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author : zzli
 * @description : 口碑数据
 * @date : 2024/2/21 19:38
 */
@Data
public class KoubeiInfoResult {

    private List<KouBeiInfoDto> list;
    private Integer pageIndex;
    private Integer pagecount;
    private Integer rowcount;

    @Data
    public static class KouBeiInfoDto {
        private int actual_battery_consumption;
        private double actual_oil_consumption;
        private int adminStatus;
        private int append_count;
        private int apperance;
        private String best;
        private int bigV;
        private String bigVCoverUrl;
        private int bigVPlayCount;
        private int bigVPlayId;
        private String bigVVideoId;
        private String boughtCityName;
        private int bought_city;
        private String bought_date;
        private int bought_place;
        private int bought_province;
        private int carOwnerLevels;
        private String carOwnershipPeriod;
        private int comfortableness;
        private int commentCount;
        private int consumption;
        private int cost_efficient;
        private String created;
        private boolean deleteShow;
        private int driven_kilometers;
        private int drivingSkills;
        private String feeling;
        private String feeling_summary;
        private String headImage;
        private int helpLV;
        private int helpfulCount;
        private int id;
        private int interior;
        private int isAuthSeries;
        private int isAuthenticated;
        private int koubeiStatus;
        private int last_append_driven_kilometers;
        private String last_edit;
        private int maneuverability;
        private int medal;
        private int newEnergyEval;
        private String newEnergyImgBack;
        private int newEnergyUser;
        private String nickName;
        private int power;
        private int powerType;
        private int power_driven_type;
        private int price;
        private int recommend;
        private int regionId;
        private String regionProvince;
        private int routineCruisingRange;
        private int row_status;
        private int seriesId;
        private String seriesName;
        private String showId;
        private int showPublishEval;
        private int showSignUp;
        private int space;
        private String specName;
        private int specid;
        private int userBigVLevel;
        private int userid;
        private int visitCount;
        private int winterCruisingRange;
        private String worst;
        private List<?> append_list;
        private List<?> koubeiVideoList;
        private List<MultiImagesBean> multiImages;
        private List<String> photos;
        private List<?> purposes;
        private List<?> topics;
        private Integer typeid;
        /**
         * 1：车主口碑
         * 6：探店口碑
         */
        private int koubeiType;

        public static class MultiImagesBean {
            /**
             * appendingId : 0
             * coverType : 1
             * during : 0
             * duringString :
             * id : 6350593
             * img : https://k2.autoimg.cn/koubeidfs/g24/M01/73/0E/480x360_0_q87_autohomecar__ChwFjmC4OXmAawKxAAJwAufKZgE035.jpg
             * is_del : 0
             * koubeiId : 3668161
             * photoId : 20355905
             * userSelected : 0
             * videoId : 0
             */

            private int appendingId;
            private int coverType;
            private int during;
            private String duringString;
            private int id;
            private String img;
            private int is_del;
            private int koubeiId;
            private int photoId;
            private int userSelected;
            private int videoId;

            public int getAppendingId() {
                return appendingId;
            }

            public void setAppendingId(int appendingId) {
                this.appendingId = appendingId;
            }

            public int getCoverType() {
                return coverType;
            }

            public void setCoverType(int coverType) {
                this.coverType = coverType;
            }

            public int getDuring() {
                return during;
            }

            public void setDuring(int during) {
                this.during = during;
            }

            public String getDuringString() {
                return duringString;
            }

            public void setDuringString(String duringString) {
                this.duringString = duringString;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public int getIs_del() {
                return is_del;
            }

            public void setIs_del(int is_del) {
                this.is_del = is_del;
            }

            public int getKoubeiId() {
                return koubeiId;
            }

            public void setKoubeiId(int koubeiId) {
                this.koubeiId = koubeiId;
            }

            public int getPhotoId() {
                return photoId;
            }

            public void setPhotoId(int photoId) {
                this.photoId = photoId;
            }

            public int getUserSelected() {
                return userSelected;
            }

            public void setUserSelected(int userSelected) {
                this.userSelected = userSelected;
            }

            public int getVideoId() {
                return videoId;
            }

            public void setVideoId(int videoId) {
                this.videoId = videoId;
            }
        }
    }
}
