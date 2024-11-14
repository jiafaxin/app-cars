package com.autohome.app.cars.service.components.recrank.dtos;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class NewPowerConfigDto {

    @JsonProperty("series_list")
    private List<Integer> seriesList = new ArrayList<>(0);

    public static NewPowerConfigDto parseConfigDto(String newPowerRankConfig) {
        if (newPowerRankConfig != null && !newPowerRankConfig.isEmpty()) {
            return JSON.parseObject(newPowerRankConfig, NewPowerConfigDto.class);
        }
        return new NewPowerConfigDto();
    }
}
