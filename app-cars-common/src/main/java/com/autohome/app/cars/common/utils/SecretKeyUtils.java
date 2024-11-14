package com.autohome.app.cars.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SecretKeyUtils {

    /**
     * 接口网关公钥（即申请时的应用名称）
     */
    private static final String openkeys = "app-newsvideo-api";

    /**
     * 网关头信息固定字符串
     */
    private static final String commprex = "Basic";

    /**
     * 接口网关头信息名称
     */
    private static final String headerName = "Authorization";


    private static final Map<String,String> SecretKeyMap = new HashMap<String, String>(){
        {
            put("oneapi_v2", "ESley$CaUx");
        }
    };

    public static Map<String,String> getSecretKey(String apiName) {
        Map<String,String> map = new HashMap<>();
        try {
            String val = "";
            String temp = "";
            if (SecretKeyMap.containsKey(apiName)) {
                temp = openkeys + ":" + SecretKeyMap.get(apiName);
                String temp_Base64 = new String(Base64.encodeBase64(temp.getBytes("UTF-8")));
                val = commprex + " " + temp_Base64;
            }
            map.put(headerName, val);
        } catch (UnsupportedEncodingException ex) {
            log.error("getSecretKey方法错误:{}",ex);
        }
        return map;
    }
}
