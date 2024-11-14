package com.autohome.app.cars.apiclient.club;

import com.autohome.app.cars.apiclient.club.dtos.ClubLatelyScoreForCarResult;
import com.autohome.app.cars.apiclient.club.dtos.TestDriveCityResult;
import com.autohome.app.cars.apiclient.club.dtos.TestDriveResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface DriveApiClient {
    @AutoGet(
            dev = "http://drive.api.corpautohome.com/v2/series/${seriesId}/cities/${cityId}/test-drive?_appId=app",
            beta =  "http://drive.api.corpautohome.com/v2/series/${seriesId}/cities/${cityId}/test-drive?_appId=app",
            online =  "http://drive.api.corpautohome.com/v2/series/${seriesId}/cities/${cityId}/test-drive?_appId=app"
    )
    CompletableFuture<BaseModel<TestDriveResult>> getTestDrice(int seriesId, int cityId);


    @AutoGet(
            dev = "http://drive.api.corpautohome.com/v2/series/${seriesId}/cities?_appid=app&testDriveType=${testDriveType}",
            beta = "http://drive.api.corpautohome.com/v2/series/${seriesId}/cities?_appid=app&testDriveType=${testDriveType}",
            online = "http://drive.api.corpautohome.com/v2/series/${seriesId}/cities?_appid=app&testDriveType=${testDriveType}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<List<TestDriveCityResult>>> getSeriesDriveCities(int seriesId, int testDriveType);


}
