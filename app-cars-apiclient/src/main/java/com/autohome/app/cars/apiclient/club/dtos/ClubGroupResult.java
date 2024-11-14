package com.autohome.app.cars.apiclient.club.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@Data
public class ClubGroupResult {
    /**
     * bbs : c
     * bbsId : 197
     * bbsName : 奔驰E级论坛
     * tagList : [{"tagId":622,"tagName":"提车作业小组","allTopicCount":269,"allReplyCount":22034,"hourTopicReplyCount":22038},{"tagId":623,"tagName":"用车问题小组","allTopicCount":2228,"allReplyCount":17962,"hourTopicReplyCount":17983},{"tagId":626,"tagName":"闲置交易小组","allTopicCount":1860,"allReplyCount":16728,"hourTopicReplyCount":16748},{"tagId":620,"tagName":"价格讨论小组","allTopicCount":624,"allReplyCount":9980,"hourTopicReplyCount":9983},{"tagId":625,"tagName":"改装升级小组","allTopicCount":612,"allReplyCount":9220,"hourTopicReplyCount":9223},{"tagId":621,"tagName":"帮选车小组","allTopicCount":318,"allReplyCount":4291,"hourTopicReplyCount":4295},{"tagId":624,"tagName":"续航讨论小组","allTopicCount":2,"allReplyCount":6,"hourTopicReplyCount":6}]
     */
    private String bbs;
    private int bbsId;
    private String bbsName;
    private List<TagListBean> tagList;

    @Data
    public static class TagListBean {
        /**
         * tagId : 622
         * tagName : 提车作业小组
         * allTopicCount : 269
         * allReplyCount : 22034
         * hourTopicReplyCount : 22038
         */
        private int tagId;
        private String tagName;
        private int allTopicCount;
        private int allReplyCount;
        private int hourTopicReplyCount;
    }

}
