package com.autohome.app.cars.provider.basic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CdnConfig {
    @Value("${cdnConfigs:}")
    private String cdnConfigs;

    public String getCdnConfigs() {
        return cdnConfigs;
    }
}
