package com.autohome.app.cars.provider.config;

import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Slf4j
@Aspect
public class SimplifyJsonUse {
    @After("@annotation(com.autohome.app.cars.provider.config.SimplifyJson)")
    public void after(JoinPoint joinPoint) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            if (method == null) {
                return;
            }
            SimplifyJson simplifyJson = method.getAnnotation(SimplifyJson.class);
            if (simplifyJson == null) {
                return;
            }

            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String pluginversion = request.getParameter("pluginversion");
            String version = simplifyJson.value();
            if (StringUtils.isEmpty(version) || StringUtils.isEmpty(pluginversion) || CommonHelper.isTakeEffectVersion(pluginversion, version)) {
                SimplifyJsonHolder.use();
            }
        }catch (Exception e) {
            log.error("设置简版json异常", e);
        }
    }
}
