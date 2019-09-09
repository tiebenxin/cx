package com.yanlong.im.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MyException implements Thread.UncaughtExceptionHandler {
    private static MyException mInstance;
    private Context mContext;
    public MyException(){

    }
    public void init(Context context){
        this.mContext=context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    public static MyException getInstance(){
        if (mInstance==null){
            mInstance=new MyException();
        }
        return mInstance;
    }
    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        Log.e("TAG","捕获到异常"+ex.getMessage());
        Toast.makeText(mContext, "程序出错:3秒后退出. 错误原因" + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
//        try{
//            Thread.sleep(3000);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
//        }catch (InterruptedException exce){
//        }
    }
}
