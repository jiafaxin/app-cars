package com.autohome.app.cars.common.utils;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DateUtil {
    public static String format(Date date,String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String dateStr, String temp) {
        SimpleDateFormat sdf = new SimpleDateFormat(temp);
        try {
            Date date = sdf.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Date parseDateFromWithDateStr(String str){
        if (StringUtils.isNotEmpty(str)) {
            if (str.length() == 21) {
                return new Date(Long.valueOf(str.substring(6, 19)));
            }
        }
        return null;
    }

    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (double)((afterTime - beforeTime) / 86400000L);
    }

    public static String convertDuration(Object obj){
        BigDecimal big = null;
        if(obj.getClass() == String.class){
            big = new BigDecimal(obj.toString());
        }else{
            big = new BigDecimal(Long.parseLong(obj.toString()));
        }
        int time = big.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
        int hour = 0;
        int mintues = 0;
        int second = 0;
        if(time < 60){
            return "00:"+ stringType(time);
        }else if(time < 3600 ){
            mintues = time /60;
            second = time%60;
            return stringType(mintues)+":"+stringType(second);
        }else{
            hour = time /3600;
            mintues = (time%3600)/60;
            second = (time%3600)%60;
            return stringType(hour)+":"+stringType(mintues)+":"+stringType(second);
        }
    }

    private static String stringType(int value) {
        return value <= 9 ? "0" + value : value + "";
    }

    public static String pastTime(Date dt) {
        if (dt == null) {
            return "";
        }
        Date d = new Date();
        long ts = d.getTime() - dt.getTime();
        if (ts < 1000) {
            return "1秒前";
        } else if (ts < 60000) {
            return (ts / 1000) + "秒前";
        } else if (ts < 3600000) {
            return (ts / 60000) + "分钟前";
        } else if (ts < 86400000) {
            return (ts / 3600000) + "小时前";
        } else if (ts < 604800000) {
            return (ts / 86400000) + "天前";
        } else if (ts < 1209600000) {
            return "1周前";
        } else if (ts < 2592000000l) {
            return "2周前";
        } else if (ts < 5184000000l) {
            return "1个月前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(dt);
        }
    }

    public static String formatTime(int second) {
        if (second < 10) {
            return "00:0" + second;
        } else if (second < 60) {
            return "00:" + second;
        } else {
            int hour;
            if (second < 3600) {
                hour = second / 60;
                second -= hour * 60;
                if (hour < 10) {
                    return second < 10 ? "0" + hour + ":0" + second : "0" + hour + ":" + second;
                } else {
                    return second < 10 ? hour + ":0" + second : hour + ":" + second;
                }
            } else {
                hour = second / 3600;
                int minute = (second - hour * 3600) / 60;
                second = second - hour * 3600 - minute * 60;
                if (hour < 10) {
                    if (minute < 10) {
                        return second < 10 ? "0" + hour + ":0" + minute + ":0" + second : "0" + hour + ":0" + minute + ":" + second;
                    } else {
                        return second < 10 ? "0" + hour + minute + ":0" + second : "0" + hour + minute + ":" + second;
                    }
                } else if (minute < 10) {
                    return second < 10 ? hour + ":0" + minute + ":0" + second : hour + ":0" + minute + ":" + second;
                } else {
                    return second < 10 ? hour + minute + ":0" + second : hour + minute + ":" + second;
                }
            }
        }
    }

    public static Date addDays(Date date, int days) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @description 将时间戳转换为指定的日期格式，例如：yyyy-MM-dd HH:mm:ss
     * @author zzli
     * @param timestampStr 时间戳-long
     * @param dateFormat 日期格式化
     * @return java.lang.String
     */

    public static String convertTimestamp(String timestampStr,String dateFormat){
        try {

            if (StringUtils.isEmpty(timestampStr)||timestampStr.length()<10) {
                return StringUtils.EMPTY;
            }
            long timestamp = Long.parseLong(timestampStr);
            boolean isSecond = timestampStr.length() == 10;

            // 将时间戳转换为LocalDateTime对象
            LocalDateTime dateTime;
            if (isSecond) {
                dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
            } else {
                dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            }

            // 创建一个DateTimeFormatter对象
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

            // 使用DateTimeFormatter对象格式化LocalDateTime对象
            return dateTime.format(formatter);

        } catch (Exception e) {
           return StringUtils.EMPTY;
        }
    }

    /**
     * 时间展示规则：
     * 当天：时：分，例如13：20
     * 昨天：昨天 时：分，例如昨天 13：20
     * 昨天之前，本年：月 日，例如6月2日
     * 昨天之前，非半年：年 月 日 ，例如 2023年6月2日
     */
    public static String formatDisplayTime(Timestamp timestamp) {
        try {
            LocalDateTime dateTime = timestamp.toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(dateTime.toLocalDate(), now.toLocalDate());

            String formatTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            if (daysBetween == 0 && !"00:00".equals(formatTime)) {
                // 当天
                return formatTime;
            } else if (daysBetween == 1 && !"00:00".equals(formatTime)) {
                // 昨天
                return "昨天 " + formatTime;
            } else if (dateTime.getYear() == now.getYear()) {
                // 本年
                return dateTime.format(DateTimeFormatter.ofPattern("M月d日"));
            } else if (dateTime.isAfter(now.minusMonths(6))) {
                // 非半年
                return dateTime.format(DateTimeFormatter.ofPattern("yyyy年M月d日"));
            } else {
                return dateTime.format(DateTimeFormatter.ofPattern("yyyy年M月d日"));
            }
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }



    public static Date deserialize(String source, String... dateFormat) {
        Date date = null;
        if (dateFormat != null && dateFormat.length > 0) {
            for (String df : dateFormat) {
                try {
                    date = new SimpleDateFormat(df).parse(source);
                    break;
                } catch (Exception e) {
                    log.debug(e.getMessage());
                }
            }

        } else {
            // sample: /Date(1528805593000)/
            Matcher matcher = Pattern.compile("\\/Date\\((-?\\d+)\\)\\/").matcher(source);
            if (matcher.matches()) {
                date = new Date(Long.valueOf(matcher.group(1)));
            } else {
                try {
                    date = parse(source);
                } catch (ParseException e) {
                    log.debug(e.getMessage());
                }
            }


        }
        Preconditions.checkNotNull(date, String.format("date %s can't be resolved", source));
        return date;

    }
    /**
     * 函数功能描述:UTC时间转本地时间格式
     *
     * @param datetime 日期字符串
     * @return 本地日期
     */
    private static Date parse(String datetime) throws ParseException {
        boolean isUTC = false;
        String utcTimePattern = "yyyy-MM-dd";
        String subTime = datetime.substring(10);// UTC时间格式以 yyyy-MM-dd 开头,将utc时间的前10位截取掉,之后是含有多时区时间格式信息的数据

        // 处理当后缀为:+8:00时,转换为:+08:00 或 -8:00转换为-08:00
        if (subTime.indexOf("+") != -1) {
            subTime = changeUtcSuffix(subTime, "+");
        } else if (subTime.indexOf("-") != -1) {
            subTime = changeUtcSuffix(subTime, "-");
        }
        datetime = datetime.substring(0, 10) + subTime;

        // 依据传入函数的utc时间,得到对应的utc时间格式
        // 步骤一:处理 T
        if (datetime.indexOf("T") != -1) {
            utcTimePattern += "'T'";
        }

        // 步骤二:处理毫秒SSS
        if (org.springframework.util.StringUtils.hasText(subTime)) {
            if (datetime.indexOf(".") != -1) {
                utcTimePattern = utcTimePattern + "HH:mm:ss.SSS";
            } else if (subTime.indexOf("+") == -1 || subTime.indexOf("-") == -1 || subTime.indexOf("Z") == -1) {
                List<String> list = Arrays.asList("HH:mm:ss".split("[:]"));
                utcTimePattern = utcTimePattern + String.join(":", list.subList(0, subTime.split("[:]").length));
            } else {
                utcTimePattern = utcTimePattern + "HH:mm:ss";
            }
        }

        // 步骤三:处理时区问题
        if (subTime.indexOf("+") != -1 || subTime.indexOf("-") != -1) {
            utcTimePattern += "XXX";
            isUTC = true;
        } else if (subTime.indexOf("Z") != -1) {
            utcTimePattern += "'Z'";
            isUTC = true;
        }


        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePattern);
        if (isUTC) {
            utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        Date date = utcFormater.parse(datetime);
        return date;

    }

    /**
     * 函数功能描述:修改时间格式后缀
     * 函数使用场景:处理当后缀为:+8:00时,转换为:+08:00 或 -8:00转换为-08:00
     *
     * @param subTime
     * @param sign
     * @return
     */
    private static String changeUtcSuffix(String subTime, String sign) {
        String timeSuffix = null;
        String[] splitTimeArrayOne = subTime.split("[" + sign + "]");
        String[] splitTimeArrayTwo = splitTimeArrayOne[1].split(":");
        if (splitTimeArrayTwo[0].length() < 2) {
            timeSuffix = sign + "0" + splitTimeArrayTwo[0] + ":" + splitTimeArrayTwo[1];
            subTime = splitTimeArrayOne[0] + timeSuffix;
            return subTime;
        }
        return subTime;
    }
    /**
     * 使用dateFormat格式化Date对象
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String serialize(Date date, String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        String str = df.format(date);
        return str;
    }

    /**
     * 使用dateFormat格式化LocalDateTime对象
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String serialize(LocalDateTime date, String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        String str = df.format(localDateTimeToDate(date));
        return str;
    }

    /**
     * 使用dateFormat格式化LocalDate对象
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String serialize(LocalDate date, String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        String str = df.format(localDateToDate(date));
        return str;
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);

        Date date = Date.from(zdt.toInstant());
        return date;
    }

    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);

        Date date = Date.from(zdt.toInstant());
        return date;
    }
}
