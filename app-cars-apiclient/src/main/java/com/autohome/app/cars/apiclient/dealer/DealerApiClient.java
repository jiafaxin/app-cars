package com.autohome.app.cars.apiclient.dealer;

import com.autohome.app.cars.apiclient.dealer.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface DealerApiClient {

    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/statistics/policy/listFactOrGovSpecs?_appId=app&cityId=${cityId}&seriesId=${seriesId}",
            beta = "http://dealer.api.lq.autohome.com.cn/statistics/policy/listFactOrGovSpecs?_appId=app&cityId=${cityId}&seriesId=${seriesId}",
            online = "http://dealer.api.lq.autohome.com.cn/statistics/policy/listFactOrGovSpecs?_appId=app&cityId=${cityId}&seriesId=${seriesId}"
    )
    CompletableFuture<BaseModel<List<FactOrGovSpecsResult>>> listFactOrGovSpecs(int seriesId, int cityId);


    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/statistics/seriesprice/getSeriesMinpriceWithSpecs?_appId=app&cityid=${cityId}&seriesId=${seriesId}&_entryid=1&specEntryId=1&isNeedUnSale=0",
            beta = "http://dealer.api.lq.autohome.com.cn/statistics/seriesprice/getSeriesMinpriceWithSpecs?_appId=app&cityid=${cityId}&seriesId=${seriesId}&_entryid=1&specEntryId=1&isNeedUnSale=0",
            online = "http://dealer.api.lq.autohome.com.cn/statistics/seriesprice/getSeriesMinpriceWithSpecs?_appId=app&cityid=${cityId}&seriesId=${seriesId}&_entryid=1&specEntryId=1&isNeedUnSale=0"
    )
    CompletableFuture<BaseModel<List<DealerSpecCanAskPriceNewApiResult>>> getSeriesMinpriceWithSpecs(int seriesId, int cityId);

    /**
     * 经销商价格信息
     *
     * @param specId 车型id
     * @param cityId 城市id
     * @return
     */
    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/statistics/spec/getSpecsMinPriceExtend?_appId=app&cityid=${cityId}&specids=${specId}",
            beta = "http://dealer.api.lq.autohome.com.cn/statistics/spec/getSpecsMinPriceExtend?_appId=app&cityid=${cityId}&specids=${specId}",
            online = "http://dealer.api.lq.autohome.com.cn/statistics/spec/getSpecsMinPriceExtend?_appId=app&cityid=${cityId}&specids=${specId}"
    )
    CompletableFuture<BaseModel<SpecCityPriceResult>> getSpecCityDealerPrice(int specId, int cityId);

    @AutoGet(
            dev = "http://export.dealer.api.lq.autohome.com.cn/ExportAPI/dealer/listCshDealerByCity?cityId=${cityId}&_appid=car",
            beta = "http://export.dealer.api.lq.autohome.com.cn/ExportAPI/dealer/listCshDealerByCity?cityId=${cityId}&_appid=car",
            online = "http://export.dealer.api.lq.autohome.com.cn/ExportAPI/dealer/listCshDealerByCity?cityId=${cityId}&_appid=car"
    )
    CompletableFuture<BaseModel<List<ListCshDealerByCityResult>>> getDealersByCityId(int cityId);

    @AutoGet(
            dev = "http://export.dealer.api.lq.autohome.com.cn/ExportAPI/spec/getSpecPriceListByDealerId?_appid=car&dealerId=${dealerId}",
            beta = "http://export.dealer.api.lq.autohome.com.cn/ExportAPI/spec/getSpecPriceListByDealerId?_appid=car&dealerId=${dealerId}",
            online = "http://export.dealer.api.lq.autohome.com.cn/ExportAPI/spec/getSpecPriceListByDealerId?_appid=car&dealerId=${dealerId}",
            timeout = 10000
    )
    CompletableFuture<BaseModel<List<SpecPriceItem>>> getSpecPriceListByDealerId(int dealerId);

    @AutoGet(
            dev = "http://tm.api.lq.corpautohome.com/dealerInfo/getLeadsRange?_appId=tm&dealerInfoId=${dealerId}",
            beta = "http://tm.api.lq.corpautohome.com/dealerInfo/getLeadsRange?_appId=tm&dealerInfoId=${dealerId}",
            online = "http://tm.api.lq.corpautohome.com/dealerInfo/getLeadsRange?_appId=tm&dealerInfoId=${dealerId}"
    )
    CompletableFuture<BaseModel<LeadsRangeResultDto>> getLeadsRange(int dealerId);

    @AutoGet(
            dev = "http://dealer.export.api.terra.corpautohome.com/ExportAPI/lq/dealerorderc/cpsProduct/getSortList?_appid=car&cityId=${cityId}&pageIndex=${pageIndex}&pageSize=${pageSize}",
            beta = "http://export.dealer.api.lq.autohome.com.cn/ExportAPI/lq/dealerorderc/cpsProduct/getSortList?_appid=car&cityId=${cityId}&pageIndex=${pageIndex}&pageSize=${pageSize}",
            online = "http://export.dealer.api.lq.autohome.com.cn/ExportAPI/lq/dealerorderc/cpsProduct/getSortList?_appid=car&cityId=${cityId}&pageIndex=${pageIndex}&pageSize=${pageSize}",
            timeout = 3000
    )
    CompletableFuture<BaseModel<List<CpsItem>>> getCpsProduct(int cityId, int pageIndex, int pageSize);

    /**
     * 源接口对接人：冯岩
     *
     * @param cityId
     * @param specIds
     * @return
     */
    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cpsProduct/getList?_appid=app&cityId=${cityId}&specIds=${specIds}",
            beta = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cpsProduct/getList?_appid=app&cityId=${cityId}&specIds=${specIds}",
            online = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cpsProduct/getList?_appid=app&cityId=${cityId}&specIds=${specIds}",
            timeout = 180
    )
    CompletableFuture<BaseModel<List<SpecCityCpsInfoResult>>> getSpecCityCpsInfo(int cityId, String specIds);

    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/statistics/text/listAreaContent?_appId=app&cityid=${cityId}&seriesId=${seriesId}&specId=${specId}&areaIds=${areaIds}&appVersion=${appVersion}",
            beta = "http://dealer.api.lq.autohome.com.cn/statistics/text/listAreaContent?_appId=app&cityid=${cityId}&seriesId=${seriesId}&specId=${specId}&areaIds=${areaIds}&appVersion=${appVersion}",
            online = "http://dealer.api.lq.autohome.com.cn/statistics/text/listAreaContent?_appId=app&cityid=${cityId}&seriesId=${seriesId}&specId=${specId}&areaIds=${areaIds}&appVersion=${appVersion}",
            timeout = 180
    )
    CompletableFuture<BaseModel<List<ListAreaContentResult>>> getListAreaContent(int cityId, int seriesId, int specId, int areaIds, String appVersion);

    /**
     * https://zhishi.autohome.com.cn/home/teamplace/file?targetId=mqc96VHCKG
     * 经销商询价接口
     */
    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/statistics/text/listSmartAreaButton?specId=${specids}&seriesId=${seriesId}&_appId=app&cityid=${cityid}&areaId=${areaid}&pvid=${pvid}&appVersion=${pluginversion}&deviceType=${devicetype}&deviceId=${deviceid}&traceType=${traceType}",
            beta = "http://dealer.api.lq.autohome.com.cn/statistics/text/listSmartAreaButton?specId=${specids}&seriesId=${seriesId}&_appId=app&cityid=${cityid}&areaId=${areaid}&pvid=${pvid}&appVersion=${pluginversion}&deviceType=${devicetype}&deviceId=${deviceid}&traceType=${traceType}",
            online = "http://dealer.api.lq.autohome.com.cn/statistics/text/listSmartAreaButton?specId=${specids}&seriesId=${seriesId}&_appId=app&cityid=${cityid}&areaId=${areaid}&pvid=${pvid}&appVersion=${pluginversion}&deviceType=${devicetype}&deviceId=${deviceid}&traceType=${traceType}",
            timeout = 200
    )
    CompletableFuture<BaseModel<List<SListAreaButtonResult>>> getListSmartAreaButton(int cityid, String devicetype, int areaid, String specids, String pluginversion, String deviceid, String traceType, int seriesId, String pvid);

    /**
     * @param cityId
     * @param seriesId
     * @return
     */
    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/statistics/seriesprice/getSeriesMinPriceExtends?_appId=app&cityid=${cityId}&seriesIds=${seriesId}&isextends=1&_sourceid=series_app&isNeedUnSale=1",
            beta = "http://dealer.api.lq.autohome.com.cn/statistics/seriesprice/getSeriesMinPriceExtends?_appId=app&cityid=${cityId}&seriesIds=${seriesId}&isextends=1&_sourceid=series_app&isNeedUnSale=1",
            online = "http://dealer.api.lq.autohome.com.cn/statistics/seriesprice/getSeriesMinPriceExtends?_appId=app&cityid=${cityId}&seriesIds=${seriesId}&isextends=1&_sourceid=series_app&isNeedUnSale=1",
            timeout = 3000
    )
    CompletableFuture<BaseModel<List<DealerSeriesCanAskPriceResult>>> getDealerSeriesCanAskPrice(int cityId, int seriesId);

    @AutoGet(dev = "http://export.dealer.api.lq.autohome.com.cn/exportapi/rcmd/buym/rcm/getOrderRecommendSeriesSpec?_appid=app&cityId=${cityId}&seriesId=${seriesId}",
            beta = "http://export.dealer.api.lq.autohome.com.cn/exportapi/rcmd/buym/rcm/getOrderRecommendSeriesSpec?_appid=app&cityId=${cityId}&seriesId=${seriesId}",
            online = "http://export.dealer.api.lq.autohome.com.cn/exportapi/rcmd/buym/rcm/getOrderRecommendSeriesSpec?_appid=app&cityId=${cityId}&seriesId=${seriesId}",
            timeout = 1000)
    CompletableFuture<BaseModel<SeriesRecommendSpecResult>> getOrderRecommendSeriesSpec(int cityId, int seriesId);




    @AutoGet(
            dev = "http://export.dealer.api.lq.autohome.com.cn/exportapi/lq/icsuseropen/spec/listPriceHis?_appId=app&cityId=${cityId}&specId=${specId}",
            beta = "http://export.dealer.api.lq.autohome.com.cn/exportapi/lq/icsuseropen/spec/listPriceHis?_appId=app&cityId=${cityId}&specId=${specId}",
            online = "http://export.dealer.api.lq.autohome.com.cn/exportapi/lq/icsuseropen/spec/listPriceHis?_appId=app&cityId=${cityId}&specId=${specId}"
    )
    CompletableFuture<BaseModel<List<SpecCityPriceHisResult>>> getDealerListPriceHis(int cityId, int specId);

    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cps/getEditionDealers?_appId=app&cityId=${cityId}&orderType=${orderType}&skuId=${skuId}&seriesId=${seriesId}&specId=${specId}",
            beta = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cps/getEditionDealers?_appId=app&cityId=${cityId}&orderType=${orderType}&skuId=${skuId}&seriesId=${seriesId}&specId=${specId}",
            online = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cps/getEditionDealers?_appId=app&cityId=${cityId}&orderType=${orderType}&skuId=${skuId}&seriesId=${seriesId}&specId=${specId}",
            timeout = 200)
    CompletableFuture<BaseModel<List<CpsEditionDealersResult>>> getCpsEditionDealers(int cityId, int orderType, int skuId, int seriesId, int specId);

    @AutoGet(
            dev = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cpsoper/getCpsProductInfo?_appId=app&cityId=${cityId}&positionId=${positionId}&seriesId=${seriesId}&deviceId=${deviceid}",
            beta = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cpsoper/getCpsProductInfo?_appId=app&cityId=${cityId}&positionId=${positionId}&seriesId=${seriesId}&deviceId=${deviceid}",
            online = "http://dealer.api.lq.autohome.com.cn/dealerorderc/cpsoper/getCpsProductInfo?_appId=app&cityId=${cityId}&positionId=${positionId}&seriesId=${seriesId}&deviceId=${deviceid}",
            timeout = 200)
    CompletableFuture<BaseModel<CpsProductInfoResult>> getCpsProductInfo(int cityId, int positionId, int seriesId, String deviceid);
}
