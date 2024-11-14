package com.autohome.app.cars.common.utils;

import org.json.JSONObject;

public class JSONObjectUtil {

    public static JSONObject union(JSONObject obj, JSONObject obj2) {
        if(obj==null)
            return obj2;
        if(obj2==null)
            return obj;

        obj2.toMap().forEach((k,v)->{
            obj.put(k,v);
        });
        return obj;
    }

}
