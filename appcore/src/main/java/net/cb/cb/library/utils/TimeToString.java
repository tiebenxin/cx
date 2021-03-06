package net.cb.cb.library.utils;

import android.text.Html;
import android.text.Spanned;

import net.cb.cb.library.CoreEnum;

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

    public final static long MILLISECOND = 1000;
    public final static long MINUTE = MILLISECOND * 60;
    public final static long HOUR = MINUTE * 60;
    public final static long DAY = HOUR * 24;

    public static long DIFF_TIME = 0L;//当前服务器时间与本地时间差值

    public static String YYYY_MM(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
        return dateFormat.format(new Date(time));
    }

    public static String getSelectMouth(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");
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

    public static String HH_MM2(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH时mm分");
        return dateFormat.format(new Date(time));
    }

    public static String YYYY_MM_DD_HH_MM(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(new Date(time));
    }

    public static String MM_DD_HH_MM2(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日HH时mm分");
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

    //分钟：ss
    public static String MM_SS(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(new Date(time));
    }

    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    public static String getTimeWx(Long timestamp) {
        if (timestamp == null || timestamp == 0L) {
            return "";
        }
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
                if (todayCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {//当天
                    result = getTime(timestamp, hourTimeFormat);
                } else if (todayCalendar.get(Calendar.DATE) == (calendar.get(Calendar.DATE) + 1)) {
                    result = getTime(timestamp, dayTimeFormat);
                } else if (todayCalendar.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR)) {
                    result = getTime(timestamp, weekNames[calendar.get(Calendar.DAY_OF_WEEK) - 1] + " " + hourTimeFormat);
                } else {
                    result = getTime(timestamp, yearTimeFormat);
                }
            } else {
                result = getTime(timestamp, yearTimeFormat);
            }
            return result;
        } catch (Exception e) {

            return "";
        }
    }

    /**
     * 收藏时间显示规则
     *
     * @param timeStamp
     * @return
     */
    public static String getTimeForCollect(Long timeStamp) {
        long curTimeMillis = System.currentTimeMillis();
        Date curDate = new Date(curTimeMillis);
        int todayHoursSeconds = curDate.getHours() * 60 * 60;
        int todayMinutesSeconds = curDate.getMinutes() * 60;
        int todaySeconds = curDate.getSeconds();
        int todayMillis = (todayHoursSeconds + todayMinutesSeconds + todaySeconds) * 1000;
        long todayStartMillis = curTimeMillis - todayMillis;
        if (timeStamp >= todayStartMillis) {
            return "今天";
        }
        int oneDayMillis = 24 * 60 * 60 * 1000;
        long yesterdayStartMilis = todayStartMillis - oneDayMillis;
        if (timeStamp >= yesterdayStartMilis) {
            return "昨天";
        }
        long yesterdayBeforeStartMilis = yesterdayStartMilis - oneDayMillis;
        if (timeStamp >= yesterdayBeforeStartMilis) {
            return "前天";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(timeStamp));
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

    public static Spanned getTimeOnline(Long timestamp, @CoreEnum.ESureType int activeType, boolean isChat) {
        String color = "#276baa";
        if (isChat) {
//            color = "#A1CCF0";
            color = "#4886c5";
        }
        if (activeType == CoreEnum.ESureType.YES) {
            String timestr = String.format("<font color='%s'>在线</font>", color);
            return Html.fromHtml(timestr);
        } else {
            Calendar todayCalendar = Calendar.getInstance();
            Long now = todayCalendar.getTimeInMillis() + DIFF_TIME;//当前服务器时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            long disparity = new Double((now - timestamp) / 1000.0).longValue();//差距秒
            String timestr = "";
            if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                if (todayCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) { //同一天
                    LogUtil.getLog().i(TimeToString.class.getSimpleName(), "  时间差=" + disparity);
                    if (disparity >= 0 && disparity < 2 * 60) { //0 到2 min
//                        timestr = "<font color='#A1CCF0'>刚刚</font>";
                        timestr = String.format("<font color='%s'>刚刚在线</font>", color);

                    } else if (disparity >= 2 * 60 && disparity < 60 * 60) { //2min到1小时
//                        timestr = "<font color='#A1CCF0'>" + new Long(disparity / 60).intValue() + "分钟前</font>";
                        timestr = String.format("<font color='%s'>" + new Long(disparity / 60).intValue() + "分钟前</font>", color);

                    } else if (disparity >= 60 * 60 && disparity <= 24 * 60 * 60) { //1 小时 到24小时
                        timestr = new Long(disparity / 60 / 60).intValue() + "小时前";
                    } else {
                        timestr = YYYY_MM_DD(timestamp) + "";
                    }
                } else if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && todayCalendar.get(Calendar.DAY_OF_YEAR) == (calendar.get(Calendar.DAY_OF_YEAR) + 1)) {//隔一天
                    if (disparity >= 0 && disparity < 2 * 60) { //0 到2 min
//                        timestr = "<font color='#A1CCF0'>刚刚</font>";
                        timestr = String.format("<font color='%s'>刚刚在线</font>", color);
                    } else if (disparity >= 2 * 60 && disparity < 60 * 60) { //2min到1小时
//                        timestr = "<font color='#A1CCF0'>" + new Long(disparity / 60).intValue() + "分钟前</font>";
                        timestr = String.format("<font color='%s'>" + new Long(disparity / 60).intValue() + "分钟前</font>", color);

                    } else {
                        timestr = "昨天 " + HH_MM(timestamp) + "";
                    }
                } else if (todayCalendar.get(Calendar.DAY_OF_YEAR) == (calendar.get(Calendar.DAY_OF_YEAR) + 2)) {
                    timestr = "前天 " + HH_MM(timestamp) + "";
                } else if (todayCalendar.get(Calendar.DAY_OF_YEAR) <= (calendar.get(Calendar.DAY_OF_YEAR) + 7) && todayCalendar.get(Calendar.DAY_OF_YEAR) >= (calendar.get(Calendar.DAY_OF_YEAR) + 3)) {
                    timestr = (todayCalendar.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR)) + "天前";
                } else {
                    timestr = YYYY_MM_DD(timestamp) + "";
                }
            } else {
                timestr = YYYY_MM_DD(timestamp) + "";

            }
            return Html.fromHtml(timestr);
        }
    }

    public static String getEnvelopeTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Calendar todayCalendar = Calendar.getInstance();
        String result = "";
        if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            if (todayCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) { //同一天
                result = HH_MM2(time);
            } else {
                result = "昨天 " + HH_MM2(time);
            }
        }
        return result;
    }

    /**
     * 广场动态时间转换
     *
     * @param timestamp
     * @return
     */
    public static String formatCircleDate(Long timestamp) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
