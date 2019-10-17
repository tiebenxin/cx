package com.yanlong.im.utils;

public class MyDiskCacheUtils {


    private static MyDiskCacheUtils myDiskCacheUtils;
    public static long MAX_SIZE=2*1024*1024;

    private MyDiskCacheUtils(){}

    //实例化对象
    public static MyDiskCacheUtils getInstance(){

        if (null==myDiskCacheUtils){
            synchronized (MyDiskCacheUtils.class){
                if (null==myDiskCacheUtils){
                    myDiskCacheUtils=new MyDiskCacheUtils();
                }
            }

        }
        return myDiskCacheUtils;
    }


    public Object getObj(String path){
        return null;
    }
}
