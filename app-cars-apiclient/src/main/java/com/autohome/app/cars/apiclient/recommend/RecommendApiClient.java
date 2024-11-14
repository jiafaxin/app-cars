package com.autohome.app.cars.apiclient.recommend;

import com.autohome.app.cars.apiclient.recommend.dtos.PkResultInfoDto;
import com.autohome.app.cars.apiclient.recommend.dtos.RecommendPkParam;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;
import com.autohome.app.cars.common.httpclient.annotation.AutoPost;
import com.autohome.app.cars.common.httpclient.annotation.PostBody;

import java.util.concurrent.CompletableFuture;


@AutoHttpClient
public interface RecommendApiClient {
    /**
     * 获取pk推荐列表
     *
     * @param param
     * @return
     */
    @AutoPost(
            dev = "http://test.search.guess.corpautohome.com/recommend/pk",
            beta = "http://search.guess.corpautohome.com/recommend/pk",
            online = "http://search.guess.corpautohome.com/recommend/pk"
    )
    CompletableFuture<BaseModel<PkResultInfoDto>> getRecommendPk(@PostBody RecommendPkParam param);
}
