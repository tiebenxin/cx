package net.cb.cb.library.utils;

import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.luck.picture.lib.tools.PictureFileUtils;

import net.cb.cb.library.bean.VideoSize;
import net.cb.cb.library.manager.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

/***
 * 图片大小获取
 */
public class ImgSizeUtil {
    public static ImageSize getAttribute(String loclUrl) {
        if (TextUtils.isEmpty(loclUrl)) {
            return null;
        }
        loclUrl = loclUrl.replace("file://", "");
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

    public static long getVideoSize(String mUri) {
        long size = 0;
        File f = new File(mUri);
        FileChannel fc = null;
        FileInputStream fis = null;
        if (f.exists() && f.isFile()) {
            try {
                fis = new FileInputStream(f);
                fc = fis.getChannel();
                size = fc.size();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static double formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.valueOf(df.format((double) fileS / 1048576));
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


    //获取视频参数信息
    public static VideoSize getVideoAttribute(String file) {
        VideoSize size = null;
        try {
            size = new VideoSize();
            android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
            retriever.setDataSource(file);
            long length = getVideoSize(file);
            size.setSize(length);
            String duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            size.setDuration(Long.parseLong(duration));
            int orientation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            long width = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            long height = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            if (orientation == 90) {
                size.setWidth(Math.min(width, height));
                size.setHeight(Math.max(width, height));
            } else {
                size.setWidth(Math.max(width, height));
                size.setHeight(Math.min(width, height));
            }
            String bgUrl = getVideoAttBitmap(file);
            if (!TextUtils.isEmpty(bgUrl)) {
                size.setBgUrl(bgUrl);
            }
        } catch (Exception e) {
        }

        return size;
    }

    private static String getVideoAttBitmap(String mUri) {
        File file = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
            } else {
            }
            file = new File(FileManager.getInstance().createImagePath());
            PictureFileUtils.saveBitmapFile(mmr.getFrameAtTime(), file);
        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return file.getAbsolutePath();
    }

}
