package com.yanlong.im.view.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.ui.BaseBindActivity;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityShowBigfaceBinding;

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
        if(!TextUtils.isEmpty(value)){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Integer.parseInt(value));
            bindingView.imgFace.setImageBitmap(bitmap);
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
