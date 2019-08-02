package net.cb.cb.library.utils;

import android.text.Html;
import android.text.Spanned;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public static String HH_MM(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(new Date(time));
    }

    public static String YYYY_MM_DD_HH_MM(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(new Date(time));
    }

    public static String YYYY_MM_DD(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date(time));
    }

    public static String YYYY_MM_DD_HH_MM_SS(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date(time));
    }

    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    public static String getTimeWx(Long timestamp) {
        String result = "";
        String[] weekNames = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String hourTimeFormat = "HH:mm";
        String dayTimeFormat = "昨天 HH:mm";
        String yearTimeFormat = "yyyy-MM-dd  HH:mm";
        try {
            Calendar todayCalendar = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                if (todayCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {//当年
                    result = getTime(timestamp, hourTimeFormat);
                } else if (todayCalendar.get(Calendar.DATE) == (calendar.get(Calendar.DATE) + 1)) {
                    result = getTime(timestamp, dayTimeFormat);
                } else if (todayCalendar.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR)) {
                    result = getTime(timestamp, weekNames[calendar.get(Calendar.DAY_OF_WEEK) -1] + hourTimeFormat);
                }
            } else {
                result = getTime(timestamp, yearTimeFormat);
            }
            return result;
        } catch (Exception e) {

            return "";
        }
    }

    public static Spanned getTimeOline(Long timestamp ){
        Long now=new Date().getTime();
        Calendar todayCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        long disparity=new Double((now-timestamp)/1000.0).longValue();//差距秒
        String timestr=YYYY_MM_DD(timestamp)+"";

        if(todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)&&todayCalendar.get(Calendar.DAY_OF_YEAR) <= (calendar.get(Calendar.DAY_OF_YEAR)+7)){
            timestr=(todayCalendar.get(Calendar.DAY_OF_YEAR)-calendar.get(Calendar.DAY_OF_YEAR))+"天前";
        }
        if(todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)&&todayCalendar.get(Calendar.DAY_OF_YEAR) == (calendar.get(Calendar.DAY_OF_YEAR)+2)){
            timestr="前天 "+ HH_MM(timestamp)+"";
        }
        if(todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)&&todayCalendar.get(Calendar.DAY_OF_YEAR) == (calendar.get(Calendar.DAY_OF_YEAR)+1)){
            timestr="昨天 "+ HH_MM(timestamp)+"";
        }
        if(todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)&&todayCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)){
            timestr= new Long(disparity/60/60).intValue()+"小时前";
        }
        if (disparity<60*60){

            timestr=  "<font color='#276baa'>"+new Long(disparity/60).intValue()+"分钟前</font>";
        }
        if (disparity<2*60){
            timestr=  "<font color='#276baa'>刚刚在线</font>";
        }


            return Html.fromHtml(timestr);
    }


    public static String A_DD_HH_MM(Long time) {
        long day = time / 86400000;
        long m = time - day * 86400000;
        long hour = m / 3600000;
        m = m - hour * 3600000;
        long minute = m / 60000;
        String s = day + "天" + hour + "小时" + minute + "分钟";

        if (day <= 0)
            s = hour + "小时" + minute + "分钟";

        if (day <= 0 && hour <= 0)
            s = minute + "分钟";

        return s;
    }
}
