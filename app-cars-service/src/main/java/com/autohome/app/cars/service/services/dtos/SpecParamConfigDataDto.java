package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.apiclient.baike.dtos.BaikeInfoResult;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecConfigInfoDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamInfoDto;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpecParamConfigDataDto {
    //车系ID
    List<Integer> seriesIdList = new ArrayList<>();
    //车型ID
    List<Integer> specIdList = new ArrayList<>();
    //车型信息
    List<SpecDetailDto> specDatailList = new ArrayList<>();
    //车型参数信息
    List<SpecParamInfoDto> specParamInfoList = new ArrayList<>();
    //车型配置信息
    List<SpecConfigInfoDto> specConfigInfoList = new ArrayList<>();
    //百科信息
    BaikeInfoResult baikeInfoResult = null;
    //关注度最高的车型
    SpecDetailDto maxAttentionSpec = null;
    //经销商价格
    List<SpecCityAskPriceDto> specCityAskPriceDtoList = new ArrayList<>();
}
