package com.autohome.app.cars.service.components.dealer.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DealerSaleRangeDto {
    List<Integer> province;
    List<Integer> city;

}
