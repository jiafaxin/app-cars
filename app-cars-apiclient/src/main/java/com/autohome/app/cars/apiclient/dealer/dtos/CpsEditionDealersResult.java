package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpsEditionDealersResult {
    private Integer dealerId;
    private String dealerSimpleName;
    private String address;
    private Integer kindId;

}
