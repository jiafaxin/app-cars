package com.autohome.app.cars.common.utils;

/**
 * @author zhangchengtao
 * @date 2024/4/30 14:29
 */
public class StrUtil {

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.isEmpty();
    }


    public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : "";
        } else if (separator == null) {
            return "";
        } else {
            String str = string.toString();
            String sep = separator.toString();
            int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
            return -1 != pos && string.length() - 1 != pos ? str.substring(pos + separator.length()) : "";
        }
    }
}
