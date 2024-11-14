package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class CityDto {
    private int code;
    private String name;

    public static Map<Integer, String> getAllNorthCityIdList(String json) {
        if (StringUtils.isNotEmpty(json)) {
            List<CityDto> cityDtoList = JsonUtil.toObjectList(json, CityDto.class);
            ;
            return cityDtoList.stream().collect(Collectors.toMap(CityDto::getCode, CityDto::getName));
        }
        return Collections.emptyMap();
    }
}
