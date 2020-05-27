package com.yanlong.im.user.ui.freeze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @author Liszt
 * @date 2020/5/21
 * Description 冻结账号
 */
public class FreezeAccountActivity extends AppActivity {

    private TextView tvConfirm;
    private HeadView headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeze_account);
        headView = findViewById(R.id.headView);
        tvConfirm = findViewById(R.id.tv_confirm);
        headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FreezeAccountActivity.this, FreezeAccountIdentifyActivity.class);
                startActivity(intent);
            }
        });
    }
}
