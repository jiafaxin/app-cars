package com.autohome.app.cars.service.services.dtos.mofang;


import com.autohome.app.cars.common.utils.StrPool;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/10/16 20:36
 */
@NoArgsConstructor
@Data
public class MoFangSubsidyPartCard {

    private String name;

    private List<ListDTO> list;

    private String key = StrPool.EMPTY;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        @JsonProperty("mUrl")
        private String mUrl = StrPool.EMPTY;
        private String appUrl = StrPool.EMPTY;
        private String pcUrl = StrPool.EMPTY;
        private String usedcarUrl = StrPool.EMPTY;
        private boolean urlPvareaid;
        private boolean callApp;
        private String uRLScheme = StrPool.EMPTY;
        private String adrDown = StrPool.EMPTY;
        private String iosDown = StrPool.EMPTY;
        private String copyText = StrPool.EMPTY;
        private String itemImg = StrPool.EMPTY;
        private String title = StrPool.EMPTY;
        private String subTitle = StrPool.EMPTY;
        private String btnName = StrPool.EMPTY;
        private BtnUrlDTO btnUrl = new BtnUrlDTO();
        private String key = StrPool.EMPTY;
        private String info1 = StrPool.EMPTY;
        private String info2 = StrPool.EMPTY;
        private String info3 = StrPool.EMPTY;
        private String info4 = StrPool.EMPTY;
        private String info5 = StrPool.EMPTY;
        @NoArgsConstructor
        @Data
        public static class BtnUrlDTO {
            @JsonProperty("mUrl")
            private String mUrl = StrPool.EMPTY;
            private String appUrl = StrPool.EMPTY;
            private String pcUrl = StrPool.EMPTY;
            private String usedcarUrl = StrPool.EMPTY;
            private boolean urlPvareaid;
            private boolean callApp;
            private String uRLScheme = StrPool.EMPTY;
            private String adrDown = StrPool.EMPTY;
            private String iosDown = StrPool.EMPTY;
            private String copyText = StrPool.EMPTY;
        }
    }
}
