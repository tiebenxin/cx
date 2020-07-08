package com.hm.cxpay.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.UrlBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.controller.ControllerPaySetting;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.identification.IdentificationInfoActivity;
import com.hm.cxpay.ui.recharege.RechargeActivity;
import com.hm.cxpay.ui.withdraw.WithdrawActivity;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.HeadView;

/**
 * 零钱首页
 */
public class LooseChangeActivity extends BasePayActivity {
    private final String TAG = getClass().getSimpleName();

    private ControllerPaySetting viewSettingOfPsw;
    private ControllerPaySetting viewMyCard;
    private ControllerPaySetting viewMyRedEnvelope;
    private ControllerPaySetting layoutChangeDetails;//零钱明细
    private ControllerPaySetting layoutAuthRealName;//实名认证
    private HeadView mHeadView;
    private TextView tvBalance;//余额
    private LinearLayout layoutRecharge;//充值
    private LinearLayout layoutWithdrawDeposit;//提现
    private Activity activity;
    private UserBean userBean;
    boolean isBalanceChange = false;//是否余额可能改变


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.getLog().i(TAG, "onCreate");
        setContentView(R.layout.activity_loose_change);
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
        activity = this;
        initView();
        initEvent();
        httpGetUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.getLog().i(TAG, "onResume");
        if (isBalanceChange) {
            httpGetUserInfo();
            isBalanceChange = false;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        resumeEnabled();
        LogUtil.getLog().i(TAG, "onPause");

    }

    private void resumeEnabled() {
        layoutRecharge.setEnabled(true);
        layoutWithdrawDeposit.setEnabled(true);
        layoutChangeDetails.setEnabled(true);
        layoutAuthRealName.setEnabled(true);
        viewMyRedEnvelope.setEnabled(true);
        viewMyCard.setEnabled(true);
        viewSettingOfPsw.setEnabled(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.getLog().i(TAG, "onPause");
//        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.getLog().i(TAG, "onNewIntent");

    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        tvBalance = findViewById(R.id.tv_money);
        layoutRecharge = findViewById(R.id.layout_recharge);
        layoutWithdrawDeposit = findViewById(R.id.layout_withdraw_deposit);
    }


    private void initEvent() {
        mHeadView.getActionbar().setChangeStyleBg();
        mHeadView.getAppBarLayout().setBackgroundResource(R.color.c_c85749);
        mHeadView.getActionbar().setTxtRight("账单");
        //标题栏事件
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
//                startActivity(new Intent(activity, BillDetailListActivity.class));
                ARouter.getInstance().build("/app/billListActivity").navigation();

            }
        });
        //显示余额
        userBean = PayEnvironment.getInstance().getUser();
        setBalance();

        //充值
        layoutRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutRecharge.setEnabled(false);
                // 1 已设置支付密码 -> 允许跳转
                startActivity(new Intent(activity, RechargeActivity.class));
                isBalanceChange = true;
            }
        });
        //提现
        layoutWithdrawDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1 已设置支付密码 -> 允许跳转
                startActivity(new Intent(activity, WithdrawActivity.class));
                isBalanceChange = true;
            }
        });
        //零钱明细
        layoutChangeDetails = new ControllerPaySetting(findViewById(R.id.layout_change_details));
        layoutChangeDetails.init(R.mipmap.ic_change_details, R.string.change_details, "");
        layoutChangeDetails.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                layoutChangeDetails.setEnabled(false);
