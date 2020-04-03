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


    public byte[] getFileToByte(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        List<byte[]> list = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            int bytesRead = 0;
            while ((bytesRead = bis.read(buffer)) != -1) {
                String s = new String(buffer, 0, bytesRead);
                stringBuffer.append(s);
//                System.out.println("PC同步--2--" + s);
                list.add(buffer);
            }
//            if (list.size() > 0) {
//                return listToBytes(list);
//            }
            return hexStrToByte(stringBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return null;
    }

    /***
     * 合并数组
     * @param values
     * @return
     */
    public byte[] listToBytes(List<byte[]> values) {
        int length_byte = 0;
        for (int i = 0; i < values.size(); i++) {
            length_byte += values.get(i).length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.size(); i++) {
            byte[] b = values.get(i);
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            if (i == length - 1) {
                sb.append(hex);
            } else {
                sb.append(hex + " ");
            }
        }
        return sb.toString();
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public byte[] hexStrToByte(String str) {
        String[] arr = str.split(" ");
        if (arr != null) {
            int len = arr.length;
            byte[] bytes = new byte[len];
            for (int i = 0; i < len; i++) {
                bytes[i] = hexToByte(arr[i]);
            }
            return bytes;
        }
        return null;
    }
}
