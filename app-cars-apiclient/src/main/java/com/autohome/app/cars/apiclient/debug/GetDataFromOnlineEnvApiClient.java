package com.autohome.app.cars.apiclient.debug;

import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface GetDataFromOnlineEnvApiClient {
    @AutoGet(
            dev = "http://car.app.corpautohome.com/v2/getComponentValue?component=${component}&method=${method}${ext}",
            beta = "http://car.app.corpautohome.com/v2/getComponentValue?component=${component}&method=${method}${ext}",
            online = "http://car.app.corpautohome.com/v2/getComponentValue?component=${component}&method=${method}${ext}",
            timeout = 3000
    )
    CompletableFuture<?> getDataFromOnline(String component, String method, String ext);
}