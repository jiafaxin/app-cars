package com.autohome.app.cars.common.httpclient;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AutoHttpClientScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(AutoHttpClientScan.class.getName()));
        if (mapperScanAttrs != null) {
            List<String> basePackages = new ArrayList<>();

            basePackages.addAll(Arrays.stream(mapperScanAttrs.getStringArray("basePackages")).filter(StringUtils::hasText)
                    .collect(Collectors.toList()));

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AutoHttpClientScannerConfigurer.class);
            builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
            registry.registerBeanDefinition(generateBaseBeanName(importingClassMetadata, 0), builder.getBeanDefinition());
        }
    }

    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
        return importingClassMetadata.getClassName() + "#" + AutoHttpClientScannerRegistrar.class.getSimpleName() + "#" + index;
    }
}
