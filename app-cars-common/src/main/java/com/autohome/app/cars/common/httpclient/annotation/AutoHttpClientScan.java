package com.autohome.app.cars.common.httpclient.annotation;

import com.autohome.app.cars.common.httpclient.AutoHttpClientScannerRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AutoHttpClientScannerRegistrar.class})
public @interface AutoHttpClientScan {
    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

}
