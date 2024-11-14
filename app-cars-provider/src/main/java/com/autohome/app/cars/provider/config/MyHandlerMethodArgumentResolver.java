package com.autohome.app.cars.provider.config;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.util.JsonFormat;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MyHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class spc = parameter.getParameterType().getSuperclass();
        if(spc==null || spc.getName().equals("java.lang.Object")){
            return false;
        }
        return spc.isAssignableFrom(GeneratedMessageV3.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Method method = parameter.getParameterType().getMethod("newBuilder");
        if (method == null)
            return null;
        GeneratedMessageV3.Builder object = (GeneratedMessageV3.Builder) method.invoke(null);

        JSONObject jsonObject = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,String> reNames = new LinkedHashMap<>();
        for (String s : webRequest.getParameterMap().keySet()) {
            if(reNames.containsKey(s.toLowerCase())){
                continue;
            }
            reNames.put(s.toLowerCase(),s);
        }

        for (Descriptors.FieldDescriptor field : object.getDescriptorForType().getFields()) {
            String fieldName = field.getJsonName();
            String param = webRequest.getParameter(fieldName);
            if(param == null && reNames.containsKey(fieldName.toLowerCase())){
                param = webRequest.getParameter(reNames.get(fieldName.toLowerCase()));
            }
            if(param==null){
                continue;
            }
            Object value = null;
            try {
                 value = convert(param,field ,mapper);
            }catch (Exception e){
                log.error("当前请求的路径：{} ;当前字段为：{} ;类型为：{} ;当前的值为:{}",webRequest.getDescription(false),fieldName,field.getJavaType().name(),param);
            }
            if(value == null)
                continue;

            jsonObject.put(fieldName,value);
        }

        JsonFormat.parser().ignoringUnknownFields().merge(jsonObject.toString(), object);
        return object.build();
    }

    public Object convert(String value,Descriptors.FieldDescriptor field,ObjectMapper mapper) {
        Class clazz = null;
        switch (field.getJavaType().name()) {
            case "STRING":
                clazz = String.class;
                break;
            case "INT":
                clazz = Integer.class;
                break;
            case "LONG":
                clazz = Long.class;
                break;
            case "DOUBLE":
                clazz = Double.class;
                break;
        }
        if (clazz == null)
            return null;

        if(field.isRepeated()){
            JSONArray jsonArray = new JSONArray();
            for (String s : value.split(",")) {
                if(StringUtils.hasLength(s)){
                    jsonArray.add(mapper.convertValue(s, clazz));
                }
            }
            return jsonArray;
        }else{
            return mapper.convertValue(value, clazz);
        }
    }

    public JavaType getListJavaType(ObjectMapper mapper,Class clazz){
        return mapper.getTypeFactory().constructParametricType(List.class, clazz);
    }


}
