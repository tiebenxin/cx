package com.yanlong.im.user.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.ClickFilter;
import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.encrypt.MD5;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @类名：更换手机号
 * @Date：2020/6/10
 * @by zjy
 * @备注：
 */
public class ChangePhoneNumActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private Activity activity;
    private EditText etUserPsw;//登录密码
    private EditText etNewPhoneNum;//新手机号
    private TextView tvGetCode;//点击获取验证码
    private EditText etCode;//验证码
    private TextView tvSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_num);
        activity = this;
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        etUserPsw = findViewById(R.id.et_user_psw);
        etNewPhoneNum = findViewById(R.id.et_new_phone_num);
        tvGetCode = findViewById(R.id.tv_get_code);
        etCode = findViewById(R.id.et_code);
        tvSubmit = findViewById(R.id.tv_submit);


    }

    private void initData() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {

            }
        });
        //密码、确认密码默认隐藏明文
        TransformationMethod method =  PasswordTransformationMethod.getInstance();
        etUserPsw.setTransformationMethod(method);
        //获取验证码
        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCountDownUtil();
            }
        });
        //提交
        ClickFilter.onClick(tvSubmit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //三项必填
                if (!TextUtils.isEmpty(etUserPsw.getText().toString())) {
                    if (!TextUtils.isEmpty(etNewPhoneNum.getText().toString())) {
                        if(!TextUtils.isEmpty(etCode.getText().toString())){
                            httpChangePhone();
                        }else {
                            ToastUtil.show(activity, "验证码不能为空");
                        }
                    } else {
                        ToastUtil.show(activity, "手机号码不能为空");
                    }
                } else {
                    ToastUtil.show(activity, "登录密码不能为空");
                }
            }
        });
    }

    private void initCountDownUtil() {
        if (TextUtils.isEmpty(etNewPhoneNum.getText().toString())) {
            ToastUtil.show(activity, "手机号码不能为空");
            return;
        }
        if (!CheckUtil.isMobileNO(etNewPhoneNum.getText().toString())) {
            ToastUtil.show(activity, "手机号码格式不正确");
            return;
        }
        CountDownUtil.getTimer(60, tvGetCode, "获取验证码", this, new CountDownUtil.CallTask() {
            @Override
            public void task() {
                new UserAction().smsCaptchaGet(etNewPhoneNum.getText().toString(), "update_phone", new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        if (response.body() == null) {
                            return;
                        }
                        if (!response.body().isOk()) {
                            CountDownUtil.cancelTimer();
                        }
                        ToastUtil.show(activity, response.body().getMsg());
                    }
                });
            }
        });
    }

    /**
     * 修改手机号
     */
    private void httpChangePhone(){
        new UserAction().changePhoneNum(MD5.md5(etUserPsw.getText().toString()), etNewPhoneNum.getText().toString(),etCode.getText().toString(),new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {

                } else {
                    ToastUtil.show(response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show(t.toString());
            }
        });
    }
}
