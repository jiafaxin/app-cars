package com.autohome.app.cars.apiclient.jiage.dtos;

import lombok.Data;

@Data
public class SeriesBottomPriceResult {
    int total;
    String ownerPrice;
    String price;
    int seriesId;
    String subTitle;
}
