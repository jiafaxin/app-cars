package com.autohome.app.cars.job.common;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RunOn {
    RoomType type() default RoomType.MASTER;
}
