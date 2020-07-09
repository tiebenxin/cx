package com.yanlong.im.user.ui.freeze;

import android.os.Bundle;

import com.example.nim_lib.ui.BaseBindActivity;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityAppealIngBinding;

import net.cb.cb.library.view.ActionbarView;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-7-6
 * @updateAuthor
 * @updateDate
 * @description 账号申诉
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class AppealIngActivity extends BaseBindActivity<ActivityAppealIngBinding> {

    @Override
    protected int setView() {
        return R.layout.activity_appeal_ing;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
    }

    @Override
    protected void loadData() {

    }
}