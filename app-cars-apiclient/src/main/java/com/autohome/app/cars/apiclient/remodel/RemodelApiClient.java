package com.autohome.app.cars.apiclient.remodel;

import com.autohome.app.cars.apiclient.remodel.dtos.Remodel3DResult;
import com.autohome.app.cars.apiclient.remodel.dtos.RemodelCoversResult;
import com.autohome.app.cars.apiclient.vr.dtos.CockpitVrResult;
import com.autohome.app.cars.apiclient.vr.dtos.SpecVrInfoResult;
import com.autohome.app.cars.apiclient.vr.dtos.VrSuperCarResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 改装车接口 -- 李李佳
 */
@AutoHttpClient
public interface RemodelApiClient {

    @AutoGet(
            dev = "http://api-remodel.corpautohome.com/3D/remodel/getInfoBySeries?seriesId=${seriesid}&source=7&platform=1&version=11.61.5",
            beta = "http://api-remodel.corpautohome.com/3D/remodel/getInfoBySeries?seriesId=${seriesid}&source=7&platform=1&version=11.61.5",
            online = "http://api-remodel.corpautohome.com/3D/remodel/getInfoBySeries?seriesId=${seriesid}&source=7&platform=1&version=11.61.5",
            timeout = 3000
    )
    CompletableFuture<BaseModel<Remodel3DResult>> getInfoBySeries(int seriesid);


    @AutoGet(
            dev = "http://la.corpautohome.com/usecar/app_usercar_refit_cover_batch?_appid=basecar&pageindex=${pageindex}&pagesize=${pagesize}",
            beta = "http://la.corpautohome.com/usecar/app_usercar_refit_cover_batch?_appid=basecar&&pageindex=${pageindex}&pagesize=${pagesize}",
            online = "http://la.corpautohome.com/usecar/app_usercar_refit_cover_batch?_appid=basecar&&pageindex=${pageindex}&pagesize=${pagesize}",
            timeout = 3000
    )
    CompletableFuture<RemodelCoversResult> getAppUsercarRefitCover(int pageindex, int pagesize);

}
