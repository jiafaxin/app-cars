package com.autohome.app.cars.service.services.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wbs
 * @date 2024/2/28
 */
@Data
public class SubscribePopContent {
    private EntryTextConfig entryTextConfig;
    private List<EntryContent> entryContent = new ArrayList<>();
    private List<Layer1> layer1 = new ArrayList<>();
    private List<Layer2> layer2 = new ArrayList<>();
    private Agreement agreement;
    private Agreement agreementalert;
    private List<EntryEid> entryEid = new ArrayList<>();
    /**
     * 车系黑名单
     */
    private List<Integer> seriesblackList = new ArrayList<>();
    /**
     * 指定关闭某种订阅
     */
    private List<Integer> closeBizId = new ArrayList<>();

    @Data
    public static class EntryTextConfig {
        private List<Integer> S = new ArrayList<>();
        private List<Integer> M = new ArrayList<>();
        private List<Integer> L = new ArrayList<>();
    }

    @Data
    public static class EntryText {
        private String textType;
        private String title;
        private String subTitle;
    }

    @Data
    public static class Content {
        private int bizId;
        private String subscribeName;
        private List<EntryText> entryText = new ArrayList<>();
    }

    @Data
    public static class EntryContent {
        private String id;
        private String successContent;
        private List<Content> content = new ArrayList<>();
    }

    @Data
    public static class Row {
        private String title;
        private String highlighttitle;
        private String highlightcolor;
        private String subtitle;
        private String highsubtitlecolor;
        private String highsubtitle;
        private int ison;
        private int type;
    }

    @Data
    public static class EntryEid {
        private int sourceId;
        private String pm1;
        private String pm2;
    }

    @Data
    public static class Layer1 {
        private List<Integer> bizIds;
        private List<Row> rows = new ArrayList<>();
    }

    @Data
    public static class Layer2 {
        private List<Integer> bizIds = new ArrayList<>();
        private int navstyle;
        private String title;
        private String subtitle;
        private String poptitle;
        private String popdealertitle;
        private String highpoptitle;
        private String highpoptitlecolor;
    }

    @Data
    @NoArgsConstructor
    public static class Agreement {
        private String tips = "";
        private String extendstr = "";
        private List<ListItem> list = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    public static class ListItem {
        public String head = "";
        public String title = "";
        public String url = "";
    }
}
