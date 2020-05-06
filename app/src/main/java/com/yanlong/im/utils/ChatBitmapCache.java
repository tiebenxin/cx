package com.yanlong.im.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.luck.picture.lib.glide.CustomGlideModule;

import java.io.File;

/**
 * 聊天界面bitmap 缓存
 *
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/27 0027
 * @description
 */
public class ChatBitmapCache {
    private LruCache<String, Bitmap> mImageCache;

    private ChatBitmapCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int memory = maxMemory / 8;
        mImageCache = new LruCache<String, Bitmap>(memory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    private static class Holder {
        private static ChatBitmapCache instance = new ChatBitmapCache();
    }

    public static ChatBitmapCache getInstance() {
        return Holder.instance;
    }

    public void put(String key, Bitmap bitmap) {
        mImageCache.put(key, bitmap);
    }

    public Bitmap getAndGlideCache(String key) {
        Bitmap bitmap = mImageCache.get(key);
        if (bitmap == null) {
            File file = new File(key);
            if (file.exists()) {//是本地文件
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    options.inJustDecodeBounds = true; //加载的时候只加载图片的宽高属性，不加载原图
                    options.inPreferredConfig = Bitmap.Config.RGB_565;//降低色彩模式，如果对透明度没有要求，RGB_565即可满足需求
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    if (bitmap != null) mImageCache.put(key, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {//是线上文件
                bitmap = CustomGlideModule.getCacheBitmap(key);
                if (bitmap != null) mImageCache.put(key, bitmap);
            }
        }
        return bitmap;
    }
    public void clear() {
        if (mImageCache != null) {
            if (mImageCache.size() > 0) {
                mImageCache.evictAll();
            }
        }
        mImageCache=null;
    }
    public void clearCache() {
        if (mImageCache != null) {
            if (mImageCache.size() > 0) {
                mImageCache.evictAll();
            }
        }
    }
}
