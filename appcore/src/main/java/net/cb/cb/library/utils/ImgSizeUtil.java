package net.cb.cb.library.utils;

import android.graphics.BitmapFactory;

import java.io.File;
import java.text.DecimalFormat;

/***
 * 图片大小获取
 */
public class ImgSizeUtil {
    public static ImageSize getAttribute(String loclUrl) {
        loclUrl=loclUrl.replace("file://","");
        //获取Options对象
        BitmapFactory.Options options = new BitmapFactory.Options();
        //仅做解码处理，不加载到内存
        options.inJustDecodeBounds = true;
        //解析文件
        BitmapFactory.decodeFile(loclUrl, options);
        //获取宽高

        ImageSize imageSize = new ImageSize();
        imageSize.setHeight(options.outHeight);
        imageSize.setWidth(options.outWidth);
        long size = new File(loclUrl).length();
        imageSize.setSize(size);
        imageSize.setSizeStr(formatFileSize(size));
        return imageSize;

    }

    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        String wrongSize = "0K";
        if (fileS == 0) {
            return wrongSize;
        }
       /* if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        }*/
        else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }


        return fileSizeString;

    }

    public static class ImageSize {
        private int width;
        private int height;
        private long size;
        private String sizeStr;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getSizeStr() {
            return sizeStr;
        }

        public void setSizeStr(String sizeStr) {
            this.sizeStr = sizeStr;
        }
    }

}
