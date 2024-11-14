package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 车系日历
 * @date : 2024/4/26 10:35
 */
@Data
public class SeriesTimeAxisDto {
    private List<Item> itemList = new ArrayList<>();

    @NoArgsConstructor
    @Data
    public static class Item {
        /**
         * 事件对应的一个编码
         */
        private Integer typecode;
        /**
         * 发布日期
         */
        private String date;
        /**
         * 跳转url
         */
        private String url;
        /**
         * 107 的销量数据
         */
        private int salecnt;
    }
}
