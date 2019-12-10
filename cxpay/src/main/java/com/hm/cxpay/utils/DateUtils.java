package com.hm.cxpay.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Liszt
 * @date 2019/12/4
 * Description
 */
public class DateUtils {


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
}
