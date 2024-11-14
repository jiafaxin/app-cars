package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/7
 */
@Data
public class SpecPicInfoDto {

    private String linkUrl;
    private int specOrigPicCount;
    private String picCount;
    private String picIcon;
    private String tip = "";
    private List<PicListDto> picList = new ArrayList<>();

    @Data
    public static class PicListDto {
        private String icon;
        private Integer isOutVr;
        private String linkUrl;
        private String tag;
        private int picCount;
        private List<PicItem> picItems = new ArrayList<>();
    }

    @Data
    public static class PicItem {

        public PicItem() {

        }

        public PicItem(int id, String url, int specId) {
            this.id = id;
            this.url = url;
            this.specId = specId;
        }

        private int id;
        private String url;
        private int specId;
    }
}
