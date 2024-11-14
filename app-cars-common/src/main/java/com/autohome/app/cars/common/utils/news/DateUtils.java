package com.autohome.app.cars.common.utils.news;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    private static String[] parsePatterns = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM", "yyyy-MM-dd HH:mm:ss.SSS"};

    public DateUtils() {
    }

    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }

        return formatDate;
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        } else {
            try {
                return parseDate(str.toString(), parsePatterns);
            } catch (ParseException var2) {
                return null;
            }
        }
    }

    public static Date parseDateFromWithDateStr(String str) {
        return StringUtils.isNotEmpty(str) && str.length() == 21 ? new Date(Long.valueOf(str.substring(6, 19))) : null;
    }

    public static Date parseDateFromWithLocalDateStr(String str) {
        return StringUtils.isNotEmpty(str) && str.length() == 26 ? new Date(Long.valueOf(str.substring(6, 19))) : null;
    }

    public static Date parseDateFromWithUTCDateStr(String str) {
        return StringUtils.isNotEmpty(str) && str.length() >= 19 ? parseDate(str.substring(0, 19)) : null;
    }

    public static Date parseDateTime12(String str) {
        if (str == null) {
            return null;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
                return sdf.parse(str);
            } catch (ParseException var2) {
                return null;
            }
        }
    }

    public static long pastDays(Date date) {
        long t = (new Date()).getTime() - date.getTime();
        return t / 86400000L;
    }

    public static long pastHour(Date date) {
        long t = (new Date()).getTime() - date.getTime();
        return t / 3600000L;
    }

    public static long pastMinutes(Date date) {
        long t = (new Date()).getTime() - date.getTime();
        return t / 60000L;
    }

    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / 86400000L;
        long hour = timeMillis / 3600000L - day * 24L;
        long min = timeMillis / 60000L - day * 24L * 60L - hour * 60L;
        long s = timeMillis / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min * 60L;
        long sss = timeMillis - day * 24L * 60L * 60L * 1000L - hour * 60L * 60L * 1000L - min * 60L * 1000L - s * 1000L;
        return (day > 0L ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (double)((afterTime - beforeTime) / 86400000L);
    }

    public static String currentTimeAdd(double minutes) {
        long currentTime = Math.round((double)System.currentTimeMillis() + minutes * 60.0 * 1000.0);
        Date date = new Date(currentTime);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String cutTimeTAndZ(String time) {
        return time.toLowerCase().replace("t", " ").replace("z", "");
    }

    public static String formatRomDataTimeSelf(Date startTime) {
        String result = "";
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Calendar ct = Calendar.getInstance();
        ct.setTime(startTime);
        int interdays = (int) ((c.getTimeInMillis() - ct.getTimeInMillis()) / 86400000L);
        if (interdays <= 365) {
            result = ct.get(2) + 1 + "-" + ct.get(5);
        } else {
            result = ct.get(1) + "-" + (ct.get(2) + 1) + "-" + ct.get(5);
        }

        return result;
    }

    public static String convertDuration(Object obj) {
        BigDecimal big = null;
        if (obj.getClass() == String.class) {
            big = new BigDecimal(obj.toString());
        } else {
            big = new BigDecimal(Long.parseLong(obj.toString()));
        }

        int time = big.setScale(0, 4).intValue();
        if (time < 60) {
            return "00:" + stringType(time);
        } else {
            int mintues;
            int second;
            if (time < 3600) {
                mintues = time / 60;
                second = time % 60;
                return stringType(mintues) + ":" + stringType(second);
            } else {
                int hour = time / 3600;
                mintues = time % 3600 / 60;
                second = time % 3600 % 60;
                return stringType(hour) + ":" + stringType(mintues) + ":" + stringType(second);
            }
        }
    }

    private static String stringType(int value) {
        return value <= 9 ? "0" + value : value + "";
    }

    public static Date parseUTCTime(String utcTime) {
        Date date = null;
        if (StringUtils.isNotEmpty(utcTime)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                date = df.parse(utcTime);
            } catch (ParseException var4) {
                var4.printStackTrace();
            }
        }

        return date;
    }
}
