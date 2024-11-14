package com.autohome.app.cars.apiclient.cms.dtos;

import com.autohome.app.cars.apiclient.maindata.dtos.SFastNewsParagraphItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 车展资讯数据
 */
@NoArgsConstructor
@Data
public class AutoShowNewsResult {

    private List<AutoShowItem> items = new ArrayList<>();

    @NoArgsConstructor
    @Data
    public static class AutoShowItem {

        private int id;
        private int bizId;
        private String mainDataType;
        private String mainDataJson;
        private Object mainDataInfo;
        private MainDataAllDTO mainDataAll;
        private Object pageId;
        private int orderNum;

        @NoArgsConstructor
        @Data
        public static class MainDataAllDTO {
            private String author_name;
            private int vv;
            private int pv;
            private String author_img;
            private List<String> img_list;
            private String img_url_16x9;
            private int cms_refine;
            private String title;
            private int pool_biz_type;
            private int cms_kind;
            private int duration;
            private String publish_time;
            private String pc_url;
            private String small_title;
            private String app_url;
            private String main_data_type;
            private String img_url_4x3;
            private String label;
            private int reply_count;
            private Boolean is_close_comment;
            private String m_url;
            private String biz_update_time;
            private int parent_biz_id;
            private String multi_images;
            private String img_url;
            private int cms_createsource;
            private int biz_id;
            private int author_id;
            private List<Integer> series_ids;
            private List<SFastNewsParagraphItem> cms_passage_list;
        }
    }
}
