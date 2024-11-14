package com.autohome.app.cars.service.components.subsidy.dtos;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 置换补贴相关字段释义，见下方wiki
 * http://wiki.corpautohome.com/pages/viewpage.action?pageId=337942886
 *
 * @author chengjincheng
 * @date 2024/4/28
 */
@Data
public class SeriesCitySubsidyDto {
    private int cityId;
    private int seriesId;

    private int price;
}
