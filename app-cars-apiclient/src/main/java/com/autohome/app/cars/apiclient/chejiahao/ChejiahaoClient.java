package com.autohome.app.cars.apiclient.chejiahao;

import com.autohome.app.cars.apiclient.chejiahao.dtos.SSecondCheJiaHaoNewsResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface ChejiahaoClient {

    @AutoGet(
            dev = "http://chejiahao.api.lq.autohome.com.cn/InfoService.svc/GetInfosRecommendTop?_appid=app&seriesId=${seriesId}",
            beta = "http://chejiahao.api.lq.autohome.com.cn/InfoService.svc/GetInfosRecommendTop?_appid=app&seriesId=${seriesId}",
            online = "http://chejiahao.api.lq.autohome.com.cn/InfoService.svc/GetInfosRecommendTop?_appid=app&seriesId=${seriesId}"
    )
    CompletableFuture<BaseModel<SSecondCheJiaHaoNewsResult>> getSecondCheJiaHaoNews(int seriesId);

}
