package com.yanlong.im.view.face;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.ui.BaseBindActivity;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityFacePreviewBinding;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.FileConfig;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-01-10
 * @updateAuthor
 * @updateDate
 * @description 表情预览
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class FacePreviewActivity extends BaseBindActivity<ActivityFacePreviewBinding> {

    private String mFacePath, mFaceName;
    private String mLocationPath;
    private ByteArrayOutputStream mByteArrayOutputStream;

    @Override
    protected int setView() {
        return R.layout.activity_face_preview;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mFacePath = getIntent().getExtras().getString(Preferences.FACE_PATH, "");
        mFaceName = getIntent().getExtras().getString(Preferences.FACE_NAME, "");
        if (!TextUtils.isEmpty(mFacePath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            BitmapFactory.decodeFile(mFacePath, options);
            Log.i("1212", "outWidth:" + options.outWidth + " outHeight:" + options.outHeight);
            if (options.outHeight > 400 || options.outHeight > 400) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(mFacePath);
                        if (file != null && file.exists()) {
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(mFacePath);
                                Bitmap bitmap = BitmapFactory.decodeStream(fis);

                                // 尺寸压缩倍数,值越大，图片尺寸越小
                                int ratio = 4;
                                // 压缩Bitmap到对应尺寸
                                Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / ratio, bitmap.getHeight() / ratio, Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(result);
                                Rect rect = new Rect(0, 0, bitmap.getWidth() / ratio, bitmap.getHeight() / ratio);
                                canvas.drawBitmap(bitmap, null, rect, null);

                                Log.i("1212", "Width:" + result.getWidth() + " Height:" + result.getHeight());
                                mByteArrayOutputStream = new ByteArrayOutputStream();
                                result.compress(Bitmap.CompressFormat.PNG, 100, mByteArrayOutputStream);
                                mLocationPath = saveBitmap(result);

                                loadImage(result);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                loadImage(mFacePath);
            }
        }
    }

    private void loadImage(Object obj) {
        Glide.with(FacePreviewActivity.this).load(obj).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).apply(GlideOptionsUtil.imageOptions())
                .into(bindingView.imgFace);
    }

    @Override
    protected void initEvent() {
        bindingView.imgBack.setOnClickListener(o -> {
            finish();
        });
        bindingView.txtConfrim.setOnClickListener(o -> {
            if (!ViewUtils.isFastDoubleClick()) {
                if(!TextUtils.isEmpty(mLocationPath)){
                    uploadFile(mLocationPath);
                }else{
                    uploadFile(mFacePath);
                }
            }
        });
    }

    @Override
    protected void loadData() {
    }

    /**
     * 保存bitmap到本地
     *
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Bitmap mBitmap) {
        File filePic;
        try {
            filePic = new File(FileConfig.PATH_FACE_CACHE + System.currentTimeMillis() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    /**
     * 上传文件
     *
     * @param file
     */
    private void uploadFile(String file) {
        alert.show();
        UpFileUtil.OssUpCallback callback = new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                alert.dismiss();
                Intent intent = new Intent();
                intent.putExtra(Preferences.FACE_PATH, mFacePath);
                intent.putExtra(Preferences.FACE_NAME, mFaceName);
                intent.putExtra(Preferences.FACE_SERVER_PATH, url);

                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void fail() {
                alert.dismiss();
                ToastUtil.show(getContext(), "上传失败!");
            }

            @Override
            public void inProgress(long progress, long zong) {

            }
        };
        new UpFileAction().upFile(UpFileAction.PATH.IMG, getContext(), callback, file);
    }
}
