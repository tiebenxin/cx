package com.yanlong.im.user.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.ScreenUtils;
import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/26 0026 9:27
 */
public class FeedbackShowImageActivity extends AppActivity {
    public static final String URL = "url";
    private HeadView headView;
    private SimpleDraweeView imageView;
    public final static String POSTION = "postion";
    private int postion = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_show_image);
        initView();
        initEvent();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        imageView = findViewById(R.id.image_view);
        headView.getActionbar().getBtnRight().setVisibility(View.VISIBLE);
        headView.getActionbar().getBtnRight().setImageResource(R.mipmap.icon_image_delect);
        postion = getIntent().getIntExtra(POSTION, 0);
        imageView.setImageURI(getIntent().getStringExtra(URL));

    }

    private void initEvent() {
        headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                setResult(RESULT_OK, new Intent().putExtra(POSTION, postion));
                finish();
            }
        });
    }

}