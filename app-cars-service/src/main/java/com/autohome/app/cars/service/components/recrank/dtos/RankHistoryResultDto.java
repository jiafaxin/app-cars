package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RankHistoryResultDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 5541237247109384809L;

    private int returncode = 0;
    private String message = "";
    private SaleSeriesRankHistoryResult result;

    @Data
    public static class SaleSeriesRankHistoryResult {
        private int seriesid;
        private int max;
        private int chartcolnum;
        private List<SaleCountItem> salecount = new ArrayList<>();
        private List<MonthItem> month = new ArrayList<>();
        private List<MonthSaleItem> monthsale = new ArrayList<>();

        @Data
        @AllArgsConstructor
        public static class SaleCountItem {
            private int count;
            private String name;
            private Info info;

            public static SaleCountItem getInstance(int count,Info info) {
                return new SaleCountItem(count, count + "辆",info);
            }

            @Data
            public static class Info{
                private String title;
                private int count;
                private String text;
                private int comparenum;
                private int comparetype;// 上升1，下降-1，持平0
                private String rank;
            }

        }
        @Data
        @AllArgsConstructor
        public static class MonthItem {
            String name;
            String subname;

            public static MonthItem getInstance(String name, String subName) {
                return new MonthItem(name, subName);
            }
        }

        @Data
        @AllArgsConstructor
        public static class MonthSaleItem {
            int type;
            String name;
            String value;
            String ranktext;

            public static MonthSaleItem getInstance(int type, String name, String value,String ranktext) {
                return new MonthSaleItem(type, name, value,ranktext);
            }
        }

    }

}
