package com.autohome.app.cars.service.services.dtos.mofang;

import com.alibaba.fastjson2.annotation.JSONField;
import com.autohome.app.cars.common.utils.StrPool;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/10/16 19:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MoFangSubsidyHeadCard extends MoFangCardModel{

    private List<BgImgDTO> bgImg = new ArrayList<>();
    private List<LogoImgDTO> logoImg = new ArrayList<>();
    private String priceTxt = StrPool.EMPTY;
    private String titleTxt = StrPool.EMPTY;
    @JsonProperty("mUrl")
    private String mUrl = StrPool.EMPTY;
    private String appUrl = StrPool.EMPTY;
    private String pcUrl = StrPool.EMPTY;
    private int sort = 0;

    @NoArgsConstructor
    @Data
    public static class BgImgDTO {
        private String name;
        private String url;

        public BgImgDTO(String url) {
            this.url = url;
            this.name = StrPool.EMPTY;
        }
    }

    @NoArgsConstructor
    @Data
    public static class LogoImgDTO {
        private String url;
    }
}
