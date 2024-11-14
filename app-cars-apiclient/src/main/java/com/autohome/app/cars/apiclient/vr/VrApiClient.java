package com.autohome.app.cars.apiclient.vr;

import com.autohome.app.cars.apiclient.vr.dtos.CockpitVrResult;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.apiclient.vr.dtos.SpecVrInfoResult;
import com.autohome.app.cars.apiclient.vr.dtos.VrSuperCarResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * VR超级车型库入口资源接口-车系列表 - 高洋
 * http://wiki.corpautohome.com/pages/viewpage.action?pageId=100474792
 * carSpecList这个旧接口已不用，使用新的carSpecListNew接口
 */
@AutoHttpClient
public interface VrApiClient {

    @AutoGet(
            dev = "http://vr.api.autohome.com.cn/api/entrance/brandexhibition/carSpecListNew",
            beta = "http://vr.api.autohome.com.cn/api/entrance/brandexhibition/carSpecListNew",
            online = "http://vr.api.autohome.com.cn/api/entrance/brandexhibition/carSpecListNew"
    )
    CompletableFuture<BaseModel<List<VrSuperCarResult>>> getSuperCarList();

    @AutoGet(
            dev = "http://panoapi.cupid.autohome.com.cn/v2/vr/getcockpitbyseriesId?_appid=app&category=car&seriesid=${seriesid}",
            beta = "http://pano.api.lq.autohome.com.cn/v2/vr/getcockpitbyseriesId?_appid=app&category=car&seriesid=${seriesid}",
            online = "http://pano.api.lq.autohome.com.cn/v2/vr/getcockpitbyseriesId?_appid=app&category=car&seriesid=${seriesid}"
    )
    CompletableFuture<BaseModel<CockpitVrResult>> getCockpitVrInfo(int seriesid);

    /**
     * 获取车型最新VR地址
     *
     * @param specId
     * @return
     */
    @AutoGet(
            dev = "http://pano.api.lq.autohome.com.cn/v1/vr/getcarvrinfobyspecid?_appid=app&category=car&specid=${specId}",
            beta = "http://pano.api.lq.autohome.com.cn/v1/vr/getcarvrinfobyspecid?_appid=app&category=car&specid=${specId}",
            online = "http://pano.api.lq.autohome.com.cn/v1/vr/getcarvrinfobyspecid?_appid=app&category=car&specid=${specId}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<SpecVrInfoResult>> getSpecVrInfo(int specId);

    /**
     * 获取车系最新VR地址
     *
     * @param seriesid
     * @return
     */
    @AutoGet(
            dev = "http://pano.api.lq.autohome.com.cn/v1/vr/getcarvrinfobyseriesid?_appid=app&category=car&seriesid=${seriesid}",
            beta = "http://pano.api.lq.autohome.com.cn/v1/vr/getcarvrinfobyseriesid?_appid=app&category=car&seriesid=${seriesid}",
            online = "http://pano.api.lq.autohome.com.cn/v1/vr/getcarvrinfobyseriesid?_appid=app&category=car&seriesid=${seriesid}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<SpecVrInfoResult>> getVrInfo(int seriesid);

    @AutoGet(
            dev = "http://panocms.api.autohome.com.cn/v1/exterior/getextframelistbyspecid?_appid=app&imgtype=webp&category=car&specid=${specId}&sizelevel=l2",
            beta = "http://panocms.api.autohome.com.cn/v1/exterior/getextframelistbyspecid?_appid=app&imgtype=webp&category=car&specid=${specId}&sizelevel=l2",
            online = "http://panocms.api.autohome.com.cn/v1/exterior/getextframelistbyspecid?_appid=app&imgtype=webp&category=car&specid=${specId}&sizelevel=l2",
            timeout = 1000
    )
    CompletableFuture<BaseModel<SeriesVrExteriorResult>> getSpecExterior(int specId);
}
