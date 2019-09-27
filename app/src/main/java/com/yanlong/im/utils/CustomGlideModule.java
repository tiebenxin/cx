package com.yanlong.im.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @创建人 shenxin
 * @创建时间 2019/9/20 0020 11:19
 */
@GlideModule
public class CustomGlideModule extends AppGlideModule {

    public void applyOptions(Context context, GlideBuilder builder) {
        int memoryCacheSizeBytes = 1024 * 1024 * 100;
        //设置内存缓存大小
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

    }


    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
