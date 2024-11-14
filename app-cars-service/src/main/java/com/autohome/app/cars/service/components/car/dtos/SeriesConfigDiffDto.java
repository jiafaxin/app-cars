package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 车系差异配置、图片差异
 */
@Data
public class SeriesConfigDiffDto {

    private List<Integer> picSpecIds =new ArrayList<>();;
    private ConfigDiffDTO configDiff;

    @Data
    public static class ConfigDiffDTO {
        private Integer diffCount;
        private List<Integer> specIds =new ArrayList<>();
    }
}
