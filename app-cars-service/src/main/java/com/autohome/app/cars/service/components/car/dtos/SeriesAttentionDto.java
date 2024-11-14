package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeriesAttentionDto {
    private int seriesId;

    List<SpecAttention> specAttentions = new ArrayList<>();

    @Data
    public static class SpecAttention {
        int specid;
        int paramIsShow;
        int yearName;
        String specname;
        int attention;
    }
}
