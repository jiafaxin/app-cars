package com.autohome.app.cars.service.components.baike.dtos;

import com.autohome.app.cars.apiclient.baike.dtos.BaikeInfoResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BaikeInfoDto {

    private List<BaikeInfoResult.ResultBean> baikeInfos = new ArrayList<>();

}
