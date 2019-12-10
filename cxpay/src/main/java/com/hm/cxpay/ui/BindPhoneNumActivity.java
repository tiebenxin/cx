package com.hm.cxpay.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hm.cxpay.R;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;


/**
 * @类名：实名认证->绑定手机号
 * @Date：2019/12/9
 * @by zjy
 * @备注：
 */

public class BindPhoneNumActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private EditText etPhoneNum;//若没有设置过手机号，则可以输入
    private TextView tvPhoneNum;//若有设置过手机号，则直接显示
    private EditText etCode;//验证码输入框
    private TextView tvGetCode;//点击获取验证码
    private TextView tvSubmit;
    private boolean hadPhoneNum = false;//是否存在手机号码  若存在则取已存在的值，若不存在手机号则取输入框的值

    private Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_bind_phone);
        initView();
        initData();
        httpGetMyPhone();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        etPhoneNum = findViewById(R.id.et_phone_num);
        tvPhoneNum = findViewById(R.id.tv_phone_num);
        etCode = findViewById(R.id.et_code);
        tvGetCode = findViewById(R.id.tv_get_code);
        tvSubmit = findViewById(R.id.tv_submit);
    }

    private void initData() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        //下一步
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有手机号
                //1-1 有则直接发请求
                if(hadPhoneNum){
                    //1-2 验证码不为空
                    if(!TextUtils.isEmpty(etCode.getText().toString())){
                        httpBindPhone(tvPhoneNum.getText().toString());
                    }else {
                        ToastUtil.show(activity,"验证码不能为空");
                    }
                }else {
                    //2-1 没有则监听输入框
                    if(!TextUtils.isEmpty(etPhoneNum.getText().toString())){
                        //2-2 验证码不为空
                        if(!TextUtils.isEmpty(etCode.getText().toString())){
                            httpBindPhone(etPhoneNum.getText().toString());
                        }else {
                            ToastUtil.show(activity,"验证码不能为空");
                        }
                    }else {
                        ToastUtil.show(activity,"手机号码不能为空");
                    }
                }
            }
        });
        //获取验证码
        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCountDownUtil();
            }
        });

    }

    private void initCountDownUtil() {
        if (TextUtils.isEmpty(etPhoneNum.getText().toString())) {
            ToastUtil.show(activity, "手机号码不能为空");
            return;
        }
        if (!CheckUtil.isMobileNO(etPhoneNum.getText().toString())) {
            ToastUtil.show(activity, "手机号码格式不正确");
            return;
        }
        CountDownUtil.getTimer(60, tvGetCode, "发送验证码", this, new CountDownUtil.CallTask() {
            @Override
            public void task() {
                httpGetCode(etPhoneNum.getText().toString());
            }
        });

    }

    /**
     * 请求->获取绑定手机的验证码
     * @param phoneNum
     */
    private void httpGetCode(String phoneNum) {
        PayHttpUtils.getInstance().getCode(phoneNum)
                .compose(RxSchedulers.<BaseResponse>compose())
                .compose(RxSchedulers.<BaseResponse>handleResult())
                .subscribe(new FGObserver<BaseResponse>() {
                    @Override
                    public void onHandleSuccess(BaseResponse baseResponse) {
                        LogUtil.getLog().i("TAG","获取验证码成功!");
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(activity, baseResponse.getMessage());
                    }
                });

    }

    /**
     * 请求->获取当前用户IM手机号
     *
     * 备注：优先获取用户IM手机号，没有则自行输入，若有则直接取手机号
     */
    private void httpGetMyPhone() {
        PayHttpUtils.getInstance().getMyPhone()
                .compose(RxSchedulers.<BaseResponse>compose())
                .compose(RxSchedulers.<BaseResponse>handleResult())
                .subscribe(new FGObserver<BaseResponse>() {
                    @Override
                    public void onHandleSuccess(BaseResponse baseResponse) {
                        if (baseResponse.getData() != null) {
                            hadPhoneNum = true;
                            tvPhoneNum.setVisibility(View.VISIBLE);
                            etPhoneNum.setVisibility(View.GONE);
                            tvPhoneNum.setText(baseResponse.getData()+"");
                        }else {
                            hadPhoneNum = false;
                            tvPhoneNum.setVisibility(View.GONE);
                            etPhoneNum.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(activity, baseResponse.getMessage());
                    }
                });

    }

    /**
     * 请求->绑定手机号码
     * @param phoneNum
     */
    private void httpBindPhone(String phoneNum) {
        PayHttpUtils.getInstance().bindPhoneNum(phoneNum,etCode.getText().toString())
                .compose(RxSchedulers.<BaseResponse>compose())
                .compose(RxSchedulers.<BaseResponse>handleResult())
                .subscribe(new FGObserver<BaseResponse>() {
                    @Override
                    public void onHandleSuccess(BaseResponse baseResponse) {
                        ToastUtil.show(context, "手机号码验证成功!");
                        PayEnvironment.getInstance().getUser().setPhoneBindStat(1);
                        go(LooseChangeActivity.class);
                        finish();
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });

    }


}
