package com.autohome.app.cars.apiclient.maindata.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 车系综述资讯-内容主数据
 * Wiki： http://wiki.corpautohome.com/display/zixun/queryContentsBySeries
 */
@Data
public class MainDataSeriesSummaryFeedsResult {
    private boolean has_more;
    private String search_after;
    private boolean is_close_comment;
    private List<MainDataSeriesSummaryFeed> items = new ArrayList<>();

    @Data
    public static class MainDataSeriesSummaryFeed extends MainDataFeedBase {
        private String multi_images;
    }
}
