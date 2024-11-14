package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 车系图片内容
 */
@Data
public class SeriesPicDto {

    private int seriesId;

    private String seriesName;

    private List<Item> items;

    private int autoShowPicCount;

    @Data
    public static class Item{
        private int id;

        private String name;

        private int count;

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
