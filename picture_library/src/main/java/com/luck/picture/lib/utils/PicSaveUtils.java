package com.luck.picture.lib.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import com.luck.picture.lib.tools.PictureFileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import static com.luck.picture.lib.tools.PictureFileUtils.APP_NAME;

public class PicSaveUtils {

    private static final String TAG = PicSaveUtils.class.getSimpleName();

    public static boolean saveImgLoc(Context mContext, Bitmap bmp, String bitName) {
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
//            if (DeviceUtils.isViVoAndOppo()) {
//                String dirPath = PictureFileUtils.createDir(mContext,
//                        fileName, "/Pictures");
//                PictureFileUtils.copyFile(file.getAbsolutePath(), dirPath);
//                System.out.println("DeviceUtils" + "--insert图片到相册--" + dirPath);
//                sendBroadcast(new File(dirPath), mContext);
//            } else {
//                sendBroadcast(file, mContext);
//            }
            System.out.println("DeviceUtils" + "--insert图片到相册--" + file);
            sendBroadcast(file, mContext);

//            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveOriginImage(String url) {

    }

    /***
     * 文件保存本地,需要网络
     * @param u
     * @param path
     * @throws IOException
     */
    public static void saveFileLocl(URL u, String path) throws IOException {
        if (!new File(path).exists()) {
            byte[] buffer = new byte[1024 * 8];
            int read;
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin;
            bin = new BufferedInputStream(u.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(path));
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                long speed = ava / (System.currentTimeMillis() - start);
            }
            bout.flush();
            bout.close();
            Log.d(TAG, "showLoadingImage: 不存在,创建");
        } else {
            Log.d(TAG, "showLoadingImage: 存在");
        }
    }

    /***
     * 获取文件后缀
     * @param urlPath
     * @return
     */
    public static String getFileExt(String urlPath) {
        String fName = urlPath.trim();
        //http://e7-test.oss-cn-beijing.aliyuncs.com/Android/20190802/fe85b909-0bea-4155-a92a-d78052e8638c.png/below-200k
        int index = fName.lastIndexOf("/");
        if (fName.lastIndexOf(".") > index) {
            return fName.substring(index + 1);
        } else {
            String name = fName.substring(fName.lastIndexOf("/", index - 1) + 1);
            name = name.replace("/", "_");
            return name;
        }
    }

    public static void sendBroadcast(File dirPath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(dirPath);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

//    public static void sendBroadcast(String dirPath, Context context) {
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(new File(dirPath));
//        intent.setData(uri);
//        context.sendBroadcast(intent);
//    }

    public static void scanFile(File file, Context context) {
        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                System.out.println("扫描成功");
            }
        });
    }
}
