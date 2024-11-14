package com.autohome.app.cars.apiclient.rank.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dx on 2024/6/3
 * 降价榜接口实体对象
 */
@NoArgsConstructor
@Data
public class DiscountRankResult {
    private int returncode = 0;
    private String message = "";
    private DiscountRankInfo result;

    @NoArgsConstructor
    @Data
    public static class DiscountRankInfo {
        private int pageindex;
        private int pagecount;
        private int pagesize;
        private String saleranktip;
        private String morescheme = "";
        private String scenetitle;
        private String scenesubtitle;
        private ShareInfoDto shareinfo = new ShareInfoDto();
        private List<Item> list = new ArrayList<>();
    }

    @NoArgsConstructor
    @Data
    public static class Item {
        private int rank_num;
        private int series_id;
        private String series_name;
        private String series_image;
        private int min_price;
        private int max_price;
        private Float praise_score;
        private int spec_id;
        private String spec_name;
        private int spec_price;
        private int spec_min_price;
        private int spec_max_price;
    }

    @NoArgsConstructor
    @Data
    public static class ShareInfoDto {
        private String subtitle = "";
        private String logo = "";
        private String title = "";
        private String url = "";
    }
}
