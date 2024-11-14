package com.autohome.app.cars.apiclient.car.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : zzli
 * @description : 车系周销量
 * @date : 2024/4/26 17:09
 */
@NoArgsConstructor
@Data
public class WeekRankHistoryDto {

    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private Integer id;
        private Integer brandid;
        private String seriesid;
        private String week_day;
        private String week_range;
        private Integer rnnum;
        private Integer salecnt;
    }
}
