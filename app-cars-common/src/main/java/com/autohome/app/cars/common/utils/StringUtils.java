package com.autohome.app.cars.common.utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtils {
    public static final String EMPTY = "";
    public static boolean isNotEmpty(String cs) {
        return cs != null && cs.trim().length() > 0;
    }

    public static boolean isEmpty(String cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 实现C#中string.Format方法，因为MessageFormat.format处理数字时会带上格式，所以都转成了字符串
     * MessageFormat.format(" {0} {1} {2} {3}", a, b,"",sb)
     *
     * @param str  需要格式化的字符串
     * @param args
     * @return
     */

    public static String format(String str, Object... args) {
        return MessageFormat.format(str, Stream.of(args).map(Objects::toString).toArray());
    }
    public static List<Integer> splitToInt(String source) {
        return splitToInt(source, ",");
    }

    public static List<Integer> splitToInt(String source, String spliter) {
        if (StringUtils.isNotEmpty(source)) {
            List collect = (List) Arrays.stream(org.apache.commons.lang3.StringUtils.split(source, spliter)).filter((x) -> {
                return isInteger(x);
            }).map((x) -> {
                return Integer.valueOf(Integer.parseInt(x));
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    //判断字符串是date
    public static boolean isDate(String str,String pattern) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
        try {
            timeFormat.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int TryParseInt(String val, int def) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return def;
        }
    }

    public static double TryParseDouble(String val, double def) {
        try {
            double no = Double.valueOf(val);
            return no;
        } catch (Exception e) {
            //System.out.println(e);
            return def;
        }
    }

    public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        try {
            if (string == null || string.length() == 0) {
                return "";
            } else if (separator == null) {
                return "";
            } else {
                String str = string.toString();
                String sep = separator.toString();
                int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
                return -1 != pos && string.length() - 1 != pos ? str.substring(pos + separator.length()) : "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        try {
            if (string == null || string.length() == 0 || separator == null) {
                return "";
            } else {
                String str = string.toString();
                String sep = separator.toString();
                if (sep.isEmpty()) {
                    return "";
                } else {
                    int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
                    if (-1 == pos) {
                        return str;
                    } else {
                        return 0 == pos ? "" : str.substring(0, pos);
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
    }

}
