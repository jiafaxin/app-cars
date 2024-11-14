package com.autohome.app.cars.service.services.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 车系页新车日历配置
 * @date : 2024/4/28 20:46
 */
@NoArgsConstructor
@Data
public class NewCarCalendarConfig {

    private int fuel_isopen;
    private int newenergy_isopen;
    private List<Integer> blacklist = new ArrayList<>();
    /**
     * 上市前留资按钮文案配置
     */
    private String btntext_10;
    /**
     * 上市后留资按钮文案配置
     */
    private String btntext_20;
    private String bottomtip;
    private int show_limit;
    private int show_range;
    private int show_range_v2;
    private SeriesListDTO comm_series;
    private List<SeriesListDTO> series_list = new ArrayList<>();
    private List<EntryContent2DTO> entryContent2 = new ArrayList<>();

    private SeriesListDTO_V2 comm_series_v2;
    private List<SeriesListDTO_V2> series_list_v2 = new ArrayList<>();
    private List<EntryContent2DTO_V2> entryContent2_v2 = new ArrayList<>();
    /**
     * 资讯-车系评级黑名单
     */
    private List<String> blackeventlevel = new ArrayList<>();

    /**
     * 可按类型配置非新车系入口的显示天数
     */
    private List<EntranceDayNum> nonnewcarentrance = new ArrayList<>();
    /**
     * 车系资讯标签配置
     */
    private List<SeriesTag> seriestag = new ArrayList<>();

    @NoArgsConstructor
    @Data
    public static class EntranceDayNum {
        //上市前xxx天
        private int beforedaynum;
        //上市后xxx天
        private int afterdaynum;
        /**
         * 支持分能源类型（纯电、非纯电、油车）、车系和分车系级别配置（S\A\其他）。
         */
        private List<String> type = new ArrayList<>();
    }

    @NoArgsConstructor
    @Data
    public static class SeriesListDTO {
        private int seriesid;
        private List<NodelistDTO> nodelist;

        @NoArgsConstructor
        @Data
        public static class NodelistDTO {
            private String nodename;
            private String subtitle;
            private Integer code;
            private String datetime;
            private String linkurl;
        }
    }

    @NoArgsConstructor
    @Data
    public static class EntryContent2DTO {
        private String id;
        private String title;
        private String subtitle;
        private String btn;
        private String title2;
        private String subtitle2;
        //private String btn2;
    }

    @NoArgsConstructor
    @Data
    public static class SeriesListDTO_V2 {
        private int seriesid;
        private List<Node_V2> nodelist;

        @NoArgsConstructor
        @Data
        public static class Node_V2 {
            private Integer code;
            private String title;
            // 副标题配置项默认为空，由业务代码处理，若填写则会取代代码中的处理逻辑
            private String subtitle;
            // 打底的副标题
            private String backupsubtitle;
            // 节点时间，仅在单车系的配置中能够生效
            private String datetime;
            // 自定义节点的内容，内容为图文样式卡片，仅当code为900~999之间生效
            private CustomNodeDetail detail;
        }

        @NoArgsConstructor
        @Data
        public static class CustomNodeDetail {
            private String title;
            private String image;
            private String linkurl;
            private String videotime;
        }
    }

    @NoArgsConstructor
    @Data
    public static class EntryContent2DTO_V2 {
        private int seriesid;
        // 未订阅未上市
        private Detail subscribe0list0;
        // 未订阅已上市
        private Detail subscribe0list1;
        // 已订阅未上市
        private Detail subscribe1list0;
        // 已订阅已上市
        private Detail subscribe1list1;

        @NoArgsConstructor
        @Data
        public static class Detail {
            private String title;
            private String subtitle;
            private String btn;
        }
    }

    @NoArgsConstructor
    @Data
    public static class SeriesTag {
        private Integer tagid;
        private String tagname;
    }

}
