package com.yanlong.im.view.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.ui.BaseBindActivity;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityShowBigfaceBinding;
import com.yanlong.im.utils.GlideOptionsUtil;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-01-11
 * @updateAuthor
 * @updateDate
 * @description 显示表情大图
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class ShowBigFaceActivity extends BaseBindActivity<ActivityShowBigfaceBinding> {

    @Override
    protected int setView() {
        return R.layout.activity_show_bigface;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        String value = getIntent().getExtras().getString(Preferences.DATA);
        if (!TextUtils.isEmpty(value)) {
            Glide.with(this).load(Integer.parseInt(value)).listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).apply(GlideOptionsUtil.defImageOptions()).into(bindingView.imgFace);
        }
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
    }

    @Override
    protected void loadData() {

    }
}
