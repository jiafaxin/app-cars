package com.autohome.app.cars.common.utils;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufUtil {
    private final static Logger logger = LoggerFactory.getLogger(ProtobufUtil.class);

    public static void merge(String json, Message.Builder builder){
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(json,builder);
        }catch (Exception e) {
            logger.error("merge error", e);
        }
    }

    public static void merge(Object obj, Message.Builder builder){
        merge(JsonUtil.toString(obj),builder);
    }
}
