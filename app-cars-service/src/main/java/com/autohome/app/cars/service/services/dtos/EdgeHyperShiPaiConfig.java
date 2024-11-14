package com.autohome.app.cars.service.services.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EdgeHyperShiPaiConfig {

    private List<Integer> seriesIds = new ArrayList<>();

    private String title;

}
