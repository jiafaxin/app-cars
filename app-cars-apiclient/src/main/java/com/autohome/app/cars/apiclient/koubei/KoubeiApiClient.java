package com.autohome.app.cars.apiclient.koubei;

import com.autohome.app.cars.apiclient.koubei.dtos.*;
import com.autohome.app.cars.apiclient.owner.dtos.BeiliStatisticsResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface KoubeiApiClient {

    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/series/carbean?_appid=app&seriesid=${seriesId}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/series/carbean?_appid=app&seriesid=${seriesId}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/series/carbean?_appid=app&seriesid=${seriesId}"
    )
    CompletableFuture<BaseModel<KouBeiSeriesBeanResult>> getSeriesBean(int seriesId);

    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/nev/car/series_info?_appid=koubei&seriesid=${seriesId}&cityid=${cityId}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/nev/car/series_info?_appid=koubei&seriesid=${seriesId}&cityid=${cityId}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/nev/car/series_info?_appid=koubei&seriesid=${seriesId}&cityid=${cityId}"
    )
    CompletableFuture<BaseModel<SeriesInfoNewEnergyKBResult>> getSeriesInfoNewEnergyKBResult(int cityId, int seriesId);

    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/nev/car/seriesindex?_appid=koubei&seriesid=${seriesId}&range=${carrange}&cityid=${cityId}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/nev/car/seriesindex?_appid=koubei&seriesid=${seriesId}&range=${carrange}&cityid=${cityId}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/nev/car/seriesindex?_appid=koubei&seriesid=${seriesId}&range=${carrange}&cityid=${cityId}"
    )
    CompletableFuture<BaseModel<BeiliStatisticsResult>> specFactDataByKouBei(Integer cityId, Integer seriesId, Integer carrange);

    /**
     * 查询口碑数据
     * http://wiki.corpautohome.com/pages/viewpage.action?spaceKey=KBkb&title=series_evaluation_list_v1
     * @param seriesId 车系ID
     * @param grade 0:最满意，最不满意 1：最满意 2：最不满意 3：空间 4：动力 5：操控 6：油耗 7：舒适性 8：外观 9：内饰 10：性价比 11：为什么选择这款车 40：智能配置
     * @param pageSize 每页个数
     * @param pageIndex 页码
     * @return KoubeiInfoRes
     */
    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/series_evaluation_list_v1?_appid=app&id=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&photosize=180&grade=${grade}&year=0&isappend=0&order=0&provinceid=",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/series_evaluation_list_v1?_appid=app&id=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&photosize=180&grade=${grade}&year=0&isappend=0&order=0&provinceid=",
            online = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/series_evaluation_list_v1?_appid=app&id=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&photosize=180&grade=${grade}&year=0&isappend=0&order=0&provinceid=",
            timeout = 3000
    )
    CompletableFuture<BaseModel<KoubeiInfoResult>> getKouBeiInfoList(Integer seriesId, Integer grade, Integer pageSize, Integer pageIndex);

    /**
     * https://doc.autohome.com.cn/docapi/page/share/share_uGQDFaHVcu
     */
    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/series_evaluation_list_v1?_appid=app&id=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&photosize=300&grade=${grade}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/series_evaluation_list_v1?_appid=app&id=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&photosize=300&grade=${grade}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/series_evaluation_list_v1?_appid=app&id=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&photosize=300&grade=${grade}",
            timeout = 30000
    )
    CompletableFuture<BaseModel<KoubeiInfoResult>> getKouBeiInfoList2(Integer seriesId, Integer grade, Integer pageSize, Integer pageIndex);

    /**
     * 判断车系是否有口碑信息
     *
     * @param seriesId
     * @return
     */
    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/stat/haskoubei?_appid=app&seriesid=${seriesId}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/stat/haskoubei?_appid=app&seriesid=${seriesId}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/stat/haskoubei?_appid=app&seriesid=${seriesId}"
    )
    CompletableFuture<BaseModel<KouBeiSeriesHasDataResult>> getSeriesHasKoubei(int seriesId);

    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/spec_evaluation_list_v1_count?_appid=app&id=${specId}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/spec_evaluation_list_v1_count?_appid=app&id=${specId}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/masterdata/spec_evaluation_list_v1_count?_appid=app&id=${specId}"
    )
    CompletableFuture<BaseModel<Integer>> getSpecKouBeiCount(int specId);

    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/Spec/GetSpecScore?_appid=cms&specIds=${specIds}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/Spec/GetSpecScore?_appid=cms&specIds=${specIds}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/Spec/GetSpecScore?_appid=cms&specIds=${specIds}"
    )
    CompletableFuture<BaseModel<List<KoubeiSpecScoreResult>>> getSpecScore(String specIds);


    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/Series/GetSeriesScoreUserNum?_appid=app&seriesId=${seriesId}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/Series/GetSeriesScoreUserNum?_appid=app&seriesId=${seriesId}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/Series/GetSeriesScoreUserNum?_appid=app&seriesId=${seriesId}"
    )
    CompletableFuture<BaseModel<KoubeiScoreResult>> getSeriesScoreUserNum(int seriesId);

    /**
     * 车系语义信息
     * http://wiki.corpautohome.com/pages/viewpage.action?pageId=69075130
     * @param seriesId
     * @param year
     * @return
     */
    @AutoGet(
            dev = "http://koubei.api.sjz.autohome.com.cn/api/Semantic/LoadSeriesPRCType?_appid=app&typekey=-1&year=${year}&seriesId=${seriesId}",
            beta = "http://koubei.api.sjz.autohome.com.cn/api/Semantic/LoadSeriesPRCType?_appid=app&typekey=-1&year=${year}&seriesId=${seriesId}",
            online = "http://koubei.api.sjz.autohome.com.cn/api/Semantic/LoadSeriesPRCType?_appid=app&typekey=-1&year=${year}&seriesId=${seriesId}",
            timeout = 3000
    )
    CompletableFuture<BaseModel<KoubeiPRCResult>> LoadSeriesPRCType(int seriesId, int year);
}
