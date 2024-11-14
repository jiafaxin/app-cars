package com.autohome.app.cars.common.enums;


public enum AppIdEnum {
    APP_ID_ENUM_APP("app", "appToken"),
    APP_ID_ENUM_PC("pc", "pcToken");

    private String appId;
    private String appKey;

    AppIdEnum(String appId,String appKey){
        this.appId = appId;
        this.appKey = appKey;
    }

    public static String getAppKey(String appId) {
        AppIdEnum[] appIdEnums = values();
        for (AppIdEnum appIdEnum : appIdEnums) {
            if (appId.equals(appIdEnum.getAppId())) {
                return appIdEnum.getAppKey();
            }
        }
        return null;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
