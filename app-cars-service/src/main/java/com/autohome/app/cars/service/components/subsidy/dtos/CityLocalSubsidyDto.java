package com.autohome.app.cars.service.components.subsidy.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/9/25
 */
@Data
public class CityLocalSubsidyDto {

    /**
     * 补贴类型外展名称
     */
    private String title;

    /**
     * 补贴类型ID: 1:新购、2:置换、3:报废买新、-1:全国报废补贴政策
     */
    private int subsidyTypeId;

    /**
     * 二级分类id,自己定义的，业务方没有这个数据
     */
    private int subsidySubTypeId;

    /**
     * 领取路径
     */
    private List<String> receivePath = new ArrayList<>();

    /**
     * 排序
     */
    private int order;

    /**
     * 应用的补贴序号 从0开始
     */
    private int index = -1;
    /**
     * 活动开始时间,有可能为null
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 活动截止时间,有可能为null
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 补贴金额参考的价格区间
     */
    private List<PriceSubsidyDto> priceList = new ArrayList<>();

    /**
     * 级别
     */
    private List<Integer> carLevelIdSet = new ArrayList<>();

    /**
     * 燃油类型
     */
    private List<Integer> fuelTypeSet = new ArrayList<>();

    /**
     * 补贴政策说明
     */
    private String subsidyPolicy;

    /**
     * 0 < 车价 <= 5万 补贴5000
     * 5万 < 车价 <= 10万 补贴10000
     * 10万 < 车价 补贴20000
     */
    @Data
    public static class PriceSubsidyDto {

        /**
         * 价格区间下限
         */
        private Integer minPrice;

        /**
         * 是否包含下限（0代表不包含，1代表包含）
         */
        private Integer minPriceInclude;

        /**
         * 价格区间上限(0代表无上限，也就是+∞)
         */
        private Integer maxPrice;

        /**
         * 是否包含上限
         */
        private Integer maxPriceInclude;

        /**
         * 补贴金额
         */
        private Integer amount;

        public PriceSubsidyDto() {
        }

        public PriceSubsidyDto(Integer amount) {
            this.amount = amount;
        }
    }
}
