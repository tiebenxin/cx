package com.hm.cxpay.ui.payword;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hm.cxpay.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：忘记密码->第四步->验证短信验证码
 * @Date：2019/12/12
 * @by zjy
 * @备注：
 */
public class ForgetPswStepFourActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private TextView tvGetCode;
    private EditText etCode;
    private TextView tvSubmit;

    private Activity activity;
    private String oldToken;//旧token，若验证码迟迟收不到，需要再重发绑卡请求来获取验证码
    private String cardNo;//得到银行卡号
    private String bankName;//得到银行名
    private String cardType  = "(借记卡)";//默认固定一种

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw_step_three);
        activity = this;
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        tvGetCode = findViewById(R.id.tv_get_code);
        etCode = findViewById(R.id.et_code);
        tvSubmit = findViewById(R.id.tv_submit);
    }

    private void initData() {
        getBundle();
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initCountDownUtil();
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //1 手机号不为空
//                if(!TextUtils.isEmpty(etPhone.getText().toString())){
//                    //2 手机号格式是否正确
//                    if(etPhone.getText().toString().length()==11){
//                        //3 是否勾选同意协议
//                        if(ivCheck.isSelected()){
//                            httpForgetPswStepThree();
//                        }else {
//                            ToastUtil.show(activity,"请先同意《用户协议》");
//                        }
//                    }else {
//                        ToastUtil.show(activity,"请检查手机号格式是否正确");
//                    }
//                }else {
//                    ToastUtil.show(activity,"手机号不能为空");
//                }
            }
        });
    }

    /**
     * 发请求->找回密码第四步->检测短信验证码
     */
//    private void httpForgetPswStepFour() {
//        PayHttpUtils.getInstance().bindBankCard(cardNo,bankName,etPhone.getText().toString(),token)
//                .compose(RxSchedulers.<BaseResponse<CommonBean>>compose())
//                .compose(RxSchedulers.<BaseResponse<CommonBean>>handleResult())
//                .subscribe(new FGObserver<BaseResponse<CommonBean>>() {
//                    @Override
//                    public void onHandleSuccess(BaseResponse<CommonBean> baseResponse) {
//                        if(baseResponse.getData()!=null){
//                            if(!TextUtils.isEmpty(baseResponse.getData().getToken())){
//                                String newToken = baseResponse.getData().getToken();
//
//                                Bundle bundle = new Bundle();
//                                bundle.putString("old_token",token);
//                                bundle.putString("card_no",cardNo);
//                                bundle.putString("bank_name",bankName);
//                                bundle.putString("phone_num",etPhone.getText().toString());
//                                bundle.putString("new_token",newToken);
//                                Intent intent = new Intent(activity,ForgetPswStepFourActivity.class);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onHandleError(BaseResponse<CommonBean> baseResponse) {
//                        super.onHandleError(baseResponse);
//                    }
//                });
//    }

    //获取传过来的值
    private void getBundle() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().containsKey("old_token")) {
                    oldToken = getIntent().getExtras().getString("old_token");
                }
                if (getIntent().getExtras().containsKey("card_no")) {
                    cardNo = getIntent().getExtras().getString("card_no");
                }
                if (getIntent().getExtras().containsKey("bank_name")) {
                    bankName = getIntent().getExtras().getString("bank_name");
                }
                if (getIntent().getExtras().containsKey("bank_name")) {
                    bankName = getIntent().getExtras().getString("bank_name");
                }
                if (getIntent().getExtras().containsKey("bank_name")) {
                    bankName = getIntent().getExtras().getString("bank_name");
                }
            }
        }
    }

//    private void initCountDownUtil() {
//
//        if (TextUtils.isEmpty(phone)) {
//            ToastUtil.show(BindBankFinishActivity.this, "请填写手机号码");
//            return;
//        }
//        if (!CheckUtil.isMobileNO(phone)) {
//            ToastUtil.show(this, "手机号格式不正确");
//            return;
//        }
//
//        CountDownUtil.getTimer(60, ui.tvGetVerificationCode, "发送验证码", this, new CountDownUtil.CallTask() {
//            @Override
//            public void task() {
//                applyBindBank(bankInfo.getBankNumber(), bankInfo.getPhone());
//
//            }
//        });
//
//    }

}
