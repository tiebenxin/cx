package net.cb.cb.library.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;

import net.cb.cb.library.dialog.DialogCommon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/7/6 0006
 * @updateAuthor
 * @updateDate
 * @description 文件上传工具类
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class UpLoadFileUtil {
    public static UpLoadFileUtil getInstance() {
        return UpLoadFileUtilHolder.INSTANCE;
    }

    private static class UpLoadFileUtilHolder {
        private static UpLoadFileUtil INSTANCE = new UpLoadFileUtil();
    }

    private HashMap<String, String> netFile;// 上传成功文件
    private int count = 0;//上传文件个数
    private Context context;
    private List<LocalMedia> mediaList;
    private OnUploadFileListener listener;

    public void upLoadFile(Context context, List<LocalMedia> mediaList, OnUploadFileListener listener) {
        this.context = context;
        this.mediaList = mediaList;
        this.listener = listener;
        initData();
    }

    private void initData() {
        if (mediaList == null) {
            mediaList = new ArrayList<>();
        }

        netFile = new HashMap<>();
        if (mediaList.size() > 0) {
            if (count == 0) {
                count = mediaList.size();
            }
            for (int i = 0; i < mediaList.size(); i++) {
                String path = getMediaPath(mediaList.get(i));
                if (TextUtils.isEmpty(path)) {
                    continue;
                }
                if (path.startsWith("http")) {// 网络图片、语音不需要上传
                    count--;
                } else {
                    uploadFile(path);//上传图片
                }
            }
        }
    }

    private String getMediaPath(LocalMedia localMedia) {
        String path = localMedia.getPath();
        if (TextUtils.isEmpty(path)) {
            path = localMedia.getCutPath();
        }
        return path;
    }

    private void uploadFile(final String file) {
        new UpFileAction().upFile(UpFileAction.PATH.IMG, context, new UpFileUtil.OssUpCallback() {

            @Override
            public void success(final String url) {
                netFile.put(file, url);
                if (--count == 0) {
                    listener.onUploadFile(netFile);
                }
            }

            @Override
            public void fail() {
                listener.onFail();
            }

            @Override
            public void inProgress(long progress, long zong) {

            }
        }, file);
    }


    public interface OnUploadFileListener {
        void onUploadFile(HashMap<String, String> netFile);

        void onFail();
    }

    public interface OnFileUrlListener {
        void onFileUrl(String key, String file);
    }
}
