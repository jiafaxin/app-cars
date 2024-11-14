package com.autohome.app.cars.service.services.dtos.mofang;

import com.autohome.app.cars.common.utils.StrPool;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/10/17 16:13
 */
@NoArgsConstructor
@Data
public class MoFangFeedResult {
    private Integer totalCount;
    private Boolean haveMore;
    private List<ListDTO> list;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private String type = StrPool.EMPTY;
        @JsonProperty("mUrl")
        private String mUrl = StrPool.EMPTY;
        private String pcUrl = StrPool.EMPTY;
        private String appUrl = StrPool.EMPTY;
        private String itemImg = StrPool.EMPTY;
        private List<ItemImgRNDTO> itemimgRn = new ArrayList<>();
        private String title = StrPool.EMPTY;
        private String subTitle = StrPool.EMPTY;
        private String subTitleTags = StrPool.EMPTY;
        private String tags = StrPool.EMPTY;
        private String currentPrice = StrPool.EMPTY;
        private String originalPrice = StrPool.EMPTY;
        private String unit = StrPool.EMPTY;
        private String btnTxt = StrPool.EMPTY;
        private String topTag = StrPool.EMPTY;
        private OtherBtnDTO otherBtn = new OtherBtnDTO();
        private String info1 = StrPool.EMPTY;
        private String info2 = StrPool.EMPTY;
        private String info3 = StrPool.EMPTY;
        private String info4 = StrPool.EMPTY;
        private String info5 = StrPool.EMPTY;

        @NoArgsConstructor
        @Data
        public static class OtherBtnDTO {
            private String name = StrPool.EMPTY;
            @JsonProperty("mUrl")
            private String mUrl = StrPool.EMPTY;
            private String pcUrl = StrPool.EMPTY;
            private String appUrl = StrPool.EMPTY;
        }

        @NoArgsConstructor
        @Data
        public static class ItemImgRNDTO {
            private Integer width;
            private Integer height;
            private String url;
        }
    }
}
