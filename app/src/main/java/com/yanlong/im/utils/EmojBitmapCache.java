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
    private Map<String, Bitmap> mBitmapCache2=new HashMap<>();
    int size=0;
    int size2=0;
    private EmojBitmapCache(){}
    private static class Holder{
        private static EmojBitmapCache instance=new EmojBitmapCache();
    }
    public static EmojBitmapCache getInstance(){
        return Holder.instance;
    }
    public void put(String key,Bitmap bitmap,int size){
        if(this.size==size){
            mBitmapCache.put(key,bitmap);
        }else if(this.size2==size) {
            mBitmapCache2.put(key, bitmap);
        }else if(this.size==0) {
            this.size = size;
            mBitmapCache.put(key, bitmap);
        }else if(this.size2==0){
            this.size2 = size;
            mBitmapCache2.put(key, bitmap);
        }else{
            //清空
            reset();
            this.size=size;
            mBitmapCache.put(key,bitmap);
        }
    }

    /**
     * 重置-清空
     */
    private void reset(){
        this.size=0;
        this.size2=0;
        for(Bitmap bitmap: mBitmapCache.values()){
            bitmap.recycle();
        }
        mBitmapCache.clear();

        for(Bitmap bitmap1: mBitmapCache2.values()){
            bitmap1.recycle();
        }
        mBitmapCache2.clear();
    }

    public Bitmap get(String key,int size){
        if(this.size==size&&mBitmapCache.containsKey(key)) {
            return mBitmapCache.get(key);
        }else  if(this.size2==size&&mBitmapCache2.containsKey(key)){
            return mBitmapCache2.get(key);
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
        for(Bitmap bitmap: mBitmapCache2.values()){
            bitmap.recycle();
        }
        mBitmapCache2.clear();
        mBitmapCache2=null;
    }
}
