package com.autohome.app.cars.apiclient.rank;

import com.autohome.app.cars.apiclient.rank.dtos.KoubeiRankResult;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author chengjincheng
 * @date 2024/7/12
 */
@AutoHttpClient
public interface KoubeiRankClient {

    // http://wiki.corpautohome.com/pages/viewpage.action?pageId=101386146
    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/seriesLevelScore/loadLevelSeriesScoreRank?_appid=app&levelids=1,2,3,4,5,6,16,17,18,19,20,8,7,11,12,13,14,15&orderBy=${koubeiTypeId}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/seriesLevelScore/loadLevelSeriesScoreRank?_appid=app&levelids=1,2,3,4,5,6,16,17,18,19,20,8,7,11,12,13,14,15&orderBy=${koubeiTypeId}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/seriesLevelScore/loadLevelSeriesScoreRank?_appid=app&levelids=1,2,3,4,5,6,16,17,18,19,20,8,7,11,12,13,14,15&orderBy=${koubeiTypeId}"
    )
    CompletableFuture<KoubeiRankResult> getKoubeiRankList(int koubeiTypeId);

}
