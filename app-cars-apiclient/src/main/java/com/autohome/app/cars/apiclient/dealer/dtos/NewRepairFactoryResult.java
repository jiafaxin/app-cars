package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 接口wiki：http://mock.corpautohome.com/project/441/interface/case/939
 */
@NoArgsConstructor
@Data
public class NewRepairFactoryResult {
    private Integer cshDealerId;//车商汇商家ID

    private String dealerShortName;//商家自定义简称

    private String address;//商家地址

    private String sellPhone;//商家电话

    private String dealerImg;//商家图片

    private String defaultHeaderImg;//默认头图

    private String saleStartTime;//商家营业开始时间

    private String saleEndTime;//商家营业结束时间

    private Integer saleStatus;//商家营业状态值

    private String saleStatusName;//商家营业状态名称

    private Number distance;//距离 单位是m

    private Number latitude;//百度维度

    private Number longitude;//百度经度

    private Integer activityTotal;//活动总数

    private Integer orderTotal;//订单总数

    private Integer score;//服务评分

    private List<NewActivities> activities;//默认活动（2个）

    private List<DealerTags> dealerTags;//商家标签

    private boolean isRecommendDealer;//是否推荐门店

    private String landingPageUrl;//商家落地页URL

    private String mapUrl;//地图中间页地址

    private String schemeType;//协议类型，h5协议需要自己拼接协议头


    /**
     * 活动
     */
    @Data
    public static class NewActivities{
        private String id;//活动ID

        private Integer cshDealerId;//车商汇商家ID

        private Integer activityCategoryId;//活动分类

        private Integer activitySubCategoryId;//活动子分类

        private String activityCategoryName;//活动分类名称

        private String activitySubCategoryName;//活动子分类名称

        private String title;//活动名称

        private Number preferPrice;//优惠价

        private Number price;//原价

        private String startTime;//活动开始时间，格式yyyy-MM-dd

        private String endTime;//活动结束时间，格式yyyy-MM-dd

        private String headerImage;//活动头图

        private String landingPageUrl;//活动落地页URL

        private String schemeType;//协议类型，h5协议需要自己拼接协议头
    }

    /**
     * 商家标签
     */
    @Data
    public static class DealerTags{
        private Integer labelId;

        private String tagName;//免费检测/免费洗车

        private String tagIcon;//综修厂不返回，需要自己切图

        private boolean isSelected;//是否高亮
    }
}
