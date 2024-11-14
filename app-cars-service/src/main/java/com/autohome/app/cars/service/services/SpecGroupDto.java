package com.autohome.app.cars.service.services;

import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class SpecGroupDto implements Serializable {

    private String groupName;

    private String name;

    private String type;
    //排序字段，不返回 排量
    private String paiLiang;
    //排序字段，不返回 进气方式
    private String jinQiFS;
    //排序字段，不返回 马力
    private String maLi;

    private List<SpecDetailDto> specDetailDtos;
}
