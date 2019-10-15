package com.yanlong.im.utils;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @创建人 shenxin
 * @创建时间 2019/10/15 0015 11:53
 */
public class ReadDestroyUtil {


    public int setSeekBarnProgress(SeekBar seekBar, int progress, TextView textView) {
        int destroyTime = 0;
        if (progress == 0) {
            textView.setText("5秒");
            seekBar.setProgress(0);
            destroyTime = 5;
        } else if (progress > 0 && progress <= 10) {
            textView.setText("10秒");
            seekBar.setProgress(10);
            destroyTime = 10;
        } else if (progress > 10 && progress <= 20) {
            textView.setText("30秒");
            seekBar.setProgress(20);
            destroyTime = 30;
        } else if (progress > 20 && progress <= 30) {
            textView.setText("1分钟");
            seekBar.setProgress(30);
            destroyTime = 60;
        } else if (progress > 30 && progress <= 40) {
            textView.setText("5分钟");
            seekBar.setProgress(40);
            destroyTime = 5 * 60;
        } else if (progress > 40 && progress <= 50) {
            textView.setText("30分钟");
            seekBar.setProgress(50);
            destroyTime = 30 * 60;
        } else if (progress > 50 && progress <= 60) {
            textView.setText("1小时");
            seekBar.setProgress(60);
            destroyTime = 60 * 60;
        } else if (progress > 60 && progress <= 70) {
            textView.setText("6小时");
            seekBar.setProgress(70);
            destroyTime = 6 * 60 * 60;
        } else if (progress > 70 && progress <= 80) {
            textView.setText("12小时");
            seekBar.setProgress(80);
            destroyTime = 12 * 60 * 60;
        } else if (progress > 80 && progress <= 90) {
            textView.setText("1天");
            seekBar.setProgress(90);
            destroyTime = 24 * 60 * 60;
        } else if (progress > 90 && progress <= 100) {
            textView.setText("一星期");
            seekBar.setProgress(100);
            destroyTime = 7 * 24 * 60 * 60;
        }
        return destroyTime;
    }


    public void initSeekBarnProgress(SeekBar seekBar,int destroyTime) {
        String date = formatDateTime(destroyTime);
        if(date.equals("5秒")){
            seekBar.setProgress(0);
        }else if(date.equals("10秒")){
            seekBar.setProgress(10);
        }else if(date.equals("30秒")){
            seekBar.setProgress(20);
        }else if(date.equals("1分钟")){
            seekBar.setProgress(30);
        }else if(date.equals("5分钟")){
            seekBar.setProgress(40);
        }else if(date.equals("30分钟")){
            seekBar.setProgress(50);
        }else if(date.equals("1小时")){
            seekBar.setProgress(60);
        }else if(date.equals("6小时")){
            seekBar.setProgress(70);
        }else if(date.equals("12小时")){
            seekBar.setProgress(80);
        }else if(date.equals("1天")){
            seekBar.setProgress(90);
        }else if(date.equals("一星期")){
            seekBar.setProgress(100);
        }
    }


    public String formatDateTime(int mss) {
        String DateTimes = null;
        int week = mss / (60 * 60 * 24 * 7);
        int days = mss / (60 * 60 * 24);
        int hours = (mss % (60 * 60 * 24)) / (60 * 60);
        int minutes = (mss % (60 * 60)) / 60;
        int seconds = mss % 60;
        if (week > 0) {
            DateTimes = "一个星期";
        } else if (days > 0) {
            DateTimes = days + "天";
        } else if (hours > 0) {
            DateTimes = hours + "小时";
        } else if (minutes > 0) {
            DateTimes = minutes + "分钟";
        } else {
            DateTimes = seconds + "秒";
        }

        return DateTimes;
    }


}
