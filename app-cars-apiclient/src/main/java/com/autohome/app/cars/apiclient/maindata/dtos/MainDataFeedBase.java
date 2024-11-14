package com.autohome.app.cars.apiclient.maindata.dtos;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 车系综述资讯-内容主数据
 * Wiki： http://wiki.corpautohome.com/display/zixun/queryContentsBySeries
 */
@Data
public class MainDataFeedBase {
    private String appversion;
    private String author_name;
    private long pv;
    private long vv;
    private String main_data_type;
    private String img_url_16x9;
    private String img_url_4x3;
    private int reply_count;
    private String title;
    private boolean is_close_comment;
    private String biz_update_time;
    private int parent_biz_id;
    private String img_url;
    private String publish_time;
    private int biz_id;
    private String small_title;
    private int cms_refine;
    private int cms_kind;
    private long duration;
    private String video_source;
    private List<SFastNewsParagraphItem> cms_passage_list;
    private int hot_event;
    /**
     * cms_createsource!=null 并且 cms_createsource==2为快讯
     */
    private Integer cms_createsource;
    private int videoflag;
}
