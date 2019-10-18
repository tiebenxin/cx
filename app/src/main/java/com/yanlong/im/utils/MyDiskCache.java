package com.yanlong.im.utils;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.engine.cache.SafeKeyGenerator;
import com.bumptech.glide.signature.EmptySignature;
import com.luck.picture.lib.glide.OriginalKey;

import java.io.File;



public class MyDiskCache {


    public File getFile(String path){
        try{
//            DiskLruCache diskLruCache=DiskLruCache.open(new File(""),0,0,0);
//            diskLruCache.get()

        }catch (Exception e){

        }

        return new File(path);
    }


    public static String getFileNmae(String url){
        OriginalKey originalKey = new OriginalKey(url, EmptySignature.obtain());
        SafeKeyGenerator safeKeyGenerator = new SafeKeyGenerator();
        String safeKey = safeKeyGenerator.getSafeKey(originalKey);
        if (url.endsWith("mp4")){

        }else if(url.endsWith("png")||url.endsWith("jpg")||url.endsWith("gif")){

        }else if(url.endsWith("caf")){

        }
//        DiskLruCache diskLruCache = DiskLruCache.open(new File(cachePath, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR), 1, 1, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
//        DiskLruCache.Value value = diskLruCache.get(safeKey);

        return safeKey;
    }

}
