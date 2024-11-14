package com.autohome.app.cars.provider.config;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimplifyJson {
    String value() default "";
}
