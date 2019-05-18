package com.yanlong.im.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;

import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * 缓存合成图片
 */
public class GroupHeadImageUtil {


    /***
     * 只能合成fresco已经加载过的图
     * @param url
     * @return
     */
    public static File synthesis(String... url) {
      List<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < url.length; i++) {
            FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory()
                    .getMainFileCache().getResource(new SimpleCacheKey(url[i]));
            if (resource != null) {
                File file = resource.getFile();
                if(file.exists()){
                   Bitmap bt=BitmapFactory.decodeFile(file.getAbsolutePath());
                   if(bt!=null){
                       bitmaps.add(bt) ;
                   }


                }
            }
        }

        return save2File(addBitmaps(10, bitmaps.toArray(new Bitmap[]{})));
    }

    private static File save2File(Bitmap bt) {
        FileOutputStream out = null;
        File file;
           // 获取SDCard指定目录下
            String dir = AppConfig.APP_CONTEXT.getCacheDir().getAbsolutePath()+"/group/";
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
                if(out!=null){
                    out.flush();
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        LogUtil.getLog().i("img","图片路径>>>>>>"+file.getAbsoluteFile());
        return file;

    }


    private static Bitmap addBitmaps(int margin, Bitmap... bitmaps) {

        int row = 1;
        int col = 0;
        int width = 0;
        int height = 0;
        int totalHeight = 0;
        int length = bitmaps.length;
        if (length > 3) {
            row = length % 3 == 0 ? length / 3 : length / 3 + 1;
            col = 3;
        } else {
            row = 1;
            col = length;
        }
        for (int i = 0; i < length; i++) {
            height = Math.max(height, bitmaps[i].getHeight());
        }
        totalHeight = height * row;
        totalHeight += (row - 1) * margin;

        for (int i = 0; i < col; i++) {
            width += bitmaps[i].getWidth();
            width += margin;
        }
        width -= margin;
        Bitmap result = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        for (int i = 0; i < row; i++) {
            int left = 0;
            for (int i1 = 0; i1 < col; i1++) {
                if (i * col + i1 >= length) {
                    break;
                }
                if (i > 0) {
                    if (i1 > 0) {
                        left += bitmaps[i * col + i1 - 1].getWidth();
                        left += margin;
                        int top = (height + margin) * i;
                        canvas.drawBitmap(bitmaps[i * col + i1], left, top, null);
                    } else {
                        left = 0;
                        int top = (height + margin) * i;
                        canvas.drawBitmap(bitmaps[i * col + i1], left, top, null);
                    }
                } else {
                    //第1行
                    if (i1 > 0) {
                        left += bitmaps[i1 - 1].getWidth();
                        left += margin;
                        canvas.drawBitmap(bitmaps[i1], left, 0, null);
                    } else {
                        left = 0;
                        canvas.drawBitmap(bitmaps[i1], left, 0, null);
                    }
                }
            }
        }
        return result;
    }
}
