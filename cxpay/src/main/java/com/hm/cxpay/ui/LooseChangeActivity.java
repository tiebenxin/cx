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
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.controller.ControllerPaySetting;
import com.hm.cxpay.dailog.ChangeSelectDialog;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankSettingActivity;
import com.hm.cxpay.ui.bank.BindBankActivity;
import com.hm.cxpay.ui.bill.BillDetailListActivity;
import com.hm.cxpay.ui.change.ChangeDetailListActivity;
import com.hm.cxpay.ui.identification.IdentificationInfoActivity;
import com.hm.cxpay.ui.payword.ManagePaywordActivity;
import com.hm.cxpay.ui.payword.SetPaywordActivity;
import com.hm.cxpay.ui.recharege.RechargeActivity;
import com.hm.cxpay.ui.withdraw.WithdrawActivity;
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

    private Activity activity;
    private ChangeSelectDialog.Builder builder;
    private ChangeSelectDialog dialogOne;//通用提示选择弹框：检测到未设置支付密码
    private ChangeSelectDialog dialogTwo;//通用提示选择弹框：没有添加过银行卡
    private ChangeSelectDialog dialogThree;//通用提示选择弹框：是否绑定手机号

    private int myCardListSize = 0;//我的银行卡个数 (判断是否添加过银行卡)
    private UserBean userBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loose_change);
        activity = this;
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBankList();
        httpGetUserInfo();
    }


    @Override
    protected void onPause() {
        super.onPause();
        resumeEnabled();
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
                startActivity(new Intent(activity, BillDetailListActivity.class));
            }
        });
        builder = new ChangeSelectDialog.Builder(activity);
        //显示余额
        userBean = PayEnvironment.getInstance().getUser();
        if (userBean != null) {
            //显示余额
            tvBalance.setText("¥ " + UIUtils.getYuan(Long.valueOf(userBean.getBalance())));
        }

        //充值
        layoutRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutRecharge.setEnabled(false);
                // 1 已设置支付密码 -> 允许跳转
                if (userBean != null && userBean.getPayPwdStat() == 1) {
                    startActivity(new Intent(activity, RechargeActivity.class));
                } else {
                    //2 未设置支付密码 -> 需要先设置
                    showSetPaywordDialog();
                }
            }
        });
        //提现
        layoutWithdrawDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1 已设置支付密码 -> 允许跳转
                if (userBean != null && userBean.getPayPwdStat() == 1) {
                    //2 是否添加过银行卡
                    if (myCardListSize > 0) {
                        startActivity(new Intent(activity, WithdrawActivity.class));
                    } else {
                        showAddBankCardDialog();
                    }
                } else {
                    //未设置支付密码 -> 需要先设置
                    showSetPaywordDialog();
                }
            }
        });
        //零钱明细
        layoutChangeDetails = new ControllerPaySetting(findViewById(R.id.layout_change_details));
        layoutChangeDetails.init(R.mipmap.ic_change_details, R.string.change_details, "");
        layoutChangeDetails.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                layoutChangeDetails.setEnabled(false);
                startActivity(new Intent(activity, ChangeDetailListActivity.class));
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
                layoutAuthRealName.setEnabled(false);
                //1 已经绑定手机
                if (userBean != null && userBean.getPhoneBindStat() == 1) {
                    if (PayEnvironment.getInstance().getUser().getPhoneBindStat() == 1) {
                        IntentUtil.gotoActivity(activity, IdentificationInfoActivity.class);
                    } else {
                        //2 没有绑定手机
                        showBindPhoneNumDialog();
                    }
                }
            }
        });
        //我的银行卡
        viewMyCard = new ControllerPaySetting(findViewById(R.id.viewBankSetting));
        int count = 0;
        viewMyCard.init(R.mipmap.ic_bank_card, R.string.settings_of_bank, count + "张");
        viewMyCard.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                viewMyCard.setEnabled(false);
                //已设置支付密码 -> 允许跳转
//                if (userBean != null && userBean.getPayPwdStat() == 1) {
//                    startActivity(new Intent(activity, BankSettingActivity.class));
//                } else {
//                    //未设置支付密码 -> 需要先设置
//                    showSetPaywordDialog();
//                }
            }
        });
        //支付密码管理
        viewSettingOfPsw = new

                ControllerPaySetting(findViewById(R.id.viewSettingOfPsw));
        viewSettingOfPsw.init(R.mipmap.ic_paypsw_manage, R.string.settings_of_psw, "");
        viewSettingOfPsw.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                // 1 已设置支付密码 -> 允许跳转
                viewSettingOfPsw.setEnabled(false);
                if (userBean != null && userBean.getPayPwdStat() == 1) {
                    IntentUtil.gotoActivity(activity, ManagePaywordActivity.class);
                } else {
                    //2 未设置支付密码 -> 需要先设置
                    showSetPaywordDialog();
                }
            }
        });
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
    private void getBankList() {
//        PayHttpUtils.getInstance().getBankList()
//                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>compose())
//                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>handleResult())
//                .subscribe(new FGObserver<BaseResponse<List<BankBean>>>() {
//                    @Override
//                    public void onHandleSuccess(BaseResponse<List<BankBean>> baseResponse) {
//                        List<BankBean> info = baseResponse.getData();
//                        if (info != null) {
//                            myCardListSize = info.size();
//                        } else {
//                            myCardListSize = 0;
//                        }
//                        viewMyCard.getRightTitle().setText(myCardListSize + "张");
//                    }
//
//                    @Override
//                    public void onHandleError(BaseResponse baseResponse) {
//                        ToastUtil.show(activity, baseResponse.getMessage());
//                    }
//                });
    }


    /**
     * 检测到未设置支付密码弹框
     */
    private void showSetPaywordDialog() {
        dialogOne = builder.setTitle("您还没有设置支付密码\n请设置支付密码后再进行操作")
                .setLeftText("取消")
                .setRightText("设置支付密码")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogOne.dismiss();
                        resumeEnabled();
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //去设置
                        dialogOne.dismiss();
                        startActivity(new Intent(activity, SetPaywordActivity.class));
                    }
                })
                .build();
        dialogOne.show();
    }

    /**
     * 是否绑定手机号弹框
     */
    private void showBindPhoneNumDialog() {
        dialogThree = builder.setTitle("您还没有绑定手机号码\n请先绑定后再进行操作")
                .setLeftText("取消")
                .setRightText("去绑定")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogThree.dismiss();
                        resumeEnabled();
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //去绑定
                        dialogThree.dismiss();
                        startActivity(new Intent(activity, BindPhoneNumActivity.class));
                    }
                })
                .build();
        dialogThree.show();
    }

    /**
     * 没有添加过银行卡弹框
     */
    private void showAddBankCardDialog() {
        dialogTwo = builder.setTitle("您尚未绑定银行卡，无法使用该功能")
                .setLeftText("取消")
                .setRightText("去绑卡")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogTwo.dismiss();
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //去添加银行卡
                        dialogTwo.dismiss();
                        startActivity(new Intent(activity, BindBankActivity.class));
                    }
                })
                .build();
        dialogTwo.show();
    }

    private void getBank(){
        
    }

}
