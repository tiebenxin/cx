package com.luck.picture.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PicSaveUtils {
    public static void saveImgLoc(Context mContext, Bitmap bmp, String bitName){
        // 首先保存图片
//        if (!StringUtil.isNotNull(bitName)){
        bitName= SystemClock.currentThreadTimeMillis()+"";
//        }else{
//            bitName=bitName.substring(bitName.lastIndexOf("\\/")+1,bitName.lastIndexOf("\\."));
//        }
        File appDir = new File(Environment.getExternalStorageDirectory(),
                "yanlong");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = bitName + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.e("TAG",file.getAbsolutePath());
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), bitName, null);
//            Toast.show(mContext,"保存成功至"+file.getAbsolutePath());
            Toast.makeText(mContext,"保存成功至"+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        setPhotoFile(file);
    }
}
