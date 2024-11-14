package com.autohome.app.cars.service.components.car.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : zzli
 * @description : 限时降需求-官降迭代 产研需求群
 * @date : 2024/10/24 10:53
 */
@NoArgsConstructor
@Data
public class CarPriceChangeDto {

    private List<CutPriceListDTO> cutPriceList = new ArrayList<>();

    /**
     * {
     * "changetype": 40, //降价类型：40直降、50限时降
     * "seriesid": 7806,
     * "specid": 69110,
     * "cutprice": 3000, //降多少元
     * "targetPrice": 109800, //降价后价格
     * "startTime": "2024-04-12 19:16:00.000", //开始时间
     * "endTime": "2024-12-31 12:32:00.000", //结束时间，为null时表时不限结束时间
     * "h5Url": "", //h5地址，可以为空
     * "rnUrl": "", //文章rn的地址，可以为空
     * "description": "补贴零售价补贴零售价补贴零售价" //说明描述
     * }
     */
    @NoArgsConstructor
    @Data
    public static class CutPriceListDTO {
        private int changeType;
        private int seriesId;
        private int specId;
        private int cutPrice;
        private int targetPrice;
        /**
         * 活动开始时间
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date startTime;
        /**
         * 活动截止时间
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date endTime;
        private String h5Url;
        private String rnUrl;
        private String description;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createdStime;
    }
}
