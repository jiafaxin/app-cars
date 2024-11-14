package com.autohome.app.cars.apiclient.car;


import com.autohome.app.cars.apiclient.car.dtos.ConfigItemResult;
import com.autohome.app.cars.apiclient.car.dtos.KouBeiInfoDto;
import com.autohome.app.cars.apiclient.car.dtos.SNewEnaryConfigResult;
import com.autohome.app.cars.apiclient.car.dtos.SpecConfigResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoCache;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface ConfigItemApiClient {

    @AutoGet(
            dev = "http://car.api.autohome.com.cn/v1/app/Config_itemBaseInfo.ashx?_appid=app",
            beta = "http://car.api.autohome.com.cn/v1/app/Config_itemBaseInfo.ashx?_appid=app",
            online = "http://car.api.autohome.com.cn/v1/app/Config_itemBaseInfo.ashx?_appid=app",
            timeout = 1000
    )
    @AutoCache(liveTime = 60 * 60, effectiveTime = 60 * 60)
    CompletableFuture<BaseModel<List<ConfigItemResult>>> getConfigItemBaseInfo();

    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/carConfig/contentList?_appid=koubei&seriesid=${seriesid}&specid=${specid}&configid=${configid}&year=${year}&subconfigid=${subconfigid}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/carConfig/contentList?_appid=koubei&seriesid=${seriesid}&specid=${specid}&configid=${configid}&year=${year}&subconfigid=${subconfigid}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/carConfig/contentList?_appid=koubei&seriesid=${seriesid}&specid=${specid}&configid=${configid}&year=${year}&subconfigid=${subconfigid}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<List<KouBeiInfoDto>>> getKouBeiInfo(Integer seriesid,Integer specid,Integer configid,Integer year,Integer subconfigid);

    @AutoGet(
            dev = "http://appletcore.corpautohome.com/api/Article/GetWxQRcodeByUrl?_appid=koubei&wxurl=${wxurl}&sence=${sence}",
            beta = "http://appletcore.corpautohome.com/api/Article/GetWxQRcodeByUrl?_appid=koubei&&wxurl=${wxurl}&sence=${sence}",
            online = "http://appletcore.corpautohome.com/api/Article/GetWxQRcodeByUrl?_appid=koubei&&wxurl=${wxurl}&sence=${sence}",
            timeout = 1000
    )
    CompletableFuture<BaseModel> getQrcode(String wxurl,String sence);

    @AutoGet(
            dev = "http://car.api.autohome.com.cn/v1/App/Electric_SpecParamBySeriesId.ashx?_appid=app&seriesid=${seriesid}",
            beta = "http://car.api.autohome.com.cn/v1/App/Electric_SpecParamBySeriesId.ashx?_appid=app&seriesid=${seriesid}",
            online = "http://car.api.autohome.com.cn/v1/App/Electric_SpecParamBySeriesId.ashx?_appid=app&seriesid=${seriesid}",
            timeout = 200
    )
    CompletableFuture<SNewEnaryConfigResult> getNewEnantyConfigInfo(Integer seriesid);
}
