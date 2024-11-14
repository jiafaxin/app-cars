package com.autohome.app.cars.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
@Slf4j
public class ClientSignUtil {

    public ClientSignUtil() {
    }


    public static String createSign(Map<String, String> paramsMap, String appKey) {
        SortedMap<String, String> p = new TreeMap<>(paramsMap);
        List<String> keys = new ArrayList<>(p.keySet());
        StringBuilder sb = new StringBuilder();
        sb.append(appKey);

        for (String s : keys) {
            Object value = p.get(s);
            if (!s.equals("sign")) {
                sb.append(s).append(value.toString());
            }
        }

        sb.append(appKey);
        StringBuilder buf = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(sb.toString().getBytes(StandardCharsets.UTF_8));

            byte[] b = md.digest();

            for (int j : b) {
                int i = j;
                if (i < 0) {
                    i += 256;
                }

                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException var13) {
            log.warn("计算签名异常",var13);
        }
        return buf.toString().toUpperCase();
    }
}
