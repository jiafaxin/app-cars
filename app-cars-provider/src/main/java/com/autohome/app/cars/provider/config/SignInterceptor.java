package com.autohome.app.cars.provider.config;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.common.enums.AppIdEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class SignInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String appId = request.getParameter("appId");
        //pc m 不需要签名
        if(AppIdEnum.APP_ID_ENUM_PC.getAppId().equals(appId)){
            return true;
        }
        String sign = request.getParameter("sign");
        //
        if(StringUtils.isBlank(appId) || StringUtils.isBlank(AppIdEnum.getAppKey(appId)) || StringUtils.isBlank(sign)){
            String jsonObjectStr = JSONObject.toJSONString(this.getAppIdError(102));
            returnJson(response, jsonObjectStr);
            return false;
        }
        boolean verifySign = verifySign(request,AppIdEnum.getAppKey(appId),sign);
        //签名失败
        if(!verifySign){
            String jsonObjectStr = JSONObject.toJSONString(this.getAppIdError(101));
            returnJson(response, jsonObjectStr);
            return false;
        }
        return true;
    }

    private boolean verifySign(HttpServletRequest request,String appKey,String sign){
        TreeMap<String, Object> hm = new TreeMap<String, Object>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()){
            String key = parameterNames.nextElement();
            Object value = request.getParameter(key);
            if("sign".equals(key) || "autohomeua".equals(key) || "perscont".equals(key)){
            }else{
                hm.put(key,value);
            }
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

    /**
     * 写入客户端
     */
    private void returnJson(HttpServletResponse response, String json) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    private Map<String, Object> getAppIdError(int errorCode) {
        Map<String, Object> resultError = new HashMap<>();
        switch (errorCode){
            case 101:
                resultError.put("returncode", 101);
                resultError.put("message", "签名失败");
                break;
            case 102:
                resultError.put("returncode", 102);
                resultError.put("message", "缺少参数");
                break;
        }
        return resultError;
    }
}
