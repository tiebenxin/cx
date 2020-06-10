package com.yanlong.im.user.ui.freeze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityFreezeIdentifyBinding;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Liszt
 * @date 2020/5/25
 * Description 解冻账号-手机验证界面
 */
public class UnFreezeAccountIdentifyActivity extends AppActivity {

    private ActivityFreezeIdentifyBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_unfreeze_identify);
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
                initCountDownUtil();
            }
        });
    }

    private void initCountDownUtil() {
        final String phone = ui.etPhoneContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(UnFreezeAccountIdentifyActivity.this, "请填写手机号码");
            return;
        }
        if (!CheckUtil.isMobileNO(phone)) {
            ToastUtil.show(this, "手机号格式不正确");
            return;
        }
        CountDownUtil.getTimer(60, ui.tvGetVerificationCode, "获取验证码", this, new CountDownUtil.CallTask() {
            @Override
            public void task() {
                taskGetSms(phone);
            }
        });
    }

    //TODO:服务端需要重新添加businessType
    private void taskGetSms(String phone) {
        new UserAction().smsCaptchaGet(phone, "xxxx", new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    ToastUtil.show(context, "冻结失败");
                    return;
                }
                if (!response.body().isOk()) {
                    CountDownUtil.cancelTimer();
                }
                ToastUtil.show(UnFreezeAccountIdentifyActivity.this, response.body().getMsg());
            }
        });
    }
}
