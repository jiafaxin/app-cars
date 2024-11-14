package com.autohome.app.cars.apiclient.rank;

import com.autohome.app.cars.apiclient.rank.dtos.ClueCheckAllResultDto;
import com.autohome.app.cars.apiclient.rank.dtos.NewEnergyRankDto;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface NewEnergyDataClient {

    @AutoGet(
            dev = "http://cms.api.autohome.com.cn/Wcf/ModuleArticleService.svc/GetNewEnergySeriesAHEvaluateItems?_appid=app&seriesIds=${seriesIds}",
            beta = "http://cms.api.autohome.com.cn/Wcf/ModuleArticleService.svc/GetNewEnergySeriesAHEvaluateItems?_appid=app&seriesIds=${seriesIds}",
            online = "http://cms.api.autohome.com.cn/Wcf/ModuleArticleService.svc/GetNewEnergySeriesAHEvaluateItems?_appid=app&seriesIds=${seriesIds}",
            timeout = 750
    )
    CompletableFuture<NewEnergyRankDto> getRankNewEnergyInfo(String seriesIds);

}