//        long ns = 1000;
        // 获得两个时间的秒时间差异
        long diff = System.currentTimeMillis() - timestamp;
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
//        long sec = diff % nd % nh % nm / ns;
        String res = "";
        if (day > 7) {// 1个星期以前：显示年-月-日（2019-2-24 ）
            res = getTime(timestamp, "yyyy-MM-dd");
        } else if (day > 2) {// 前天到1个星期内：多少天前（3天前）
            res = day + "天前";
        } else if (day == 2) {// 前天 时:分 （前天17:10）
            res = "前天" + getTime(timestamp, "HH:mm");
        } else if (day == 1) {// 前天 时:分 （前天17:10）
            res = "昨天" + getTime(timestamp, "HH:mm");
        } else if (hour >= 1) {// 1小时以上，当日以内：多少小时前（2小时前）
            res = hour + "小时前";
        } else if (min > 2) {// 2~60分钟：多少分钟前（10分钟前）
            res = min + "分钟前";
        } else {
            res = "刚刚";
        }
        return res;
    }

    //获取朋友圈推荐时间
    public static String getRecommendTime(long time) {
        long diff = System.currentTimeMillis() - time;
//        LogUtil.getLog().i("时间", "diff=" + diff);
        if (diff <= 0) {
            return "1秒前推荐";
        }
        if (diff > 0 && diff < MILLISECOND * 60) {
            int s = (int) (diff / MILLISECOND);
            if (s == 0) {
                s = 1;
            }
            return s + "秒前推荐";
        } else if (diff >= MINUTE && diff < 60 * MINUTE) {
            int m = (int) (diff / MINUTE);
            if (m == 0) {
                m = 1;
            }
            return m + "分钟前推荐";
        } else if (diff >= HOUR && diff < 24 * HOUR) {
            int h = (int) (diff / HOUR);
            if (h == 0) {
                h = 1;
            }
            return h + "小时前推荐";
        } else if (diff >= DAY) {
            int d = (int) (diff / DAY);
            if (d == 0) {
                d = 1;
            }
            return diff + "天前推荐";
        } else {
            return "1秒前推荐";
        }
    }
}
