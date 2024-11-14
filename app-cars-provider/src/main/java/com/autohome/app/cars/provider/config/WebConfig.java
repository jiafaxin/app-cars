package com.autohome.app.cars.provider.config;

import autohome.rpc.car.app_cars.v1.subsidy.SubsidyPolicyReportResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Descriptors;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CDNTimeInterceptor cdnTimeInterceptor;

    @Autowired
    private SignInterceptor signInterceptor;


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        // 设置 URL 忽略大小写
        pathMatcher.setCaseSensitive(false);
        configurer.setPathMatcher(pathMatcher);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MyHandlerMethodArgumentResolver());
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        SimpleModule simpleModule = new SimpleModule();
        JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry.newBuilder()
                .add(SubsidyPolicyReportResponse.Result.Card30101.getDescriptor())
                .add(SubsidyPolicyReportResponse.Result.Card30102.getDescriptor())
                .add(SubsidyPolicyReportResponse.Result.Card30103.getDescriptor())
                .add(SubsidyPolicyReportResponse.Result.Card30105.getDescriptor())
                .build();
        simpleModule.addSerializer(MessageOrBuilder.class, new JsonSerializer<MessageOrBuilder>() {
            @Override
            public void serialize(MessageOrBuilder messageOrBuilder, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if (SimplifyJsonHolder.isUse()) {
                    Descriptors.FieldDescriptor returnCode = messageOrBuilder.getDescriptorForType().getFields().get(0);
                    Set<Descriptors.FieldDescriptor> idvfs = new HashSet<>();
                    idvfs.add(returnCode);
                    jsonGenerator.writeRawValue(JsonFormat.printer().includingDefaultValueFields(idvfs).omittingInsignificantWhitespace().usingTypeRegistry(typeRegistry).print(messageOrBuilder));
                } else {
                    jsonGenerator.writeRawValue(JsonFormat.printer().usingTypeRegistry(typeRegistry).includingDefaultValueFields().omittingInsignificantWhitespace().print(messageOrBuilder));
                }
            }
        });
        ObjectMapper objectMapper = builder.build();
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cdnTimeInterceptor);
        registry.addInterceptor(signInterceptor).addPathPatterns("/carMiddle/*");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 允许的源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许的头信息
                .allowCredentials(true); // 是否允许发送Cookie
    }

//
//    @Autowired
//    private EncodingInterceptor encodingInterceptor;
//
//    @Autowired
//    JsonpInterceptor jsonpInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(encodingInterceptor);
//        registry.addInterceptor(jsonpInterceptor);
//    }

}
