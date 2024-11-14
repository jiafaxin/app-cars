package com.autohome.app.cars.common.utils.news;

import com.autohome.app.cars.common.utils.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateHelper extends DateUtils {
    /**
     * 获取某天最大时间：如2023-01-01 23:59:59
     *
     * @param date
     * @return
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        ;
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取某天最小时间：如2023-01-01 00:00:00
     *
     * @param date
     * @return
     */
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    public static Date getNow() {
        return Calendar.getInstance().getTime();
    }

    /**
     * <span>计算某指定日期，增加指定天数后的日期</span>
     *
     * @param baseDate 基础日期
     * @param amount   增加的天数，负数为计算指定日期之前的天数。单位：天
     * @return
     */
    public static Date plusDays(Date baseDate, int amount) {
        Calendar date = Calendar.getInstance();
        date.setTime(baseDate);
        date.add(Calendar.DATE, amount);
        return date.getTime();
    }

    /**
     * 格式化发布时间
     *
     * @param publishDate 发布时间
     * @return
     */
    public static String formatPublishTime(Date publishDate) {
        String timeStr = "";
        if (publishDate == null) {
            return "";
        }
        Calendar publishTimeCalendar = Calendar.getInstance();
        publishTimeCalendar.setTime(publishDate);

        Date currentDate = new Date();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);

        if (publishTimeCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR)) {
            return "";
        }
        long ONE_MINUTE = 60;
        long ONE_HOUR = 3600;
        long ONE_DAY = 86400;
        long publishTimestamp = publishDate.getTime() / 1000;
        long currentTimestamp = currentDate.getTime() / 1000;
        long pastSecond = currentTimestamp - publishTimestamp;
        if (pastSecond < 10 * ONE_MINUTE) {
            timeStr = "刚刚";
        } else if (pastSecond >= 10 * ONE_MINUTE && pastSecond < ONE_HOUR) {
            timeStr = pastSecond / ONE_MINUTE + "分钟前";
        } else if (pastSecond > ONE_HOUR && pastSecond < ONE_DAY) {
            timeStr = pastSecond / ONE_HOUR + "小时前";
        } else if (pastSecond >= ONE_DAY) {
            long pastDay = currentCalendar.get(Calendar.DAY_OF_YEAR) - publishTimeCalendar.get(Calendar.DAY_OF_YEAR);
            if (pastDay < 2) {
                timeStr = "昨天" + DateUtils.formatDate(publishDate, "HH:mm");
            } else if (pastDay >= 2 && pastDay < 3) {
                timeStr = "前天" + DateUtils.formatDate(publishDate, "HH:mm");
            } else if (pastDay >= 3) {
                timeStr = pastDay == 3 ? pastDay + "天前" : "";
            }
        }
        return timeStr;
    }

    public static boolean isToday(String time, String format) {
        if (StringUtils.isEmpty(time)) {
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDate localDate = LocalDate.parse(time, formatter);
        LocalDate currentDate = LocalDate.now();
        return localDate.isEqual(currentDate);
    }

    public static String formatDate(String date, String sourceFormat, String format) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(sourceFormat);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(date, inputFormatter);
        return dateTime.format(outputFormatter);
    }



    public static void main(String[] args) {
        String inputDateTime = "2022-03-25 23:59:59"; // The string representation of the date and time
        String ss = formatDate(inputDateTime, "yyyy-MM-dd HH:mm:ss", "MM月dd日");
        System.out.println(ss);
    }

}