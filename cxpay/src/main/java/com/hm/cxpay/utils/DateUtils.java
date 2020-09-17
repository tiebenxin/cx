package com.hm.cxpay.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.TimeToString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Liszt
 * @date 2019/12/4
 * Description
 */
public class DateUtils {
    public final static long MILLISECOND = 1000;
    public final static long MINUTE = MILLISECOND * 60;
    public final static long HOUR = MINUTE * 60;
    public final static long DAY = HOUR * 24;


    //获取每月第一天的最初时间
    public static long getStartTimeOfMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        LogUtil.getLog().i("时间LOG--getStartTimeOfMonth", TimeToString.YYYY_MM_DD_HH_MM_SS(calendar.getTimeInMillis()));
        return calendar.getTimeInMillis();
    }


    /**
     * 获取指定日期所在月份第一天开始的时间戳
     *
     * @param date 指定日期
     * @return
     */
    public static Long getMonthBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND, 0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();
    }


    /**
     * 根据某个时间戳获取当月的结束时间（最后一天的最后一毫秒）
     *
     * @return
     */
    public static Date endTimeOfMonth(long time) {
        Calendar first = new GregorianCalendar();
        first.setTimeInMillis(time);
        first.add(Calendar.MONTH, 1); // 加一个月
        first.set(Calendar.DAY_OF_MONTH, 1);
        first.set(Calendar.HOUR_OF_DAY, 0);
        first.set(Calendar.MINUTE, 0);
        first.set(Calendar.SECOND, 0);
        first.set(Calendar.MILLISECOND, 0);
        first.add(Calendar.MILLISECOND, -1);
        return first.getTime();
    }

    //获取红包抢完时间
    public static String getGrabFinishedTime(long finishTime) {
        String result = "0秒";
        long diff = finishTime;
        if (diff <= MILLISECOND) {
            result = "1秒";
        } else if (diff > MILLISECOND && diff < MINUTE) {
            int sec = (int) (diff / MILLISECOND);
            result = sec + "秒";
        } else if (diff > MINUTE && diff < HOUR) {
            int min = (int) (diff / MINUTE);
            result = min + "分钟";
        } else if (diff > HOUR && diff < DAY) {
            int hour = (int) (diff / HOUR);
            result = hour + "小时";
        } else if (diff > DAY) {
            int day = (int) (diff / DAY);
            result = day + "天";
        }
        return result;
    }

    //获取抢红包时间
    public static String getGrabTime(long time) {
        Calendar todayCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String hourTimeFormat = "HH:mm";
        String dayTimeFormat = "MM-dd HH:mm";
        String yearTimeFormat = "yyyy-MM-dd  HH:mm";
        String result = "";
        if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            if (todayCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {//当天
                result = getTime(time, hourTimeFormat);
            } else {
                result = getTime(time, dayTimeFormat);
            }
        } else {
            result = getTime(time, yearTimeFormat);
        }
        return result;

    }

    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    public static String getFullTime(long time) {
        return getTime(time, "yyyy-MM-dd  HH:mm");
    }

    public static String getTransferTime(long time) {
        return getTime(time, "yyyy-MM-dd  HH:mm:ss");
    }

    /**
     * 判断时间间隔是否在X小时以内
     *
     * @param date1 上次时间
     * @param date2 本次时间
     * @param hour  目标小时数
     * @return boolean
     * @throws Exception
     */
    public static boolean judgmentDate(String date1, String date2, double hour) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse(date1);
        Date end = sdf.parse(date2);
        long cha = end.getTime() - start.getTime();
        if (cha < 0) {
            return false;
        }
        double result = cha * 1.0 / (1000 * 60 * 60);
        if (result <= hour) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前系统时间，格式如2018-11-27 10:41:47
     *
     * @return
     */
    public static String getNowFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }


    /**
     * 判断时间间隔是否在X小时以内
     *
     * @param date1 上次时间
     * @param date2 本次时间
     * @param hour  目标小时数
     * @return boolean
     * @throws Exception
     */
    public static boolean isInHours(long date1, long date2, double hour) {
        long diff = date2 - date1;
        if (diff < hour * HOUR) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 秒转为分秒 00:00
     *
     * @param i
     * @return
     */
    public static String secondFormat(long i) {
        if (String.valueOf(i).length() < 2) {
            return "0" + i;
        } else {
            return String.valueOf(i);
        }
    }

    /**
     * 秒转为分秒 00:00
     *
     * @param m
     * @return
     */
    public static String getSecondFormatTime(long m) {
        if (m < 60) {//秒
            return secondFormat(0) + ":" + secondFormat(m);
        }
        if (m < 3600) {//分
            return secondFormat(m / 60) + ":" + secondFormat(m % 60);
        }
        return "--";
    }

    //获取当前月最初时间及最后时间（下个月最初时间）
    public static Long[] getStartAndEndTimeOfMonth(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(calendar.getTimeInMillis());
        Long[] result = new Long[2];
        result[0] = getStartTimeOfMonth(c);
        c.add(Calendar.MONTH, 1);//下月最初时间
        result[1] = getStartTimeOfMonth(c);
        return result;
    }


    //获取当前月最初时间及最后时间（下个月最初时间）,不含本周时间
    public static Long[] getStartAndEndTimeOfMonth2(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(calendar.getTimeInMillis());
        Long[] result = new Long[2];
        result[0] = getStartTimeOfMonth(c);
        result[1] = getStartTimeOfWeek(c);
        return result;
    }

    //获取当前周最初时间及最后时间（下个月最初时间）
    public static Long[] getStartAndEndTimeOfWeek(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(calendar.getTimeInMillis());
        Long[] result = new Long[2];
        result[0] = getStartTimeOfWeek(c);
        c.add(Calendar.WEEK_OF_MONTH, 1);//下月最初时间
        result[1] = getStartTimeOfWeek(c);
        return result;
    }

    /**
     * 获取当前系统时间，格式如2018年9月
     *
     * @return
     */
    public static String getYYYY_MM(long time, String format) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyy/MM";
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    //当前时间是否是在本周
    public static boolean isCurrentMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);
        if (calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH)) {
            return true;
        }
        return false;
    }

    //当前时间是否是在本周
    public static boolean isCurrentWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);
        if (calendar.get(Calendar.WEEK_OF_YEAR) == calendar1.get(Calendar.WEEK_OF_YEAR)) {
            return true;
        }
        return false;
    }

    //当前时间是否是在本周（未考虑跨年）
    public static boolean isCurrentYear(long time) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);
        if (calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }

    //当前时间是否是在本周
    public static boolean isFirstWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);
        if (calendar.get(Calendar.WEEK_OF_YEAR) == calendar1.get(Calendar.WEEK_OF_YEAR) && calendar.get(Calendar.WEEK_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

    //获取当前周的最初时间
    public static long getStartTimeOfWeek(Calendar calendar) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar1.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH));
        calendar1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//周日为一周的第一天
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        if (calendar1.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
            LogUtil.getLog().i("时间LOG--getStartTimeOfWeek", TimeToString.YYYY_MM_DD_HH_MM_SS(calendar1.getTimeInMillis()));
        } else if (calendar1.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)) {
            calendar1.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            calendar1.set(Calendar.DAY_OF_MONTH, 1);
            calendar1.set(Calendar.HOUR_OF_DAY, 0);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);
            LogUtil.getLog().i("时间LOG--getStartTimeOfWeek--本年跨月", TimeToString.YYYY_MM_DD_HH_MM_SS(calendar1.getTimeInMillis()));
        } else if (calendar1.get(Calendar.YEAR) < calendar.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) > calendar.get(Calendar.MONTH)) {
            calendar1.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            calendar1.set(Calendar.DAY_OF_MONTH, 1);
            calendar1.set(Calendar.HOUR_OF_DAY, 0);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);
            LogUtil.getLog().i("时间LOG--getStartTimeOfWeek--跨年跨月", TimeToString.YYYY_MM_DD_HH_MM_SS(calendar1.getTimeInMillis()));
        }
        return calendar1.getTimeInMillis();

    }


}
