package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 动态频道配置
 * @date : 2024/9/6 16:28
 */
@NoArgsConstructor
@Data
public class SubscribeConfig {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeConfig.class);

    private String title;
    private String onLine;
    private String comingSoon;
    private String cms_source;
    private NoScribeInfo noScribeInfo;
    private PriceDownThreshold priceDownThreshold;
    /**
     * 动态服务内容卡片分类配置：名称
     */
    private List<NodelistDTO> nodelist = new ArrayList<>();

    @NoArgsConstructor
    @Data
    public static class NodelistDTO {
        private String title;
        private Integer code;
    }

    @NoArgsConstructor
    @Data
    public static class NoScribeInfo {
        private String title;
        private String subtitle;
        private String describe;
    }

    @NoArgsConstructor
    @Data
    public static class PriceDownThreshold{
        private double dealerThreshold;
        private int guideThreshold;
    }

    public static SubscribeConfig createFromJson(String json) {
        try {
            if (json == null || json.equals("")) {
                return new SubscribeConfig();
            }
            return JsonUtil.toObject(json, SubscribeConfig.class);
        } catch (Exception e) {
            logger.error("AutoShowConfig 序列化失败", e);
            return new SubscribeConfig();
        }
    }


}
