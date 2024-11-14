package com.autohome.app.cars.common.httpclient.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AutoCache {

    /**
     * 存活时长，当原接口挂掉时，存活多久
     * 单位：秒
     *
     * @return
     */
    int liveTime() default 300;

    /**
     * 缓存有效时长，每隔多久更新
     * 单位秒，默认5分钟
     *
     * @return
     */
    int effectiveTime() default 300;


    String v() default "";
}
