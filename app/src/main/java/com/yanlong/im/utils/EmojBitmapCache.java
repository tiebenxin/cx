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
    private EmojBitmapCache(){}
    private static class Holder{
        private static EmojBitmapCache instance=new EmojBitmapCache();
    }
    public static EmojBitmapCache getInstance(){
        return Holder.instance;
    }
    public void put(String key,Bitmap bitmap){
        mBitmapCache.put(key,bitmap);
    }

    public Bitmap get(String key){
        if(mBitmapCache.containsKey(key)){
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
