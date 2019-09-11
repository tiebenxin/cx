package com.yanlong.im.utils;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;


/**
 * @创建人 shenxin
 * @创建时间 2019/9/11 0011 14:36
 */
@GlideModule
public class MyGlide extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);

        builder.setDiskCache(new DiskLruCacheFactory(context.getCacheDir().getAbsolutePath(), 300 * 1024 * 1024));

    }
}