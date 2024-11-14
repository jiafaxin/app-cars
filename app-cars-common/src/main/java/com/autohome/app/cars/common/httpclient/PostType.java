package com.autohome.app.cars.common.httpclient;

public enum PostType {
    /**
     * PostBody必须为String，或者可被序列化为JSON的对象
     */
    JSON,

    /**
     * PostBody必须为Map
     */
    X_WWW_FORM_URLENCODED
}
