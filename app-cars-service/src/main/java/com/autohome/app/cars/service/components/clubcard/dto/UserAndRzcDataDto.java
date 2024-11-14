package com.autohome.app.cars.service.components.clubcard.dto;

import java.util.List;

import com.autohome.app.cars.apiclient.user.dtos.UserAuthSeriesResult;
import com.autohome.app.cars.apiclient.user.dtos.UserDefaultCarResult;
import com.autohome.app.cars.apiclient.user.dtos.UserInfoResult;
import com.autohome.app.cars.common.BaseModel;

/**
 * @author wbs
 * @date 2024/6/7
 */
public class UserAndRzcDataDto {

    BaseModel<List<UserInfoResult>> userInfo;

    BaseModel<List<UserAuthSeriesResult>> userRzc;

    BaseModel<List<UserDefaultCarResult>> userDefault;

    public BaseModel<List<UserInfoResult>> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(BaseModel<List<UserInfoResult>> userInfo) {
        this.userInfo = userInfo;
    }

    public BaseModel<List<UserAuthSeriesResult>> getUserRzc() {
        return userRzc;
    }

    public void setUserRzc(BaseModel<List<UserAuthSeriesResult>> userRzc) {
        this.userRzc = userRzc;
    }

    public BaseModel<List<UserDefaultCarResult>> getUserDefault() {
        return userDefault;
    }

    public void setUserDefault(BaseModel<List<UserDefaultCarResult>> userDefault) {
        this.userDefault = userDefault;
    }
}
