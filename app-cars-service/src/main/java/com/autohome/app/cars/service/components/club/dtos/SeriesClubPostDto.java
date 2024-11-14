package com.autohome.app.cars.service.components.club.dtos;

import lombok.Data;

@Data
public class SeriesClubPostDto {
    private int seriesId;

    private SeriesClubPostBean post;
    @Data
    public static class SeriesClubPostBean{
        private Number topicId;//帖子id
        private String title = "";//帖子标题
        private String scheme = "";//跳转协议
        private String pic = "";//图片
        private String publishTime = "";//发布时间
        private String listScheme = "";//帖子列表跳转协议
    }
}
