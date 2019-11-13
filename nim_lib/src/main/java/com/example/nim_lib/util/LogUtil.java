package com.example.nim_lib.util;

import android.util.Log;

public class LogUtil {
    private static LogUtil log;
    private static boolean isOpen = true;
    private static int LOG_MAXLENGTH = 2000;
    public void init(boolean open) {
        isOpen = open;
        if(isOpen){
            Log.i("Log", "=================调试日志:开启================");
        }
    }

    private void sp(String TAG,String msg,int state){
        if(!isOpen)
            return;
        if(TAG==null||TAG.length()<1){
            TAG="log";
        }
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                p(TAG+i,msg.substring(start, end),state);

                start = end;
                end = end + LOG_MAXLENGTH;
            } else {

                p(TAG, msg.substring(start, strLength),state);
                break;
            }
        }
    }

    private void p(String TAG,String msg,int state){
        TAG="a==="+TAG;
        switch (state){
            case 0:
                Log.i(TAG,msg);
                break;
            case 1:
                Log.d(TAG,msg);
                break;
            case 2:
                Log.e(TAG,msg);
                break;
        }

    }

    public void e(String tag, String msg) {
        sp(tag, msg,2);
    }
    public void d(String tag, String msg) {
        sp(tag, msg,1);
    }
    public void i(String tag, String msg) {
        sp(tag, msg,0);
    }

    public static LogUtil getLog() {
        log = log == null ? new LogUtil() : log;

        return log;
    }

}
