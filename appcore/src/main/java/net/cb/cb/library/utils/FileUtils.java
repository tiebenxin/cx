package net.cb.cb.library.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.HashSet;

/**
 * @类名：文件操作工具类
 * @Date：2020/1/10
 * @by zjy
 * @备注：
 *  ->方法列表
 *  1 根据Uri获取文件path路径
 *  2 获取文件名（含后缀）
 *  3 获取文件后缀
 *  4 获取文件大小并转换，按照指定转换的类型，显示文件大小分别显示B K M G
 *  5 判断本地文件是否存在
 *  6 获取文件的MIME类型
 *  7 判断文件是否为图片/视频
 *  8 获取文件重命名 { 如123.txt若有重名则依次保存为123.txt(1) 123.txt(2) }
 */
public class FileUtils {

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * 根据Uri获取文件path路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    path = getDataColumn(context, contentUri, null, null);
                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            }
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * 获取文件名（含后缀）
     *
     * @param path 文件路径
     * @return
     */
    public static String getFileName(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        int start = path.lastIndexOf("/");
        if (start != -1) {
            return path.substring(start + 1);
        } else {
            return "";
        }
    }


    /**
     * 获取文件后缀
     *
     * @param fileName 传入文件名
     * @return
     */
    public static String getFileSuffix(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            int pointPosition = fileName.lastIndexOf(".");//查询尾部逗号位置，截取后缀
            if (pointPosition != -1) { //查不到则返回-1
                return fileName.substring(pointPosition + 1);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }


    /**
     * 获取文件大小
     *
     * @param filePath
     * @param sizeType
     * @return
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLog().e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }


    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            LogUtil.getLog().e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File file) throws Exception {
        long size = 0;
        File flist[] = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }


    /**
     * 文件消息显示规则处理，根据文件大小分别显示B K M G
     *
     * @param fileSize
     * @return
     */
    public static String getFileSizeString(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + "B";
        } else if (fileSize > 1024 && fileSize < 1048576) {
            return FormetFileSize(fileSize, SIZETYPE_KB) + "K";
        } else if (fileSize > 1048576 && fileSize < 1073741824) {
            return FormetFileSize(fileSize, SIZETYPE_MB) + "M";
        } else {
            return FormetFileSize(fileSize, SIZETYPE_GB) + "G";
        }
    }


    /**
     * 判断本地文件是否存在
     * @param filePath
     * @return
     */
    public static boolean fileIsExist(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 检测文件MIME类型
     */
    public static final String[][] MIME_MapTable={
            //{后缀名，    MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".docx",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".JPEG",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".pptx",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xls",    "application/vnd.ms-excel"},
            {".xlsx",    "application/vnd.ms-excel"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };

    /**
     * 获取文件的MIME类型
     * @param file
     * @return
     */
    public static String getMIMEType(File file) {
        //无后缀名的未知文件，6.0以下不涉及权限问题，预期效果是调用全部支持格式的程序列表，部分手机无法兼容，会报"解析软件包时出现问题"
        //TODO 因此暂时将无格式的文件，默认采用文本格式打开，可用程序为浏览器、文本等
        String type="text/plain";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        //获取文件的后缀名
        String fileType = fName.substring(dotIndex,fName.length()).toLowerCase();
        if(fileType == null || "".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){
            if(fileType.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }


    /**
     * 判断文件是否为图片/视频
     * @param fileFormat
     * @return
     */
    public static boolean isImage(String fileFormat){
        if(fileFormat.equals("bmp") || fileFormat.equals("gif") || fileFormat.equals("jpeg") || fileFormat.equals("JPEG")
                || fileFormat.equals("jpg") || fileFormat.equals("png") || fileFormat.equals("webp") || fileFormat.equals("psd")){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isVideo(String fileFormat){
        if(fileFormat.equals("3gp") || fileFormat.equals("asf") || fileFormat.equals("avi") || fileFormat.equals("m4u")
                || fileFormat.equals("m4v") || fileFormat.equals("mov") || fileFormat.equals("mp4") || fileFormat.equals("mpe")
                || fileFormat.equals("mpeg") || fileFormat.equals("mpg") || fileFormat.equals("mpg4") || fileFormat.equals("wmv")
                || fileFormat.equals("flv") || fileFormat.equals("rmvb")){
            return true;
        }else {
            return false;
        }
    }


    /**
     * 获取文件重命名 { 如123.txt若有重名则依次保存为123.txt(1) 123.txt(2) }
     * @param fileName 文件名
     */
    public static String getFileRename(String fileName) {
        //获取文件名和后缀，如123.txt
        String oldFileName;//文件名
        String fileSuffix = getFileSuffix(fileName);//后缀
        //后缀不为空，则截取出文件名；后缀为空，则取原文件名
        if(!TextUtils.isEmpty(fileSuffix)){
            oldFileName = fileName.substring(0,fileName.indexOf("."));
        }else {
            oldFileName = fileName;
        }
        //先去下载路径寻找目标文件
        File f = new File(FileConfig.PATH_DOWNLOAD);
        //判断路径是否存在，拿到下载路径所有文件集合，对比名称
        if (f.exists()) {
            File[] files = f.listFiles();
            HashSet<String> hashSet = new HashSet<>();
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    hashSet.add(name);
                }
            }
            int a = 1;
            //循环查找，集合不含该名称则判定为新文件，直接返回；若有重名文件，则变量+1，并重命名出新的文件名
            while (true) {
                if (a > 1) {
//                    String[] split = fileName.split("\\.");
                    if(!TextUtils.isEmpty(fileSuffix)){
                        fileName = oldFileName + "(" + a + ")." + fileSuffix;
                    }else {
                        fileName = oldFileName + "(" + a + ")";
                    }

                }
                if (!hashSet.contains(fileName)) {
                    return fileName;
                } else {
                    a++;
                }
            }
        }
        return fileName;
    }

}
