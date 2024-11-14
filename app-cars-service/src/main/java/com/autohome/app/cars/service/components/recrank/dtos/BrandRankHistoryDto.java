package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 品牌销量趋势
 * @date : 2024/10/30 16:39
 */
@Data
public class BrandRankHistoryDto {

    private int brandid;
    private String brandname;
    private List<SaleCountItem> salecount = new ArrayList<>();
    private List<MonthItem> month = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class SaleCountItem {
        private Long count;
        private String name;
        private SaleCountItem.Info info;

        public SaleCountItem(Long count, SaleCountItem.Info info) {
            this.count = count;
            this.info = info;
            this.name = count + "辆";
        }

        @Data
        public static class Info {
            private String title;
            private Long count;
            private String text;
            private Long comparenum;
            private int comparetype;// 上升1，下降-1，持平0
            private String rank;
        }

    }

    @Data
    @NoArgsConstructor
    public static class MonthItem {
        String name;
        String subname;

        public MonthItem(String name, String subName) {
            this.name = name;
            this.subname = subName;
        }
    }
}
