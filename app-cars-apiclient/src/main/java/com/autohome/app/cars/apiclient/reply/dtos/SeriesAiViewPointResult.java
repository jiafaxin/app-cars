package com.autohome.app.cars.apiclient.reply.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 车系车型观点
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeriesAiViewPointResult {
    private static final long serialVersionUID = -101754286978984265L;

    private Integer seriesId;
    private Integer specId;
    private WeekReportDto weekReport;
    private HotTopicDto hotTopic;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeekReportDto implements Serializable {
        private static final long serialVersionUID = 460487404695350072L;

        private Integer vpWeekReportId;//datatype=1的记录的主键id，用于给周总结观点点赞用
        private Integer likeCount;//点赞数、观点支持数
        private Boolean isLiked;
        ;//是否已点赞
        private String headerTxtCenter;//"近一周用户观点AI提炼"
        //private String headerTxtRight;//"xxx人认可"
        private String summary;
        private Integer summaryMemberCount;
        private List<VpItemDto> items;
        private String appUrl;//跳转协议
        private String header;//页眉（左上角标题）
        private String footer;//页脚（左下角标题）
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VpItemDto implements Serializable {
        private static final long serialVersionUID = -6436375374905404262L;

        private String itemName;//“空间”
        private String itemViewPoint;//“大大大大大”
        private Integer itemType;//分项观点倾向性：1-正向观点，2-负向观点
        private Integer itemMemberCount;//分项观点的样本源参考人数
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HotTopicDto implements Serializable {
        private static final long serialVersionUID = -2731057695393118939L;

        private String headerTxtCenter;//"近一天用户最多表达"
        private String headerTxtRight;//"15:00更新"
        private List<VpTopicDto> hotTopics;
        private String header;//页眉（左上角标题）
        private String footer;//页脚（左下角标题）

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VpTopicDto implements Serializable {
        private static final long serialVersionUID = 6938890193827848601L;

        private String topicIcon;//“http icon”
        private String topicName;//“雷军从雷子变雷神”
        private Integer topicHotValue;//“888888”
        private String topicHotValueTxt;//“123车友讨论”
        private String appUrl;//跳转协议
    }
}
