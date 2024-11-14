package com.autohome.app.cars.apiclient.che168.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchengtao
 * @date 2024/10/9 16:35
 */
@NoArgsConstructor
@Data
public class UsedCarDetailResult {

    /**
     * 车系最低价
     */
    private String lowprice;
    /**
     * 当前城市车源个数
     */
    private Integer salecount;

    private Integer allsalecount;
}
