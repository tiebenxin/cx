package com.yanlong.im.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.yanlong.im.chat.server.ChatServer;

import net.cb.cb.library.utils.ToastUtil;

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
            synchronized (MyException.class){
                mInstance=new MyException();
            }
        }
        return mInstance;
    }
    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        Log.e("TAG","捕获到异常"+ex.getMessage());
       // ToastUtil.show(mContext,"程序异常!即将退出");
       try{
           // Thread.sleep(3000);
           mContext.stopService(new Intent(mContext, ChatServer.class));
           android.os.Process.killProcess(android.os.Process.myPid());
           System.exit(0);
       }catch (Exception exce){
        }
    }
}
