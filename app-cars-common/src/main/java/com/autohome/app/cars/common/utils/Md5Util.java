package com.autohome.app.cars.common.utils;

import com.autohome.app.cars.common.enums.AppIdEnum;
import com.google.protobuf.Descriptors;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerScopeUtils;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Md5Util {

    public static String get(Object obj){
        String json = JsonUtil.toString(obj);
        return DigestUtils.md5DigestAsHex(json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 给中间层验签用
     * @param
     * @return
     */
    public static boolean verifySign(TreeMap<String, Object> hm,String sign,String appKey){
        //pc m 不需要签名
        if(AppIdEnum.APP_ID_ENUM_PC.getAppKey().equals(appKey)){
            return true;
        }
        //如果没有传appId或者不存在的appId 或者没有传加密串
        if(StringUtils.isBlank(appKey) || StringUtils.isBlank(sign)){
            return false;
        }
        StringBuilder params = new StringBuilder();
        Iterator var4 = hm.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, Object> me = (Map.Entry)var4.next();
            params.append(me.getKey()).append(me.getValue());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(appKey);
        sb.append(params);
        sb.append(appKey);
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8)).toUpperCase();
        if(md5DigestAsHex.equalsIgnoreCase(sign)){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        long timestamp = 1720438060;
        String appId = "app";
        String appKey = AppIdEnum.getAppKey(appId);
        TreeMap<String, Object> hm = new TreeMap<String, Object>();
        hm.put("timestamp",timestamp);
        hm.put("appId",appId);
        hm.put("brandId",3);
        StringBuilder params = new StringBuilder();
        Iterator var4 = hm.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, Object> me = (Map.Entry)var4.next();
            params.append(me.getKey()).append(me.getValue());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(appKey);
        sb.append(params);
        sb.append(appKey);
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8)).toUpperCase();
        System.out.println(md5DigestAsHex);
    }
}
