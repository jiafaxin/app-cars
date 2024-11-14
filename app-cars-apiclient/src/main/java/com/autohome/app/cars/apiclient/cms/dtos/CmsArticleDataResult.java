package com.autohome.app.cars.apiclient.cms.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : zzli
 * @description : 资讯文章
 * @date : 2024/6/11 12:54
 */
@NoArgsConstructor
@Data
public class CmsArticleDataResult {

    private Integer totalcount;
    private List<ItemsDTO> items;
    private Boolean havemore;
    private Object pageid;

    @NoArgsConstructor
    @Data
    public static class ItemsDTO {
        private Integer id;
        private Integer bizId;
        private MainDataAllDTO mainDataAll;

        @NoArgsConstructor
        @Data
        public static class MainDataAllDTO {
            private Integer cms_PartType;
            private Integer cms_class2;
            private String cms_content_class;
            private Integer cms_editor_id;
            private Integer cms_city_type;
            private String cms_editor_name;
            private String img_url_16x9;
            private Integer cms_refine;
            private String title;
            private Integer cms_kind;
            private String cms_content_class2;
            private String publish_time;
            private String cms_dir;
            private Integer is_publish;
            private String small_title;
            private Integer cms_class1;
            private String summary;
            private String main_data_type;
            private String img_url_4x3;
            private Integer cms_auto_show_classid;
            private Integer is_delete;
            private Integer is_close_comment;
            private Integer has_multi_images;
            private String biz_update_time;
            private List<String> multi_images;
            private String img_url;
            private List<Integer> cms_tags_ids;
            private Integer cms_createsource;
            private Integer biz_id;
            private List<Integer> series_ids;
            private Integer author_id;
            private String app_url;
            private Integer duration;
            private String video_app_title;
        }
    }
}
