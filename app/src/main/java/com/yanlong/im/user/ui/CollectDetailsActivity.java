package com.yanlong.im.user.ui;

import android.os.Bundle;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：收藏详情
 * @Date：2020/4/28
 * @by zjy
 * @备注：
 */
public class CollectDetailsActivity extends AppActivity {

    private HeadView mHeadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_details);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);

    }

    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {

            }
        });

    }
}
