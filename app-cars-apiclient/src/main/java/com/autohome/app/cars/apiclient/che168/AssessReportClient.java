package com.autohome.app.cars.apiclient.che168;

import com.autohome.app.cars.apiclient.che168.dtos.GetSeriesAssessPriceResult;
import com.autohome.app.cars.apiclient.che168.dtos.GetSeriesSpecListJumpInfoResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface AssessReportClient {
    /**
     * 二手车估值报告
     *  接口文档：https://doc.autohome.com.cn/docapi/page/share/share_104NeB4noxM
     *  维护人：王子安
     * @return
     */
    @AutoGet(
            dev = "http://apicone.cupid.autohome.com.cn/v2/valuationzr/getmodprice?_appid=app&cid=${cid}&seriesid=${seriesId}&specid=${specId}&deviceid=${deviceId}",
            beta = "http://apicone.cupid.autohome.com.cn/v2/valuationzr/getmodprice?_appid=app&cid=${cid}&seriesid=${seriesId}&specid=${specId}&deviceid=${deviceId}",
            online = "http://apicone.che168.com/v2/valuationzr/getmodprice?_appid=app&cid=${cid}&seriesid=${seriesId}&specid=${specId}&deviceid=${deviceId}"
    )
    CompletableFuture<BaseModel<GetSeriesAssessPriceResult>> getAssessPrice(int cid,  int seriesId, int specId, String deviceId);
}
