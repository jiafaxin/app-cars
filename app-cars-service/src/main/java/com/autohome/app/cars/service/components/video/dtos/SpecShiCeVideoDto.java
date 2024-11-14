package com.autohome.app.cars.service.components.video.dtos;

import com.autohome.app.cars.apiclient.video.dtos.SpecShiCeSmallVideoResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpecShiCeVideoDto {

    private int specId;
    private List<SpecShiCeSmallVideoResult.ResultBean> videoInfoList = new ArrayList<>();

}
