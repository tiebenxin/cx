package com.yanlong.im.user.ui.logout;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.jrmf360.tools.utils.ThreadUtil;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityLogoutStepBinding;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Liszt
 * @date 2020/6/4
 * Description
 */
public class LogoutAccountStepActivity extends AppActivity {

    private ActivityLogoutStepBinding ui;
    private UserAction userAction = new UserAction();
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        ui = DataBindingUtil.setContentView(this, R.layout.activity_logout_step);
        ui.tvPhone.setText(phone);
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        ui.tvGetVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountDownUtil.getTimer(60, ui.tvGetVerificationCode, "获取验证码", LogoutAccountStepActivity.this, new CountDownUtil.CallTask() {
                    @Override
                    public void task() {
                        taskGetSms(phone);
                    }
                });
            }
        });

        ui.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = ui.etIdentifyingCodeContent.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.show("验证码不能为空");
                    return;
                }
                showConfirmDialog(phone, code);

            }
        });
    }

    private void showConfirmDialog(String phone, String code) {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon dialogConfirm = new DialogCommon(LogoutAccountStepActivity.this);
                dialogConfirm.setTitleAndSure(false, false)
                        .setLeft("确定")
                        .setRight("取消")
                        .setContent("确定要注销账号吗？", true)
                        .setListener(new DialogCommon.IDialogListener() {
                            @Override
                            public void onSure() {
                                httpLogoutAccount(phone, code);
                            }

                            @Override
                            public void onCancel() {

                            }
                        }).show();
            }
        });
    }

    private void httpLogoutAccount(String phone, String code) {
        userAction.logoutAccount(code, phone, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    ToastUtil.show(context, "注销账号失败");
                    return;
                }
                ToastUtil.show(LogoutAccountStepActivity.this, response.body().getMsg());
            }
        });
    }

    private void taskGetSms(String phone) {
        userAction.getSms(phone, "deactivate", new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    ToastUtil.show(context, "获取短信验证码失败");
                    return;
                }
                if (!response.body().isOk()) {
                    CountDownUtil.cancelTimer();
                }
                ToastUtil.show(LogoutAccountStepActivity.this, response.body().getMsg());
            }
        });
    }
}
