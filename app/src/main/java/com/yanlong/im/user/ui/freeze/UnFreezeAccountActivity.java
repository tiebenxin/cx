package com.yanlong.im.user.ui.freeze;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityUnfreezeAccountBinding;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/5/25
 * Description 解冻账号
 */
public class UnFreezeAccountActivity extends AppActivity {

    private ActivityUnfreezeAccountBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_unfreeze_account);
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        ui.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnFreezeAccountActivity.this, UnFreezeAccountIdentifyActivity.class);
                startActivity(intent);
            }
        });
    }
}
