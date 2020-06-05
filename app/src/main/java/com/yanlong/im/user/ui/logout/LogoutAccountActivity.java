package com.yanlong.im.user.ui.logout;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityLogoutBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;

import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/5/20
 * Description 注销常信账号
 */
public class LogoutAccountActivity extends AppActivity {
    private ActivityLogoutBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_logout);
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
                IUser user = UserAction.getMyInfo();
                if (user != null && !TextUtils.isEmpty(user.getPhone())) {
                    toNextActivity(user.getPhone());
                } else {
                    ToastUtil.show("该账号无手机号，不能注销");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CountDownUtil.getLogoutTimer(10, ui.tvConfirm, "申请注销", this, new CountDownUtil.CallTask() {
            @Override
            public void task() {

            }
        });
    }


    private void toNextActivity(String phone) {
        Intent intent = new Intent(LogoutAccountActivity.this, LogoutAccountStepActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);
        finish();
    }

}
