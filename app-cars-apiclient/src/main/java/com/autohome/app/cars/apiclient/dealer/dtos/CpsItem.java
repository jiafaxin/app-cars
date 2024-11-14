package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CpsItem {
    int seriesId;
    BigDecimal facRebateAmount;
}
