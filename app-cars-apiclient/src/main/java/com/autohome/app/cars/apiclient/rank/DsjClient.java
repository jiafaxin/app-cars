package com.autohome.app.cars.apiclient.rank;

import com.autohome.app.cars.apiclient.rank.dtos.SeriesHotRankDto;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author chengjincheng
 * @date 2024/6/12
 */
@AutoHttpClient
public interface DsjClient {
    @AutoGet(
            dev = "http://dsj.dataservice.autohome.com.cn/api/app/getSeriesHotRankNew?areaType=1&areaId=${areaId}&level=0&topN=0",
            beta = "http://dsj.dataservice.autohome.com.cn/api/app/getSeriesHotRankNew?areaType=1&areaId=${areaId}&level=0&topN=0",
            online = "http://dsj.dataservice.autohome.com.cn/api/app/getSeriesHotRankNew?areaType=1&areaId=${areaId}&level=0&topN=0"
    )
    CompletableFuture<SeriesHotRankDto> getSeriesHotRankNew(int areaId);
}
