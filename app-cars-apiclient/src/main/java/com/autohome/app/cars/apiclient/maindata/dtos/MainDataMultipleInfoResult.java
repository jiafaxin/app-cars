package com.autohome.app.cars.apiclient.maindata.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class MainDataMultipleInfoResult {
        private String author_name;
        private long vv;
        private long pv;
        private String author_img;
        private String img_url_16x9;
        private int cms_refine;
        private String title;
        private int cms_kind;
        private long duration;
        private String publish_time;
        private String pc_url;
        private String small_title;
        private String app_url;
        private String main_data_type;
        private String img_url_4x3;
        private int reply_count;
        private int is_close_comment;
        private String m_url;
        private String biz_update_time;
        private int parent_biz_id;
        private List<String> multi_images;
        private String img_url;
        private int cms_createsource;
        private int biz_id;
        private int author_id;
        private String video_source;
}
