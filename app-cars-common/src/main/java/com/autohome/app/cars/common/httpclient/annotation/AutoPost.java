package com.autohome.app.cars.common.httpclient.annotation;

import com.autohome.app.cars.common.httpclient.PostType;

import java.lang.annotation.*;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AutoPost {
    String online() default "";

    String beta() default "";

    String dev() default "";

    String charset() default "utf-8";

    PostType type() default PostType.JSON;
    int timeout() default 500;


    String authorization() default "";

    String userAgentHeader() default "";
}
