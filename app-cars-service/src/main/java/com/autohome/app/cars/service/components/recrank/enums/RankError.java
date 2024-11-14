package com.autohome.app.cars.service.components.recrank.enums;

/**
 * 排行榜异常枚举
 */
public enum RankError {
    SOURCE_EMPTY_ERROR("101", "业务组合筛选条件正常，但源接口数据本无数据。"),
    FILTER_CRITERIA_ERROR("102", "业务筛选条件校验异常"),
    PUBLIC_PARAMETERS_MISSING_ERROR("103", "客户端缺少公共参数"),
    CLIENT_NETWORK_ERROR("104", "客户端本地网络错误异常"),
    SOURCE_INTERFACE_ERROR("105", "服务端源接口异常"),
    PACKAGING_INTERFACE_ERROR("106", "服务端包装接口异常"),
    OTHER_ERROR("107", "其他错误");


    /**
     * 错误码
     */
    public String error_code;
    /**
     * 错误原因
     */
    public String reason;

    RankError(String error_code, String reason) {
        this.error_code = error_code;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "{" +
                "error_code='" + error_code + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
