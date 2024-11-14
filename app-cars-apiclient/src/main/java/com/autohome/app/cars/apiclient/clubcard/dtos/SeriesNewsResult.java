package com.autohome.app.cars.apiclient.clubcard.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/8/15 14:18
 */
@NoArgsConstructor
@Data
public class SeriesNewsResult {
    private Integer total;
    private List<ItemsDTO> items;
    private Boolean hasMore;
    private String searchAfter;

    @NoArgsConstructor
    @Data
    public static class ItemsDTO {
        private Integer cms_class2;
        private String author_name;
        private Integer pv;
        private String cms_tags;
        private String author_img;
        private Integer cms_refine;
        private String title;
        private Integer cms_kind;
        private Integer duration;
        private String cms_afirstappcoverimg;
        private String publish_time;
        private Object cms_app_img;
        private String cms_thirdappcoverimg;
        private Object video_source;
        private String pc_url;
        private Integer cms_class1;
        private String small_title;
        private String cms_firstappcoverimg;
        private Integer like_count;
        private String cms_athirdappcoverimg;
        private String main_data_type;
        private Long global_id;
        private String cms_asecondappcoverimg;
        private String img_url_4x3;
        private Integer reply_count;
        private String cms_secondappcoverimg;
        private Boolean is_close_comment;
        private Integer has_multi_images;
        private String m_url;
        private String biz_update_time;
        private Integer parent_biz_id;
        private String multi_images;
        private String img_url;
        private List<Integer> cms_tags_ids;
        private Integer cms_createsource;
        private List<Integer> series_ids;
        private Integer author_id;
        private Integer biz_id;
        private Integer vv;
        private Integer video_height;
        private Integer video_width;
        private String video_vertical_img_url;
        private String video_app_title;
        private Integer video_direction;
        private String video_vertical_jb_img_url;
        private Integer video_type;
    }
}
