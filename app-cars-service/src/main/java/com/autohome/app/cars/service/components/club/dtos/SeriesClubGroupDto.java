package com.autohome.app.cars.service.components.club.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@Data
public class SeriesClubGroupDto {

    private String bbs;
    private int bbsId;
    private String bbsName;
    private List<TagListBean> tagList;

    @Data
    public static class TagListBean {
        private int tagId;
        private String tagName;
        private int allTopicCount;
        private int allReplyCount;
        private int hourTopicReplyCount;
    }

}
