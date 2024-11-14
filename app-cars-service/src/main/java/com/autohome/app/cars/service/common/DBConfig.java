package com.autohome.app.cars.service.common;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface DBConfig {

    String tableName();

    String[] uniqueKeys() default {};

}
