package com.luck.picture.lib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.luck.picture.lib.tools.PictureFileUtils.APP_NAME;

public class PicSaveUtils {
    public static void saveImgLoc(Context mContext, Bitmap bmp, String bitName) {
        // 首先保存图片
        bitName = SystemClock.currentThreadTimeMillis() + "";
        File appDir = new File(Environment.getExternalStorageDirectory(),
                APP_NAME);
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
            Log.e("TAG", file.getAbsolutePath());
            //TODO:执行MediaStore.Images.Media.insertImage会在相册中产生两张图片
//            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), bitName, null);
            sendBroadcast(file, mContext);
//            Toast.makeText(mContext, "保存成功至" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        setPhotoFile(file);
    }

    public static void sendBroadcast(File dirPath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(dirPath);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcast(String dirPath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(dirPath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}
