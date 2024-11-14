package com.autohome.app.cars.apiclient.vr;

import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrRealSceneResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface PanoApiClient {

    @AutoGet(
            dev = "http://panocmsapi.cupid.autohome.com.cn/v1/exterior/getextframelist?_appid=app&imgtype=webp&category=car&seriesid=${seriesId}&sizelevel=l2",
            beta = "http://panocmsapi.cupid.autohome.com.cn/v1/exterior/getextframelist?_appid=app&imgtype=webp&category=car&seriesid=${seriesId}&sizelevel=l2",
            online = "http://panocms.api.autohome.com.cn/v1/exterior/getextframelist?_appid=app&imgtype=webp&category=car&seriesid=${seriesId}&sizelevel=l2",
            timeout = 1000
    )
    CompletableFuture<BaseModel<SeriesVrExteriorResult>> getSeriesExterior(int seriesId);

    @AutoGet(
            dev = "http://pano.api.autohome.com.cn/v2/vr/getpanoramalistbyseriesid?_appid=car&category=car&seriesid=${seriesId}",
            beta = "http://pano.api.autohome.com.cn/v2/vr/getpanoramalistbyseriesid?_appid=car&category=car&seriesid=${seriesId}",
            online = "http://pano.api.autohome.com.cn/v2/vr/getpanoramalistbyseriesid?_appid=car&category=car&seriesid=${seriesId}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<List<SeriesVrRealSceneResult>>> getSeriesRealScene(int seriesId);


}
