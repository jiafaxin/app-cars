package com.autohome.app.cars.apiclient.owner;

import com.autohome.app.cars.apiclient.owner.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface OwnerApiClient {

    @AutoGet(
            dev = "http://ownermp.corpautohome.com/ownerserviceapi/instructions/getentry?seriesid=${seriesId}&sourceid=1&app_version=11.59.0",
            beta = "http://ownermp.corpautohome.com/ownerserviceapi/instructions/getentry?seriesid=${seriesId}&sourceid=1&app_version=11.59.0",
            online = "http://ownermp.corpautohome.com/ownerserviceapi/instructions/getentry?seriesid=${seriesId}&sourceid=1&app_version=11.59.0"
    )
    CompletableFuture<BaseModel<OwnerVideoInstructionsResult>> getSeriesVideoInstruction(int seriesId);


    @AutoGet(
            dev = "http://ownermp.corpautohome.com/ownerhotapi/usecar/portal/candy?seriesid=${seriesId}&pid=&cityid=&app_version=11.58.99",
            beta = "http://ownermp.corpautohome.com/ownerhotapi/usecar/portal/candy?seriesid=${seriesId}&pid=&cityid=&app_version=11.58.99",
            online = "http://ownermp.corpautohome.com/ownerhotapi/usecar/portal/candy?seriesid=${seriesId}&pid=&cityid=&app_version=11.58.99"
    )
    CompletableFuture<BaseModel<List<GaizhuangResult>>> getGaizhuang(int seriesId);


    @AutoGet(
            dev = "http://ownermp.corpautohome.com/ownerhotapi/usecar/portal/card?seriesid=${seriesId}&pid=${provinceId}&cityid=${cityId}",
            beta = "http://ownermp.thallo.corpautohome.com/ownerhotapi/usecar/portal/card?seriesid=${seriesId}&pid=${provinceId}&cityid=${cityId}",
            online = "http://ownermp.corpautohome.com/ownerhotapi/usecar/portal/card?seriesid=${seriesId}&pid=${provinceId}&cityid=${cityId}"
    )
    CompletableFuture<BaseModel<CardResult>> getCard(int seriesId, int provinceId, int cityId);

    @AutoGet(
            dev = "http://ownermp.corpautohome.com/ownerserviceapi/fun/getcard?seriesid=${seriesId}&cityid=${cityId}&level=${levelId}&app_version=11.64.0&tabid=0",
            beta = "http://ownermp.corpautohome.com/ownerserviceapi/fun/getcard?seriesid=${seriesId}&cityid=${cityId}&level=${levelId}&app_version=11.64.0&tabid=0",
            online = "http://ownermp.corpautohome.com/ownerserviceapi/fun/getcard?seriesid=${seriesId}&cityid=${cityId}&level=${levelId}&app_version=11.64.0&tabid=0",
            timeout = 250
    )
    CompletableFuture<BaseModel<PlayCarCardResult>> getPlayCarCard(int seriesId, int levelId, int cityId);

    @AutoGet(
            dev = "http://ownermp.corpautohome.com/ownerserviceapi/usecar/portal/card?seriesid=${seriesId}&pid=${pid}&cityid=${cityId}&app_version=11.64.0",
            beta = "http://ownermp.corpautohome.com/ownerserviceapi/usecar/portal/card?seriesid=${seriesId}&pid=${pid}&cityid=${cityId}&app_version=11.64.0",
            online = "http://ownermp.corpautohome.com/ownerserviceapi/usecar/portal/card?seriesid=${seriesId}&pid=${pid}&cityid=${cityId}&app_version=11.64.0",
            timeout = 200
    )
    CompletableFuture<BaseModel<UseCarCardResult>> getUseCarCard(int seriesId, int pid, int cityId);
}
