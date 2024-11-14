package com.autohome.app.cars.apiclient.jiage;

import com.autohome.app.cars.apiclient.jiage.dtos.SeriesBottomPriceResult;
import com.autohome.app.cars.apiclient.jiage.dtos.SpecCityListOwnerPriceResult;
import com.autohome.app.cars.apiclient.jiage.dtos.SpecCityOwnerPriceResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface JiageApiClient {

    @AutoGet(
            dev = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getSeriesBottomPrice?_appid=app&seriesIds=${seriesIds}",
            beta = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getSeriesBottomPrice?_appid=app&seriesIds=${seriesIds}",
            online = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getSeriesBottomPrice?_appid=app&seriesIds=${seriesIds}"
    )
    CompletableFuture<BaseModel<List<SeriesBottomPriceResult>>> getSeriesBottomPrice(String seriesIds);

    /**
     * 批量获取车型车主价格列表
     */
    @AutoGet(
            dev = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getCarNumAndPriceSpecIds?specIds=${specId}&cityId=${cityId}&_appid=app",
            beta = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getCarNumAndPriceSpecIds?specIds=${specId}&cityId=${cityId}&_appid=app",
            online = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getCarNumAndPriceSpecIds?specIds=${specId}&cityId=${cityId}&_appid=app"
    )
    CompletableFuture<BaseModel<List<SpecCityOwnerPriceResult>>> getOwnerPrice(String specId, int cityId);

    @AutoGet(
            dev = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getSpecNumCityGroup?_appid=app&specId=${specId}",
            beta = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getSpecNumCityGroup?_appid=app&specId=${specId}",
            online = "http://jiageapi.in.autohome.com.cn/api/carprice/v2/getSpecNumCityGroup?_appid=app&specId=${specId}",
            timeout = 10000
    )
    CompletableFuture<BaseModel<List<SpecCityListOwnerPriceResult>>> getOwnerPriceCityList(String specId);
}
