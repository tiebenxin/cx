package com.hm.cxpay.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.controller.ControllerPaySetting;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankBean;
import com.hm.cxpay.ui.bank.BankSettingActivity;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.HeadView;

import java.util.List;

/**
 * 零钱首页
 */
public class LooseChangeActivity extends BasePayActivity {

    private ControllerPaySetting viewSettingOfPsw;
    private ControllerPaySetting viewMyCard;
    private ControllerPaySetting viewMyRedEnvelope;
    private ControllerPaySetting layoutChangeDetails;//零钱明细
    private ControllerPaySetting layoutAuthRealName;//实名认证

    private HeadView mHeadView;
    private TextView tvBalance;//余额
    private LinearLayout layoutRecharge;//充值
    private LinearLayout layoutWithdrawDeposit;//提现

    private Context activity;

    public static int REFRESH_BALANCE = 98;//获取最新余额展示
    public static int REFRESH_BANKCARD_NUM = 97;//刷新银行卡数
    private int myCardListSize = 0;//我的银行卡个数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loose_change);
        activity = this;
        initView();
        initEvent();
        getBankList();
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
                ToastUtil.show(context, "账单");

            }
        });
        //显示余额
        tvBalance.setText("¥ " + UIUtils.getYuan(Long.valueOf(PayEnvironment.getInstance().getUser().getBalance())));
        //充值
        layoutRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LooseChangeActivity.this, RechargeActivity.class), REFRESH_BALANCE);
            }
        });
        //提现
        layoutWithdrawDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LooseChangeActivity.this, WithdrawActivity.class), REFRESH_BALANCE);
            }
        });
        //零钱明细
        layoutChangeDetails = new ControllerPaySetting(findViewById(R.id.layout_change_details));
        layoutChangeDetails.init(R.mipmap.ic_change_details, R.string.change_details, "");
        layoutChangeDetails.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                ToastUtil.show(context, "零钱明细");
//                IntentUtil.gotoActivity(LooseChangeActivity.this, ManagePaywordActivity.class);
            }
        });
        //红包明细
        viewMyRedEnvelope = new ControllerPaySetting(findViewById(R.id.viewMyRedEnvelope));
        viewMyRedEnvelope.init(R.mipmap.ic_red_packet_info, R.string.my_red_envelope, "");
        viewMyRedEnvelope.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                ARouter.getInstance().build("/app/redEnvelopeDetailsActivity").navigation();
//                ARouter.getInstance().build("/app/redpacketRecordActivity").navigation();
            }
        });
        //账户信息
        ControllerPaySetting viewAccountInfo = new ControllerPaySetting(findViewById(R.id.viewAccountInfo));
        viewAccountInfo.init(R.mipmap.ic_account_info, R.string.account_info, "");
        viewAccountInfo.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                IntentUtil.gotoActivity(LooseChangeActivity.this, IdentificationInfoActivity.class);
            }
        });
        //实名认证
        layoutAuthRealName = new ControllerPaySetting(findViewById(R.id.layout_auth_realname));
        layoutAuthRealName.init(R.mipmap.ic_auth_realname, R.string.auth_realname, "");
        layoutAuthRealName.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                IntentUtil.gotoActivity(LooseChangeActivity.this, IdentificationUserActivity.class);
            }
        });
        //我的银行卡
        viewMyCard = new ControllerPaySetting(findViewById(R.id.viewBankSetting));
        int count = 0;
        viewMyCard.init(R.mipmap.ic_bank_card, R.string.settings_of_bank, count + "张");
        viewMyCard.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                startActivityForResult(new Intent(LooseChangeActivity.this, BankSettingActivity.class), REFRESH_BALANCE);
            }
        });
        //支付密码管理
        viewSettingOfPsw = new ControllerPaySetting(findViewById(R.id.viewSettingOfPsw));
        viewSettingOfPsw.init(R.mipmap.ic_paypsw_manage, R.string.settings_of_psw, "");
        viewSettingOfPsw.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                IntentUtil.gotoActivity(LooseChangeActivity.this, ManagePaywordActivity.class);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REFRESH_BALANCE) {
            if (resultCode == RESULT_OK) {
                httpGetUserInfo();
            }
            if (resultCode == REFRESH_BANKCARD_NUM) {
                getBankList();
            }
        }
    }

    /**
     * 请求->获取用户信息
     */
    private void httpGetUserInfo() {
        PayHttpUtils.getInstance().getUserInfo()
                .compose(RxSchedulers.<BaseResponse<UserBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UserBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UserBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UserBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            UserBean userBean = null;
                            if (baseResponse.getData() != null) {
                                userBean = baseResponse.getData();
                            } else {
                                userBean = new UserBean();
                            }
                            //刷新最新余额
                            PayEnvironment.getInstance().getUser().setBalance(userBean.getBalance());
                            tvBalance.setText("¥ " + UIUtils.getYuan(Long.valueOf(userBean.getBalance())));
                        } else {
                            ToastUtil.show(context, baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse<UserBean> baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });
    }

    /**
     * 请求->绑定的银行卡列表
     *
     * 备注：主要用于零钱首页更新"我的银行卡" 张数，暂时仅"充值、提现、我的银行卡"返回此界面后需要刷新
     */
    private void getBankList() {
        PayHttpUtils.getInstance().getBankList()
                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>compose())
                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>handleResult())
                .subscribe(new FGObserver<BaseResponse<List<BankBean>>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<List<BankBean>> baseResponse) {
                        List<BankBean> info = baseResponse.getData();
                        if (info != null) {
                            myCardListSize = info.size();
                        } else {
                            myCardListSize = 0;
                        }
                        viewMyCard.getRightTitle().setText(myCardListSize+"张");
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(activity, baseResponse.getMessage());
                    }
                });
    }

}
