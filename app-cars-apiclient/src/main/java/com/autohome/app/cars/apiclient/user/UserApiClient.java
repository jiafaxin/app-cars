package com.autohome.app.cars.apiclient.user;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.autohome.app.cars.apiclient.user.dtos.UserAuthSeriesResult;
import com.autohome.app.cars.apiclient.user.dtos.UserDefaultCarResult;
import com.autohome.app.cars.apiclient.user.dtos.UserInfoResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

@AutoHttpClient
public interface UserApiClient {

    @AutoGet(
            dev = "http://app.user.api.autohome.com.cn/api/go_userInfo/getuserinfolist?_appid=app&useridlist=${userId}&fields=headimage,sex,nickname,newnickname,userid,cityname,adddate",
            beta = "http://app.user.api.autohome.com.cn/api/go_userInfo/getuserinfolist?_appid=app&useridlist=${userId}&fields=headimage,sex,nickname,newnickname,userid,cityname,adddate",
            online = "http://app.user.api.autohome.com.cn/api/go_userInfo/getuserinfolist?_appid=app&useridlist=${userId}&fields=headimage,sex,nickname,newnickname,userid,cityname,adddate"
    )
    CompletableFuture<BaseModel<List<UserInfoResult>>> getUserInfoList(int userId);

    @AutoGet(
            dev = "http://rzc.api.in.autohome.com.cn/api/CarOwnerCamp/SelecCarLevelsListByUserids?_appid=app&userids=${userId}",
            beta = "http://rzc.api.in.autohome.com.cn/api/CarOwnerCamp/SelecCarLevelsListByUserids?_appid=app&userids=${userId}",
            online = "http://rzc.api.in.autohome.com.cn/api/CarOwnerCamp/SelecCarLevelsListByUserids?_appid=app&userids=${userId}"
    )
    CompletableFuture<BaseModel<List<UserAuthSeriesResult>>> getUserAuthseries(int userId);

    @AutoGet(
            dev = "http://platform.app.in.autohome.com.cn/carserver/car/defaultautocarv2?_appid=app&uids=${userId}",
            beta = "http://platform.app.in.autohome.com.cn/carserver/car/defaultautocarv2?_appid=app&uids=${userId}",
            online = "http://platform.app.in.autohome.com.cn/carserver/car/defaultautocarv2?_appid=app&uids=${userId}"
    )
    CompletableFuture<BaseModel<List<UserDefaultCarResult>>> getUserDefaultCar(int userId);

    @AutoGet(
            dev = "http://app.user.api.autohome.com.cn/api/go_userInfo/getuserinfolist?_appid=app&useridlist=${userId}&fields=headimage,sex,nickname,newnickname,userid,cityname,adddate",
            beta = "http://app.user.api.autohome.com.cn/api/go_userInfo/getuserinfolist?_appid=app&useridlist=${userId}&fields=headimage,sex,nickname,newnickname,userid,cityname,adddate",
            online = "http://app.user.api.autohome.com.cn/api/go_userInfo/getuserinfolist?_appid=app&useridlist=${userId}&fields=headimage,sex,nickname,newnickname,userid,cityname,adddate"
    )
    CompletableFuture<BaseModel<List<UserInfoResult>>> batchGetUserInfoList(String userId);

    @AutoGet(
            dev = "http://rzc.api.in.autohome.com.cn/api/CarOwnerCamp/SelecCarLevelsListByUserids?_appid=app&userids=${userId}",
            beta = "http://rzc.api.in.autohome.com.cn/api/CarOwnerCamp/SelecCarLevelsListByUserids?_appid=app&userids=${userId}",
            online = "http://rzc.api.in.autohome.com.cn/api/CarOwnerCamp/SelecCarLevelsListByUserids?_appid=app&userids=${userId}"
    )
    CompletableFuture<BaseModel<List<UserAuthSeriesResult>>> batchGetUserAuthseries2(String userId);

    @AutoGet(
            dev = "http://platform.app.in.autohome.com.cn/carserver/car/defaultautocarv2?_appid=app&uids=${userId}",
            beta = "http://platform.app.in.autohome.com.cn/carserver/car/defaultautocarv2?_appid=app&uids=${userId}",
            online = "http://platform.app.in.autohome.com.cn/carserver/car/defaultautocarv2?_appid=app&uids=${userId}"
    )
    CompletableFuture<BaseModel<List<UserDefaultCarResult>>> batchGetUserDefaultCar2(String userId);


}
