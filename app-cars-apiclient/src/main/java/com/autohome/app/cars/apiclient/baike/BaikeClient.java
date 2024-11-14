package com.autohome.app.cars.apiclient.baike;

import com.autohome.app.cars.apiclient.baike.dtos.ConfigBaikeLinkDto;
import com.autohome.app.cars.apiclient.bfai.dtos.SeriesSortParam;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;
import com.autohome.app.cars.common.httpclient.annotation.AutoPost;
import com.autohome.app.cars.common.httpclient.annotation.PostBody;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface BaikeClient {

    /**
     * wiki https://zhishi.autohome.com.cn/home/teamplace/file?targetId=jgvfmOmP3Y
     *
     * @return
     */
    @AutoGet(
            dev = "http://uc-news-baikeservice.msapi.autohome.com.cn/baike/getbaikelinkforconfig?_appid=app",
            beta = "http://uc-news-baikeservice.msapi.autohome.com.cn/baike/getbaikelinkforconfig?_appid=app",
            online = "http://uc-news-baikeservice.msapi.autohome.com.cn/baike/getbaikelinkforconfig?_appid=app",
            timeout = 3000
    )
    CompletableFuture<BaseModel<List<ConfigBaikeLinkDto>>> getSeriesSortList();

}
