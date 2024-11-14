package com.autohome.app.cars.service.common;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.util.JsonFormat;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MessageUtil {
    public static <T> T toMessage(Object obj, Class<T> messageClass) {
        if (obj == null)
            return null;
        try {
            String json = JsonUtil.toString(obj);
            Method method = messageClass.getMethod("newBuilder");
            GeneratedMessageV3.Builder object = (GeneratedMessageV3.Builder) method.invoke(null);
            JsonFormat.parser().ignoringUnknownFields().merge(json, object);
            return (T) object.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> List<T> toMessageList(List list, Class<T> messageClass) {
        List<T> objectList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return objectList;
        }
        for (Object obj : list) {
            objectList.add(toMessage(obj, messageClass));
        }
        return objectList;
    }
}
