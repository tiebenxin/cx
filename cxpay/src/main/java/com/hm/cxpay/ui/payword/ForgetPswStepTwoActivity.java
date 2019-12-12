package com.hm.cxpay.ui.payword;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hm.cxpay.R;
import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：忘记密码->第二步->填写银行卡信息
 * @Date：2019/12/12
 * @by zjy
 * @备注：
 */
public class ForgetPswStepTwoActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private TextView tvName;
    private EditText etBankCard;
    private TextView tvSubmit;
    private TextView tvViewSupport;

    private Context activity;
    private String token;//得到认证需要的token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw_step_two);
        activity = this;
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        tvName = findViewById(R.id.tv_name);
        etBankCard = findViewById(R.id.et_bankcard);
        tvSubmit = findViewById(R.id.tv_submit);
        tvViewSupport = findViewById(R.id.tv_view_support);
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
//        tvSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //1 姓名不为空
//                if(!TextUtils.isEmpty(etName.getText().toString())){
//                    //2 身份证号不为空
//                    if(!TextUtils.isEmpty(etIdcard.getText().toString())){
//                        httpForgetPswStepOne(etIdcard.getText().toString(),etName.getText().toString());
//                    }else {
//                        ToastUtil.show(activity,"身份证号不能为空");
//                    }
//                }else {
//                    ToastUtil.show(activity,"姓名不能为空");
//                }
//            }
//        });
        tvViewSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(activity,"查看支持银行");
            }
        });

    }

    /**
     * 发请求->找回密码第一步->验证实名信息
     */
    private void httpForgetPswStepOne(String idNumber,String realName) {
        PayHttpUtils.getInstance().checkRealNameInfo(idNumber,realName)
                .compose(RxSchedulers.<BaseResponse<CommonBean>>compose())
                .compose(RxSchedulers.<BaseResponse<CommonBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<CommonBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<CommonBean> baseResponse) {
                        if(baseResponse.getData()!=null){
                            if(!TextUtils.isEmpty(baseResponse.getData().getToken())){
                                token = baseResponse.getData().getToken();
                            }
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse<CommonBean> baseResponse) {
                        super.onHandleError(baseResponse);
                    }
                });
    }

}
