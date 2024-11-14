package com.autohome.app.cars.service.components.video.dtos;

import com.autohome.app.cars.apiclient.video.dtos.SpecSmallVideoResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SpecConfigSmallVideoDto {

    private int specId;
    private Map<Integer,SpecSmallVideoResult.ResultBean.VideolistBean> videoInfoMap = new HashMap<>();

}
