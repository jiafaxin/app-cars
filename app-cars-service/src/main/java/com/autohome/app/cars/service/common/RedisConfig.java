package com.autohome.app.cars.service.common;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RedisConfig {
    int timeout() default 604800;
    String keyVersion() default "";

    // 是否使用自定义的slot代替默认的slot
    boolean useCustomSlot() default false;
}
