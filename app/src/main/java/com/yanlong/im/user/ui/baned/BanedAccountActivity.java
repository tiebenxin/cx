package com.yanlong.im.user.ui.baned;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityBanAccountBinding;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/6/8
 * Description
 */
public class BanedAccountActivity extends AppActivity {

    private ActivityBanAccountBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_ban_account);
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        String avatar = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IMAGE_HEAD).get4Json(String.class);
        Glide.with(this).load(avatar).apply(GlideOptionsUtil.headImageOptions()).into(ui.ivAvatar);

        ui.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpGetBalance();
            }
        });
    }

    private void httpGetBalance() {
        boolean hasBalance = true;
        if (hasBalance) {
            go(WithdrawBalanceActivity.class);
        } else {
            go(WithDrawFailActivity.class);
        }
    }
}