//                startActivity(new Intent(activity, ChangeDetailListActivity.class));
                ARouter.getInstance().build("/app/changeListActivity").navigation();
            }
        });
        //红包明细
        viewMyRedEnvelope = new ControllerPaySetting(findViewById(R.id.viewMyRedEnvelope));
        viewMyRedEnvelope.init(R.mipmap.ic_red_packet_info, R.string.my_red_envelope, "");
        viewMyRedEnvelope.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                viewMyRedEnvelope.setEnabled(false);
                ARouter.getInstance().build("/app/redEnvelopeDetailsActivity").navigation();
            }
        });

        //实名认证
        layoutAuthRealName = new ControllerPaySetting(findViewById(R.id.layout_auth_realname));
        layoutAuthRealName.init(R.mipmap.ic_auth_realname, R.string.auth_realname, "");
        layoutAuthRealName.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                IntentUtil.gotoActivity(activity, IdentificationInfoActivity.class);
            }
        });
        //我的银行卡
        viewMyCard = new ControllerPaySetting(findViewById(R.id.viewBankSetting));
        viewMyCard.init(R.mipmap.ic_bank_card, R.string.settings_of_bank, ""/*count + "张"*/);
        viewMyCard.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                viewMyCard.setEnabled(false);
                //已设置支付密码 -> 允许跳转
                getBankUrl();
            }
        });
        //支付密码管理
        viewSettingOfPsw = new ControllerPaySetting(findViewById(R.id.viewSettingOfPsw));
        viewSettingOfPsw.init(R.mipmap.ic_paypsw_manage, R.string.settings_of_psw, "");
//        viewSettingOfPsw.setVisible(false);
        viewSettingOfPsw.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                // 1 已设置支付密码 -> 允许跳转
                viewSettingOfPsw.setEnabled(false);
                getPswManager();
            }
        });
    }

    private void setBalance() {
        if (userBean != null) {
            //显示余额
            tvBalance.setText("¥ " + UIUtils.getYuan(Long.valueOf(userBean.getBalance())));
        }
    }

    /**
     * 请求->获取用户信息
     */
    private void httpGetUserInfo() {
        long uid = PayEnvironment.getInstance().getUserId();
        if (uid <= 0) {
            return;
        }
        PayHttpUtils.getInstance().getUserInfo(uid)
                .compose(RxSchedulers.<BaseResponse<UserBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UserBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UserBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UserBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            if (baseResponse.getData() != null) {
                                userBean = baseResponse.getData();
                            } else {
                                userBean = new UserBean();
                            }
                            setBalance();
                            PayEnvironment.getInstance().setUser(userBean);
                            //刷新最新余额
                            tvBalance.setText("¥ " + UIUtils.getYuan(Long.valueOf(userBean.getBalance())));
                        } else {
                            ToastUtil.show(context, baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse<UserBean> baseResponse) {
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });
    }

    /**
     * 请求->绑定的银行卡列表
     * <p>
     * 备注：主要用于零钱首页更新"我的银行卡" 张数，暂时仅"充值、提现、我的银行卡"返回此界面后需要刷新
     */
    private void getBankUrl() {
        PayHttpUtils.getInstance().getBankUrl()
                .compose(RxSchedulers.<BaseResponse<UrlBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UrlBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UrlBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UrlBean> baseResponse) {
                        viewMyCard.setEnabled(true);
                        if (baseResponse.getData() != null) {
                            UrlBean urlBean = baseResponse.getData();
                            goWebActivity(LooseChangeActivity.this, urlBean.getUrl());
                        } else {
                            ToastUtil.show(activity, "获取数据失败");
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        viewMyCard.setEnabled(true);
                        ToastUtil.show(activity, baseResponse.getMessage());
                    }
                });
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void eventPayResult(PayResultEvent event) {
//        if (event.getResult() == PayEnum.EPayResult.SUCCESS) {
//            httpGetUserInfo();
//        }
//    }

    /**
     * 请求->绑定的银行卡列表
     * <p>
     * 备注：主要用于零钱首页更新"我的银行卡" 张数，暂时仅"充值、提现、我的银行卡"返回此界面后需要刷新
     */
    private void getPswManager() {
        PayHttpUtils.getInstance().getPswManager()
                .compose(RxSchedulers.<BaseResponse<UrlBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UrlBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UrlBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UrlBean> baseResponse) {
                        viewSettingOfPsw.setEnabled(true);
                        if (baseResponse.getData() != null) {
                            UrlBean urlBean = baseResponse.getData();
                            goWebActivity(LooseChangeActivity.this, urlBean.getUrl());
                        } else {
                            ToastUtil.show(activity, "获取数据失败");
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        viewSettingOfPsw.setEnabled(true);
                        ToastUtil.show(activity, baseResponse.getMessage());
                    }
                });
    }

}
