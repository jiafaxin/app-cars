package com.autohome.app.cars.apiclient.che168.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetSeriesAssessPriceResult {
    //估值价
    private Double valuationprice;
    //最低价
    private Double minprice;
    //最高价
    private Double maxprice;
    //上月估值价
    private Double lastmonthprice;
    //对比上月差价
    private Double lastdiffprice;
    //下月估值价
    private Double nextmonthprice;
    //对比下月差价
    private Double nextdiffprice;
}
