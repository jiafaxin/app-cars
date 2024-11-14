package com.autohome.app.cars.provider.controller;

import com.autohome.app.cars.common.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class MethodInvokeService {

    @Autowired
    private ApplicationContext applicationContext;

    public Object invokeMethod(HttpServletRequest request) throws Exception {
        //获取组件名
        String name = request.getParameter("component");
        String methodName = StringUtils.hasLength(request.getParameter("method")) ? request.getParameter("method") : "get";

        if (!StringUtils.hasLength(name)) {
            return new BaseModel(-1, "组件名不能为空");
        }
        Object bean = applicationContext.getBean(name + "Component");

        if (!StringUtils.hasLength(methodName)) {
            return new BaseModel(-1, "方法名不能为空");
        }

        //特殊方法名
        if (methodName.contains("refresh") || methodName.contains("update") || methodName.contains("delete")) {
            return new BaseModel(-1, "方法禁止调用");
        }

        //有效参数
        TreeMap<String, String[]> treeMap = new TreeMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (!Arrays.asList("_appid", "component", "method").contains(k)) {
                treeMap.put(k, v);
            }
        });

        //根据参数找到对应方法
        Method method = findMethod(bean, methodName, treeMap);
        if (method == null) {
            return new BaseModel(-1, "方法不存在");
        }

        //获取方法的参数类型
        Parameter[] parameters = method.getParameters();

        //构建参数值数组
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            args[i] = convertType(parameters[i].getType(), treeMap.get(parameters[i].getName()));
        }

        //反射调用方法
        Object invoke = method.invoke(bean, args);

        //检查结果是否是CompletableFuture
        if (invoke instanceof CompletableFuture) {
            CompletableFuture<?> future = (CompletableFuture<?>) invoke;

            Object obj = future.get();
            return new BaseModel(obj);
        }
        return new BaseModel(invoke);
    }

    private Method findMethod(Object bean, String methodName, Map<String, String[]> params) {
        for (Method method : bean.getClass().getMethods()) {
            if (method.getName().equals(methodName) && parametersMatch(method.getParameters(), params)) {
                return method;
            }
        }
        return null;
    }

    private boolean parametersMatch(Parameter[] parameters, Map<String, String[]> params) {
        if (parameters.length != params.size()) {
            return false;
        }

        //检查每个参数名称是否存在于传入的参数中
        for (Parameter parameter : parameters) {
            if (!params.containsKey(parameter.getName())) {
                return false;
            }
        }
        return true;
    }

    private Object convertType(Class<?> type, String[] values) {
        if (type == String.class) {
            return values[0];
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(values[0]);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(values[0]);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(values[0]);
        } else if (type == List.class) {
            return convertToList(values);
        }
        return null;
    }

    private Object convertToList(String[] values) {
        if (values.length == 0 || (values.length == 1 && com.autohome.app.cars.common.utils.StringUtils.isEmpty(values[0]))) {
            return new ArrayList<>();
        }

        String firstValue = values[0];
        if (isInteger(firstValue)) {
            List<Integer> list = new ArrayList<>();
            for (String value : values) {
                list.add(Integer.parseInt(value));
            }
            return list;
        }

        List<String> list = new ArrayList<>();
        for (String value : values) {
            list.add(value);
        }
        return list;
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
