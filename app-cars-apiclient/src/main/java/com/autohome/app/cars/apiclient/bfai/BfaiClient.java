package com.autohome.app.cars.apiclient.bfai;

import com.autohome.app.cars.apiclient.bfai.dtos.SSeriesSortListResult;
import com.autohome.app.cars.apiclient.bfai.dtos.SeriesSortParam;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;
import com.autohome.app.cars.common.httpclient.annotation.AutoPost;
import com.autohome.app.cars.common.httpclient.annotation.PostBody;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface BfaiClient {

    /**
     * wiki https://zhishi.autohome.com.cn/home/teamplace/file?targetId=jgvfmOmP3Y
     *
     * @return
     */
    @AutoPost(
            dev = "http://uat.bfai.thallo.corpautohome.com/search/bfai",
            beta = "http://bfai.inner.corpautohome.com/search/bfai",
            online = "http://bfai.inner.corpautohome.com/search/bfai",
            timeout = 250
    )
    CompletableFuture<SSeriesSortListResult> getSeriesSortList(@PostBody SeriesSortParam param);
}
