package com.autohome.app.cars.apiclient.rank;

import com.autohome.app.cars.apiclient.rank.dtos.SeriesRankHistoryResult;
import com.autohome.app.cars.common.httpclient.annotation.AutoCache;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface RankHistoryClient {

    @AutoGet(
            dev = "http://common-output.openapi.corpautohome.com/v1/ol/historyCarseriesSaleRank?flag=true&seriesid=${seriesId}",
            beta = "http://common-output.openapi.corpautohome.com/v1/ol/historyCarseriesSaleRank?flag=true&seriesid=${seriesId}",
            online = "http://common-output.openapi.corpautohome.com/v1/ol/historyCarseriesSaleRank?flag=true&seriesid=${seriesId}",
            authorization = "Basic YXBwLWNhcnMtYXBpOnR0RnBzITg4czM=",
            timeout = 750
    )
    @AutoCache(liveTime = 60 * 30, effectiveTime = 60 * 30)
    CompletableFuture<SeriesRankHistoryResult> getRankHistory(int seriesId);

}
