package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import java.util.List;

/**
 * @ Author     ：lvming
 * @ Date       ：Created in 14:57 2020/11/2
 * @ Description：多车型参数配置图片提示实体
 * @ Modified By：
 * @Version: $
 */
public class ParamConfigTipsGroupDto {

    private Integer specid;

    private List<SpecParamConfigPicTipDto> list;


    public List<SpecParamConfigPicTipDto> getList() {
        return list;
    }

    public void setList(List<SpecParamConfigPicTipDto> list) {
        this.list = list;
    }

    public Integer getSpecid() {
        return specid;
    }

    public void setSpecid(Integer specid) {
        this.specid = specid;
    }





}
