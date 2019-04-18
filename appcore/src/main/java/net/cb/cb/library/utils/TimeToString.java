package net.cb.cb.library.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * 时间工具类
 * @author jyj
 * @date 2017/1/9
 */
public class TimeToString {
    public static String YYYY_MM(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
        return dateFormat.format(new Date(time));
    }
    public static long toYYYY_MM(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
        try {
            return dateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static long getYYYY_MM(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
        try {
            return dateFormat.parse(dateFormat.format(new Date(time))).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String MM_DD_HH_MM(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
        return dateFormat.format(new Date(time));
    }

    public static String YYYY_MM_DD_HH_MM(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(new Date(time));
    }
    public static String YYYY_MM_DD_HH_MM_SS(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date(time));
    }

    public static String A_DD_HH_MM(Long time) {
       long day= time/86400000;
        long m=time-day*86400000;
        long hour=m/3600000;
        m=m-hour*3600000;
        long minute=m/60000;
        String s=day+"天"+hour+"小时"+minute+"分钟";

        if(day<=0)
            s=hour+"小时"+minute+"分钟";

        if(day<=0&&hour<=0)
            s=minute+"分钟";

        return s;
    }
}
