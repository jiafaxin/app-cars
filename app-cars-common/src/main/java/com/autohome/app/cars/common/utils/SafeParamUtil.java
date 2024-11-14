package com.autohome.app.cars.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SafeParamUtil {

    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
    private static final String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符


    /**
     * @param htmlStr
     * @return
     *  删除Html标签
     */
    public static String delHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
        htmlStr = htmlStr.replaceAll("-->", "");
        htmlStr = htmlStr.replaceAll("<!--.*", "");
        htmlStr = htmlStr.replaceAll("&(quot|#34);", "\"");
        htmlStr = htmlStr.replaceAll("&(amp|#38);", "&");
        htmlStr = htmlStr.replaceAll("&(lt|#60);", "<");
        htmlStr = htmlStr.replaceAll("&(gt|#62);", ">");
        htmlStr = htmlStr.replaceAll("&(nbsp|#160);", " ");
        htmlStr = htmlStr.replaceAll("&(iexcl|#161);", "¡");
        htmlStr = htmlStr.replaceAll("&(cent|#162);", "¢");
        htmlStr = htmlStr.replaceAll("&(pound|#163);", "£");
        htmlStr = htmlStr.replaceAll("&(copy|#169);", "©");
        htmlStr = htmlStr.replaceAll("<", "");
        htmlStr = htmlStr.replaceAll(">", "");
        htmlStr = htmlStr.replaceAll("\r\n", "");
        return htmlStr.trim(); // 返回文本字符串
    }



    /**
     * 将字符串"/Date(1423872957960)/"转换为date对象;
     * @param str
     * @return
     */
    public static Date parseDateFromWithDateStr(String str){
        if (StringUtils.isNotEmpty(str)) {
            if (str.length() == 21) {
                return new Date(Long.valueOf(str.substring(6, 19)));
            }
        }
        return null;
    }
    /**
     * 对象转换成整型
     *
     * @param obj
     *            对象
     * @param defaultValue
     *            默认值
     * @return
     */
    public static int toSafeInt(Object obj, Integer defaultValue) {
        if (defaultValue == null) {
            defaultValue = -1;
        }
        if (obj == null || "".equals(obj.toString().trim())) {
            return defaultValue;
        }
        String str = obj.toString().trim().toLowerCase();
        if ("true".equals(str)) {
            return 1;
        }
        if ("false".equals(str)) {
            return 0;
        }
        try {
            int i = Integer.parseInt(str);
            return i;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
    /**
     * 对象转换成整型
     *
     * @param obj
     *            对象
     * @param defaultValue
     *            默认值
     * @return
     */
    public static Long toSafeLong(Object obj, Long defaultValue) {
        if (defaultValue == null) {
            defaultValue = -1L;
        }
        if (obj == null || "".equals(obj.toString().trim())) {
            return defaultValue;
        }
        String str = obj.toString().trim().toLowerCase();
        if ("true".equals(str)) {
            return 1L;
        }
        if ("false".equals(str)) {
            return 0L;
        }
        try {
            Long i = Long.parseLong(str);
            return i;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
    /**
     * 对象转换成整型
     *
     * @param obj
     *            对象
     * @param
     *
     * @return
     */
    public static Long toSafeLong(Object obj) {
        return toSafeLong(obj,0L);
    }


    /**
     * 对象转换成整型
     *
     * @param obj
     *            对象
     * @return
     */
    public static int toSafeInt(Object obj) {
        return toSafeInt(obj, -1);
    }

    /**
     * 对象转换成字符串
     *
     * @param obj
     *            对象
     * @param defaultValue
     *            默认值
     * @return
     */
    public static String toSafeString(Object obj, String defaultValue) {
        if (defaultValue == null) {
            defaultValue = "";
        }
        if (obj == null) {
            return defaultValue;
        }
        return obj.toString();
    }

    /**
     * 对象转换成字符串
     *
     * @param obj
     *            对象
     * @return
     */
    public static String toSafeString(Object obj) {
        return toSafeString(obj, "");
    }


    /**
     * 对象安全转变为日期
     *
     * @param obj
     *            对象
     * @param defaultValue
     *            默认值
     * @return
     */
    public static Date toSafeDateTime(Object obj, String defaultValue) {
        if (defaultValue == null) {
            defaultValue = "1900-01-01 00:00:00";
        }
        Date defaultDate = DateUtil.parse(defaultValue,"yyyy-MM-dd HH:mm:ss");

        if (obj == null || DateUtil.parse(obj.toString(),"yyyy-MM-dd HH:mm:ss") == null) {
            return defaultDate;
        }

        return DateUtil.parse(obj.toString(),"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 对象安全转变为日期
     *
     * @param obj
     *            对象
     * @return
     */
    public static Date toSafeDateTime(Object obj) {
        return toSafeDateTime(obj, "1900-01-01 00:00:00");
    }

    /**
     * 数字转为以万为单位的字符串
     *
     * @param num
     * @return
     */
    public static String convertToWan(int num) {
        String str = "";
        if (num > 10000) {
            if (num % 10000 == 0) {
                str = (num / 10000) + "万";
            } else {
                DecimalFormat df = new DecimalFormat("0.0");
                str = df.format(num / 10000.0) + "万";
            }
        } else {
            str = num + "";
        }
        return str;
    }

    /**
     * 数字转为以中文【万】为单位的字符串
     *
     * @param num
     * @return
     */
    public static String convertToWanEnd2(int num) {
        return convertToWan(num, true);
    }
    /**
     * 数字转为以【万/w】为单位的字符串
     * @param num 数字
     * @param showChineseUnit 是否显示中文单位 true - "万"；false - "w"
     * @return
     */
    public static String convertToWan(Integer num,boolean showChineseUnit) {
        String unit = showChineseUnit ? "万" : "w";
        String str = "0";
        if (SafeParamUtil.toSafeInt(num, -1) > 0) {
            if (num > 10000) {
                if (num % 10000 == 0) {
                    str = (num / 10000) + unit;
                } else {
                    DecimalFormat df = new DecimalFormat("0.0");
                    str = df.format(num / 10000.0) + unit;
                }
            } else {
                str = num + "";
            }
        }
        return str;
    }

    /**
     * 转换视频的播放时长： 时:分:秒
     *
     * @param second
     * @return
     */
    public static String convertVideoPlayTime(int second) {
        if (second > 0) {
            if (second < 3600) {
                return second > 0 ? String.format("%02d", second / 60) + ":"
                        + String.format("%02d", second % 60) : "0:00";
            } else {
                return second / (60 * 60) + ":"
                        + String.format("%02d", (second % (60 * 60)) / 60)
                        + ":" + String.format("%02d", second % 60);
            }
        } else {
            return "00:00";
        }
    }

    /**
     * 转换为安全的boolean类型
     *
     * @param o
     * @return
     */
    public static boolean toSafeBoolean(Object o) {
        Boolean bs = false;
        boolean defaultV = false;
        if (o == null || "".equals(o.toString().trim())) {
            return defaultV;
        }
        String s = o.toString().trim().toLowerCase();
        if ("true".equals(s) || "1".equals(s)) {
            return true;
        }
        if ("false".equals(s) || "0".equals(s)) {
            return false;
        }
        try {
            bs = Boolean.parseBoolean(s);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultV;
        }
        return bs;
    }

    /**
     * 处理直播预告时间
     *
     * @param
     * @return
     */
//    public static String convertLiveStartTime(String startTime) {
//        String result = "";
//        if (!StringUtils.isEmpty(startTime)) {
//            Date st = DateUtils.parseDate(startTime); // toSafeDateTime(startTime);//
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            SimpleDateFormat formatHHmm = new SimpleDateFormat("HH:mm");
//            SimpleDateFormat formatMdHm = new SimpleDateFormat("MM-dd HH:mm");
//            String formatHHmmRes = formatHHmm.format(st);
//            String formatMdHmRes = formatMdHm.format(st);
//            String nowDate = format.format(new Date());
//            Date curDate = null;
//            String stDateStr = DateUtils.formatDate(st, "yyyy-MM-dd");
//            String tomorrow = "";
//            try {
//                curDate = format.parse(nowDate);
//                tomorrow = DateUtils.formatDate(DateUtils.addDays(curDate, 1),
//                        "yyyy-MM-dd");
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            if (stDateStr.equals(nowDate)) {
//                result = "今天" + formatHHmmRes;
//            } else if (stDateStr.equals(tomorrow)) {
//                result = "明天" + formatHHmmRes;
//            } else {
//                result = formatMdHmRes;
//            }
//        }
//        return result;
//    }


    /**
     * 格式化字符串
     *
     * @param pubTime
     * @return
     */
    public static String getTimeStr(String pubTime) {
        return StringUtils.isEmpty(pubTime) ? "" : pubTime.substring(0,
                pubTime.indexOf("+")).trim();
    }

    /*
     * public static void main(String[] args) { String res = convertToWan(100);
     * System.out.println(res);
     *
     * convertLiveStartTime("2017-3-23 17:00:00"); }
     */

    /**
     * 对象转换成四舍五入整型
     *
     * @param obj
     *            对象
     * @param defaultValue
     *            默认值
     * @return
     */
    public static int strToRoundInt(Object obj, Integer defaultValue) {
        if (defaultValue == null) {
            defaultValue = -1;
        }
        if (obj == null || "".equals(obj.toString().trim())) {
            return defaultValue;
        }
        String str = obj.toString().trim().toLowerCase();
        if ("true".equals(str)) {
            return 1;
        }
        if ("false".equals(str)) {
            return 0;
        }
        try {
            int i = Math.round(Float.parseFloat(str));
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
