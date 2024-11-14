package com.autohome.app.cars.apiclient.che168;

import com.autohome.app.cars.apiclient.che168.dtos.GetSeriesSpecListJumpInfoResult;
import com.autohome.app.cars.apiclient.che168.dtos.GetUsedCarsJumpInfoResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * 张驰 负责
 * 二手车跳转接口
 */
@AutoHttpClient
public interface DsabClient {
    /**
     * 接口的sceneno和sceneid都是固定参数，更新不频繁，每天刷一次即可
     *
     * @param sceneno
     * @param sceneid
     * @param cityId
     * @param seriesId
     * @param brandId
     * @return
     */
    @AutoGet(
            dev = "http://dsab.corpautohome.com/api/rec/jump/v1?cityid=${cityId}&seriesid=${seriesId}&sceneno=${sceneno}&sceneid=${sceneid}&brandid=${brandId}",
            beta = "http://dsab.corpautohome.com/api/rec/jump/v1?cityid=${cityId}&seriesid=${seriesId}&sceneno=${sceneno}&sceneid=${sceneid}&brandid=${brandId}",
            online = "http://dsab.corpautohome.com/api/rec/jump/v1?cityid=${cityId}&seriesid=${seriesId}&sceneno=${sceneno}&sceneid=${sceneid}&brandid=${brandId}"
    )
    CompletableFuture<BaseModel<GetSeriesSpecListJumpInfoResult>> getSeriesSpecListJumpInfo(int sceneno, int sceneid, int cityId, int seriesId, int brandId);
}
