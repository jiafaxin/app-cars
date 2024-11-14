package com.autohome.app.cars.common.httpclient.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AutoGet {
    String online() default "";

    String beta() default "";

    String dev() default "";

    String charset() default "utf-8";

    int timeout() default 500;

    String authorization() default "";

    String userAgentHeader() default "";
}
