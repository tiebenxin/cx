package com.hm.cxpay.utils;

import com.umeng.commonsdk.debug.D;

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
    public static Calendar getStartTimeOfMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
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
    public static String getGrabFinishedTime(long start, long end) {
        String result = "0秒";
        if (end > start) {
            long diff = end - start;
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
        }
        return result;
    }
}
