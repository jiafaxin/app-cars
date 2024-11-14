package com.autohome.app.cars.apiclient.owner;

import com.autohome.app.cars.apiclient.owner.dtos.BeiliStatisticsResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author : zzli
 * @description : 北里
 * @date : 2024/2/21 17:17
 */
@AutoHttpClient
public interface NewVideoApiClient {
    /**
     * 获取车主数据基本信息 (北里)
     */
    @AutoGet(
            dev = "https://newvideo.autohome.com.cn/openapi/aggregationapi/battery/spec_fact_data?cityId=${cityId}&seriesId=${seriesId}&specId=${specId}",
            beta = "https://newvideo.autohome.com.cn/openapi/aggregationapi/battery/spec_fact_data?cityId=${cityId}&seriesId=${seriesId}&specId=${specId}",
            online = "https://newvideo.autohome.com.cn/openapi/aggregationapi/battery/spec_fact_data?cityId=${cityId}&seriesId=${seriesId}&specId=${specId}"
    )
    CompletableFuture<BaseModel<BeiliStatisticsResult>> getSpecFactData(Integer cityId, Integer seriesId, String specId);

}
