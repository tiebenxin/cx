package net.cb.cb.library.manager;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liszt
 * @date 2019/9/28
 * Description  缓存文件管理类，图片，语音，视频，其他
 */
public class FileManager {
    public static final String CACHE_ROOT = "/changxin";
    public static final String CACHE = "/cache";
    public static final String IMAGE = "/image";
    public static final String VOICE = "/voice";
    public static final String VIDEO = "/video";
    public static final String OTHER = "/other";
    private static FileManager INSTACNCE;

    public static FileManager getInstance() {
        if (INSTACNCE == null) {
            INSTACNCE = new FileManager();
        }
        return INSTACNCE;
    }


    static {
        initCacheFile();
    }

    /*
     * 初始化本地缓存文件夹
     * */
    private static void initCacheFile() {
        File storageFile = Environment.getExternalStorageDirectory();
        File packFile = new File(storageFile.getAbsolutePath() + CACHE_ROOT);
        if (!packFile.exists()) {
            packFile.mkdir();
        }
        File cacheFile = new File(storageFile.getAbsolutePath() + CACHE_ROOT + CACHE);
        if (!cacheFile.exists()) {
            cacheFile.mkdir();
        }

        File imageFile = new File(storageFile.getAbsolutePath() + CACHE_ROOT + CACHE + IMAGE);
        if (!imageFile.exists()) {
            imageFile.mkdir();
        }

        File voiceFile = new File(storageFile.getAbsolutePath() + CACHE_ROOT + CACHE + VOICE);
        if (!voiceFile.exists()) {
            voiceFile.mkdir();
        }

        File videoFile = new File(storageFile.getAbsolutePath() + CACHE_ROOT + CACHE + VIDEO);
        if (!videoFile.exists()) {
            videoFile.mkdir();
        }

        File otherFile = new File(storageFile.getAbsolutePath() + CACHE_ROOT + CACHE + OTHER);
        if (!otherFile.exists()) {
            otherFile.mkdir();
        }
    }

    public String getImageCachePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + CACHE_ROOT + CACHE + IMAGE;
    }

    public String getVoiceCachePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + CACHE_ROOT + CACHE + VOICE;
    }

    public String getVideoCachePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + CACHE_ROOT + CACHE + VIDEO;
    }

    public String getOtherRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + CACHE_ROOT + CACHE + OTHER;
    }


    //存储pc同步消息
    public File saveMsgFile(byte[] bytes) {
        String filePath = getOtherRoot();
        String fileName = System.currentTimeMillis() + ".txt";

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes, 0, bytes.length);
            bos.flush();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public static byte[] readFileBytes(File file) {
        if (!file.exists()) {
            return null;
        }
        BufferedInputStream fw = null;
        try {
            fw = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = new byte[(int) file.length()];
            fw.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fw) {
                try {
                    fw.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public String createImagePath() {
        return getImageCachePath() + "/" + System.currentTimeMillis() + ".jpg";
    }

}
