package com.yanlong.im.utils;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/3 0003
 * @description emoj缓存
 */
public class EmojBitmapCache {
    private Map<String, Bitmap> mBitmapCache=new HashMap<>();
    int size=0;
    private EmojBitmapCache(){}
    private static class Holder{
        private static EmojBitmapCache instance=new EmojBitmapCache();
    }
    public static EmojBitmapCache getInstance(){
        return Holder.instance;
    }
    public void put(String key,Bitmap bitmap,int size){
        if(size!=this.size){
            for(Bitmap bitmap1: mBitmapCache.values()){
                bitmap1.recycle();
            }
            mBitmapCache.clear();
        }
        mBitmapCache.put(key,bitmap);
        this.size=size;
    }

    public Bitmap get(String key,int size){
        if(this.size==size&&mBitmapCache.containsKey(key)){
            return mBitmapCache.get(key);
        }else{
            return null;
        }
    }

    public void clear(){
        for(Bitmap bitmap: mBitmapCache.values()){
            bitmap.recycle();
        }
        mBitmapCache.clear();
        mBitmapCache=null;

    }
}
