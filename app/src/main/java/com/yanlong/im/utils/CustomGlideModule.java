package com.yanlong.im.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;

/**
 * @创建人 shenxin
 * @创建时间 2019/9/20 0020 11:19
 */
@GlideModule
public class CustomGlideModule extends AppGlideModule {

    public void applyOptions(Context context, GlideBuilder builder) {
        int memoryCacheSizeBytes = 1024 * 1024 * 100;
        //有外部内存写入权限，将缓存设置在外部存储卡中，否则是应用内缓存
        if (hasPermission(context)) {
            System.out.println("Glide缓存位置：/cll/cache/image");
            File storageDirectory = Environment.getExternalStorageDirectory();
            String cachePath = storageDirectory + "/cll/cache/image";
            builder.setDiskCache(new DiskLruCacheFactory(cachePath, memoryCacheSizeBytes));
        } else {
            //设置内存缓存大小,默认缓存位置
            System.out.println("Glide缓存位置：默认应用内");
            builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
        }
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

    }


    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    //是否有写入内存权限
    private boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        }
        return true;
    }

}
