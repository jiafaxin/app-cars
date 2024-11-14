package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by dx on 2024/1/4
 * 经销商180天城市车型最低报价接口实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecCityPriceHisResult {

    private Integer newsPrice;
    private String date;
}
