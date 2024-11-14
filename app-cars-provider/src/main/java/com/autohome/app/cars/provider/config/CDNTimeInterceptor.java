package com.autohome.app.cars.provider.config;

import com.alibaba.fastjson2.JSON;
import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

@Slf4j
@Component
public class CDNTimeInterceptor implements HandlerInterceptor {

    @Value("${cdnConfigs:}")
    private String cdnConfigs;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String path = request.getRequestURI();
            HashMap<String, Integer> mapUrls = JsonUtil.toObject(cdnConfigs, HashMap.class);

            if (mapUrls.containsKey(path)) {
                response.setHeader("Cache-Control", "max-age=" + mapUrls.get(path));
            }

            response.setHeader("AppServer", StringUtils.substringAfter(getAllLocalHostIP(), "."));

            return true;
        } catch (Exception e) {
            log.error("CDNTimeInterceptor preHandle error", e);
            return true;
        }
    }

    /**
     * 服务器ip信息
     *
     * @return
     */
    public static String getAllLocalHostIP() {
        StringBuffer strBuff = new StringBuffer();

        try {
            Enumeration netInterfaces;
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces
                        .nextElement();
                Enumeration nii = ni.getInetAddresses();
                while (nii.hasMoreElements()) {
                    ip = (InetAddress) nii.nextElement();
                    if (ip.getHostAddress().indexOf(":") == -1) {
                        strBuff.append(ip.getHostAddress() + "|");

                    }
                }
            }
        } catch (SocketException e) {
        }
        return strBuff.toString();
    }
}