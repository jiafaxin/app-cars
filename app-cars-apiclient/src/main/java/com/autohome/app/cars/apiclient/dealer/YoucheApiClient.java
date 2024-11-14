package com.autohome.app.cars.apiclient.dealer;

import com.autohome.app.cars.apiclient.dealer.dtos.HomeDealerListResult;
import com.autohome.app.cars.apiclient.dealer.dtos.MoreDealerListCountResult;
import com.autohome.app.cars.apiclient.dealer.dtos.NewRepairFactoryResult;
import com.autohome.app.cars.apiclient.dealer.dtos.RepairFactoryResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface YoucheApiClient {

    @AutoGet(
            dev = "http://api.youche.in.autohome.com.cn/dealer/getMoreDealerListCount?_appid=app&cityId=${cityId}&seriesId=${seriesId}",
            beta = "http://api.youche.in.autohome.com.cn/dealer/getMoreDealerListCount?_appid=app&cityId=${cityId}&seriesId=${seriesId}",
            online = "http://api.youche.in.autohome.com.cn/dealer/getMoreDealerListCount?_appid=app&cityId=${cityId}&seriesId=${seriesId}"
    )
    CompletableFuture<BaseModel<MoreDealerListCountResult>> getMoreDealerListCount(int seriesId,int cityId);

    @AutoGet(
            dev = "http://api.youche.in.autohome.com.cn/dealer/getHomeDealerList?_appid=app&cityId=${cityId}&seriesId=${seriesId}&longitude=${longitude}&latitude=${latitude}&needMoreLink=1",
            beta = "http://api.youche.in.autohome.com.cn/dealer/getHomeDealerList?_appid=app&cityId=${cityId}&seriesId=${seriesId}&longitude=${longitude}&latitude=${latitude}&needMoreLink=1",
            online = "http://api.youche.in.autohome.com.cn/dealer/getHomeDealerList?_appid=app&cityId=${cityId}&seriesId=${seriesId}&longitude=${longitude}&latitude=${latitude}&needMoreLink=1",
            timeout = 200
    )
    CompletableFuture<BaseModel<HomeDealerListResult>> getHomeDealerList(int seriesId, int cityId, double longitude, double latitude);

    @AutoGet(
            dev = "http://cfw-crp.api.corpautohome.com/app/dealer/list2tabinfo?cityId=${cityId}&seriesId=${seriesId}&longitude=${longitude}&latitude=${latitude}&from=series_page&source=221111406.RN004",
            beta = "http://cfw-crp.api.corpautohome.com/app/dealer/list2tabinfo?cityId=${cityId}&seriesId=${seriesId}&longitude=${longitude}&latitude=${latitude}&from=series_page&source=221111406.RN004",
            online = "http://cfw-crp.api.corpautohome.com/app/dealer/list2tabinfo?cityId=${cityId}&seriesId=${seriesId}&longitude=${longitude}&latitude=${latitude}&from=series_page&source=221111406.RN004",
            timeout = 300
    )
    CompletableFuture<BaseModel<List<NewRepairFactoryResult>>> newGetRepairFactoryList(int cityId, int seriesId, String longitude, String latitude);
}
