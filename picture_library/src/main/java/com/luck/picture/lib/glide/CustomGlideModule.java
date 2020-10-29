package com.luck.picture.lib.glide;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.signature.EmptySignature;

import java.io.File;
import java.io.IOException;

/**
 * @创建人 shenxin
 * @创建时间 2019/9/20 0020 11:19
 * 备注：需要依赖 annotationProcessor 'com.github.bumptech.glide:compiler:4.5.0'， 且在同一个模块，否则GlideModule注解不生效
 */
@GlideModule
public class CustomGlideModule extends AppGlideModule {

    private static int memoryCacheSizeBytes = 1024 * 1024 * 100;
    private static OriginalKey originalKey;
    private static SafeKeyGenerator safeKeyGenerator;
    private static String safeKey;
    private static File storageDirectory = Environment.getExternalStorageDirectory();
    private static String cachePath = storageDirectory + "/changxin/cache/image/";
    private static DiskLruCache diskLruCache;
    private static DiskLruCache.Value value;

    public void applyOptions(Context context, GlideBuilder builder) {
        //有外部内存写入权限，将缓存设置在外部存储卡中，否则是应用内缓存
        try {
            if (hasPermission(context)) {
                if (Environment.isExternalStorageEmulated()) {
                    builder.setDiskCache(new DiskLruCacheFactory(cachePath, memoryCacheSizeBytes * 5));
                } else {
                    builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
                }
            } else {
                builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getCacheFile(String url) {
        originalKey = new OriginalKey(url, EmptySignature.obtain());
        safeKeyGenerator = new SafeKeyGenerator();
        safeKey = safeKeyGenerator.getSafeKey(originalKey);
        try {
            diskLruCache = DiskLruCache.open(new File(cachePath), 1, 1, memoryCacheSizeBytes * 5);
            value = diskLruCache.get(safeKey);
            if (value != null) {
                return value.getFile(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 适用于缩略图
     *
     * @param url
     * @return
     */
    public static Bitmap getCacheBitmap(String url) {
        File localPath = getCacheFile(url);
        Bitmap bitmap = null;
        try {
            if (localPath != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(localPath.getAbsolutePath(), options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
//        registry.register(FrameSequence.class, FrameSequenceDrawable.class, new GifDrawableTranscoder())
//                .append(InputStream.class, FrameSequence.class, new MyGifDecoder());
//        registry.prepend(Registry.BUCKET_GIF, InputStream.class, FrameSequenceDrawable.class, new GifDecoder(glide.getBitmapPool()));
    }


    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    //是否有写入内存权限
    public static boolean hasPermission(Context context) {
        if (hasSDCard()) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean hasSDCard() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

}
