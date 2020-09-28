package com.luck.picture.lib.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/***
 * 缓存合成图片
 */
public class GroupHeadImageUtil {

    public static File save2File(Context context, Bitmap bt) {
        FileOutputStream out = null;
        File file;
        // 获取SDCard指定目录下
        String dir = context.getCacheDir().getAbsolutePath() + "/group/";
        File dirFile = new File(dir);  //目录转化成文件夹
        if (!dirFile.exists()) {                //如果不存在，那就建立这个文件夹
            dirFile.mkdirs();
        }                            //文件夹有啦，就可以保存图片啦
        file = new File(dir, System.currentTimeMillis() + ".jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名

        try {
            out = new FileOutputStream(file);
            bt.compress(Bitmap.CompressFormat.JPEG, 90, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.flush();
                out.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
