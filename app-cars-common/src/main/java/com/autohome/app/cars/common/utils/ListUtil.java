package com.autohome.app.cars.common.utils;

import java.util.List;

public class ListUtil {

    public static <T> void addIfNotNull(List<T> list, T item){
        if(item==null)
            return;
        list.add(item);
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

}
