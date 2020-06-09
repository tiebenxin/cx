package com.hm.cxpay.ui.recharege;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hm.cxpay.R;
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.bean.UrlBean;
import com.hm.cxpay.dailog.DialogErrorPassword;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.payword.ForgetPswStepOneActivity;
import com.hm.cxpay.utils.UIUtils;
import com.hm.cxpay.widget.PswView;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.WebPageActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * @类名：零钱->充值
 * @Date：2019/11/29
 * @by zjy
 * @备注：
 */
public class RechargeActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private TextView tvBalance;//我的余额
    private EditText etRecharge;//充值金额
    private TextView tvSubmit;//支付
    private TextView tvSelectOne;//选中10
    private TextView tvSelectTwo;//选中20
    private TextView tvSelectThree;//选中30
    private TextView tvSelectFour;//选中100
    private TextView tvSelectFive;//选中200
    private TextView tvSelectSix;//选中500
    private Activity activity;
    private boolean ifAddBankcard = false;//判断是否添加过银行卡
    private List<BankBean> bankList = null;//我所绑定的所有银行卡列表数据

    private AlertDialog dialogOne;//添加银行卡弹框
    private AlertDialog dialogTwo;//充值支付弹框
    private TextView tvBankNameTwo;//已有银行卡切换->显示银行卡名
    private ImageView ivBankIconTwo;//已有银行卡切换->显示银行卡头像
    private TextView tvNotice;//低于10元顶部提示
    private TextView tvQuestion;//常见问题
    private PswView pswView;//密码输入框
    private DialogErrorPassword dialogErrorPassword;

    public static final int SELECT_BANKCARD = 99;
    private BankBean selectBankcard;//选中的银行卡
    private StringBuilder builder;
    private RequestOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_recharge);
        initView();
        initData();
        PayEnvironment.getInstance().notifyStampUpdate(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getBankList();
        if (dialogTwo != null && dialogTwo.isShowing()) {
            //强制唤起软键盘
            showSoftKeyword(pswView);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayEnvironment.getInstance().notifyStampUpdate(true);
        if (dialogErrorPassword != null) {
            dialogErrorPassword.dismiss();
            dialogErrorPassword = null;
        }
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        tvBalance = findViewById(R.id.tv_balance);
        etRecharge = findViewById(R.id.et_recharge);
        tvSubmit = findViewById(R.id.tv_submit);
        tvSelectOne = findViewById(R.id.tv_select_one);
        tvSelectTwo = findViewById(R.id.tv_select_two);
        tvSelectThree = findViewById(R.id.tv_select_three);
        tvSelectFour = findViewById(R.id.tv_select_four);
        tvSelectFive = findViewById(R.id.tv_select_five);
        tvSelectSix = findViewById(R.id.tv_select_six);
        tvNotice = findViewById(R.id.tv_notice);
        tvQuestion = findViewById(R.id.tv_question);
        actionbar = headView.getActionbar();
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
        bankList = new ArrayList<>();
        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        //显示余额
        tvBalance.setText("当前零钱余额  ¥ " + UIUtils.getYuan(Long.valueOf(PayEnvironment.getInstance().getUser().getBalance())));

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1 充值金额不能为空
                if (!TextUtils.isEmpty(etRecharge.getText().toString())) {
                    //2 最低充值10元
                    int yuan = Integer.valueOf(etRecharge.getText().toString());
                    if (yuan >= 10) {
                        httpRecharge(yuan);
                    } else {
                        ToastUtil.show(context, "最低充值金额10元");
                    }
                } else {
                    ToastUtil.show(context, "充值金额不能为空");
                }

            }
        });
        tvSelectOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("10");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectOne.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectOne.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("20");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectTwo.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectTwo.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("30");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectThree.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectThree.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("100");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectFour.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectFour.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("200");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectFive.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectFive.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("500");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectSix.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectSix.setTextColor(getResources().getColor(R.color.white));
            }
        });
        etRecharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //1 为空不参与计算
                if (!TextUtils.isEmpty(etRecharge.getText().toString())) {
                    //选中状态
                    selectItem(etRecharge.getText().toString());
                    //2 自动过滤用户金额前乱输入0
                    String total = etRecharge.getText().toString();
                    if (total.startsWith("0")) {
                        if (total.length() >= 2) {
                            if (!".".equals(String.valueOf(total.charAt(1)))) {
                                total = total.substring(1, total.length());
                                etRecharge.setText(total);
                                etRecharge.setSelection(total.length());
                            }
                        }
                    }
                    //3 金额最高限制
                    if (Double.valueOf(total) > 500) {
                        ToastUtil.show(activity, "单笔充值最高不能超过500元");
                        etRecharge.setText("");
                    }
                    //4 低于10元顶部提示
                    if (Double.valueOf(total) < 10) {
                        tvNotice.setVisibility(View.VISIBLE);
                    } else {
                        tvNotice.setVisibility(View.INVISIBLE);
                    }
                } else {
                    clearSelectedStatus();
                }
            }
        });
        tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/HelpActivity").navigation();
            }
        });

    }

    /**
     * 清除其他选中状态
     */
    public void clearSelectedStatus() {
        tvSelectOne.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectTwo.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectThree.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectFour.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectFive.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectSix.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectOne.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectTwo.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectThree.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectFour.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectFive.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectSix.setTextColor(getResources().getColor(R.color.c_517da2));
    }

    /**
     * 选中某一项
     */
    private void selectItem(String value) {
        clearSelectedStatus();
        switch (value) {
            case "10":
                tvSelectOne.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectOne.setTextColor(getResources().getColor(R.color.white));
                break;
            case "20":
                tvSelectTwo.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectTwo.setTextColor(getResources().getColor(R.color.white));
                break;
            case "30":
                tvSelectThree.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectThree.setTextColor(getResources().getColor(R.color.white));
                break;
            case "100":
                tvSelectFour.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectFour.setTextColor(getResources().getColor(R.color.white));
                break;
            case "200":
                tvSelectFive.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectFive.setTextColor(getResources().getColor(R.color.white));
                break;
            case "500":
                tvSelectSix.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectSix.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
    }

//    /**
//     * 两种类型弹框 (特殊样式，暂不复用)
//     * 1 添加银行卡->没绑定过
//     * 2 输入支付密码->绑定过
//     */
//    public void showRechargeDialog(int type) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
//        dialogBuilder.setCancelable(false);//取消点击外部消失弹窗
//        if (type == 1) {
//            dialogOne = dialogBuilder.create();
//            //获取界面
//            View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_add_bankcard, null);
//            //初始化控件
//            ImageView ivClose = dialogView.findViewById(R.id.iv_close);
//            TextView tvRechargeValue = dialogView.findViewById(R.id.tv_recharge_value);
//            LinearLayout layoutAddBankcard = dialogView.findViewById(R.id.layout_add_bankcard);
//            //显示和点击事件
//            tvRechargeValue.setText("¥" + etRecharge.getText().toString());
//            ivClose.setOnClickListener(new android.view.View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialogOne.dismiss();
//                }
//            });
//            layoutAddBankcard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //新增完银行卡后，会回到此界面调用getBankList()，此时bankList已经有数据，且ifAddBankcard为true
//                    //和提现逻辑稍有差别，但影响不大
//                    go(BindBankActivity.class);
//                    dialogOne.dismiss();
//                }
//            });
//            //展示界面
//            dialogOne.show();
//            //解决圆角shape背景无效问题
//            Window window = dialogOne.getWindow();
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            //相关配置
//            WindowManager.LayoutParams lp = window.getAttributes();
//            window.setGravity(Gravity.CENTER);
//            WindowManager manager = window.getWindowManager();
//            DisplayMetrics metrics = new DisplayMetrics();
//            manager.getDefaultDisplay().getMetrics(metrics);
//            //设置宽高，高度自适应，宽度屏幕0.8
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            lp.width = (int) (metrics.widthPixels * 0.8);
//            dialogOne.getWindow().setAttributes(lp);
//            dialogOne.setContentView(dialogView);
//        } else if (type == 2) {
//            dialogTwo = dialogBuilder.create();
//            View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_pay_psw, null);
//            ImageView ivClose = dialogView.findViewById(R.id.iv_close);
//            TextView tvRechargeValue = dialogView.findViewById(R.id.tv_recharge_value);
//            tvBankNameTwo = dialogView.findViewById(R.id.tv_bank_name);
//            ivBankIconTwo = dialogView.findViewById(R.id.iv_bank_icon);
//            LinearLayout layoutChangeBankcard = dialogView.findViewById(R.id.layout_change_bankcard);
//            pswView = dialogView.findViewById(R.id.psw_view);
//            //充值金额
//            tvRechargeValue.setText("¥" + etRecharge.getText().toString());
//            builder = new StringBuilder();
//            //默认取第一张银行卡信息展示: 银行卡名 银行卡id 银行卡图标
//            selectBankcard = bankList.get(0);
//            if (!TextUtils.isEmpty(selectBankcard.getBankName())) {
//                builder.append(selectBankcard.getBankName());
//                if (!TextUtils.isEmpty(selectBankcard.getCardNo())) {
//                    int length = selectBankcard.getCardNo().length();
//                    builder.append("(");
//                    builder.append(selectBankcard.getCardNo().substring(length - 4, length));
//                    builder.append(")");
//                }
//                tvBankNameTwo.setText(builder);//银行卡名称尾号
//                if (!TextUtils.isEmpty(selectBankcard.getLogo())) {
//                    Glide.with(activity).load(selectBankcard.getLogo())
//                            .apply(options).into(ivBankIconTwo);
//                } else {
//                    ivBankIconTwo.setImageResource(R.mipmap.ic_bank_zs);
//                }
//            }
//            //关闭弹框
//            ivClose.setOnClickListener(new android.view.View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialogTwo.dismiss();
//                }
//            });
//            //输入支付密码
//            pswView.setOnPasswordChangedListener(new PswView.onPasswordChangedListener() {
//                @Override
//                public void setPasswordChanged(String payword) {
//                    showLoadingDialog();
//                    httpRecharge(payword, selectBankcard.getId());
//
//                    if (dialogTwo != null) {
//                        dialogTwo.dismiss();
//                    }
//                }
//            });
//            //切换其他银行卡来支付
//            layoutChangeBankcard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivityForResult(new Intent(activity, SelectBankCardActivity.class), SELECT_BANKCARD);
//
//                }
//            });
//            //展示界面
//            dialogTwo.show();
//            //强制唤起软键盘
//            showSoftKeyword(pswView);
//            //解决dialog里edittext不响应键盘的问题
//            dialogTwo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//            //解决圆角shape背景无效问题
//            Window window = dialogTwo.getWindow();
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            //相关配置
//            WindowManager.LayoutParams lp = window.getAttributes();
//            window.setGravity(Gravity.CENTER);
//            WindowManager manager = window.getWindowManager();
//            DisplayMetrics metrics = new DisplayMetrics();
//            manager.getDefaultDisplay().getMetrics(metrics);
//            //设置宽高，高度自适应，宽度屏幕0.8
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            lp.width = (int) (metrics.widthPixels * 0.8);
//            dialogTwo.getWindow().setAttributes(lp);
//            dialogTwo.setContentView(dialogView);
//        }
//    }

    /**
     * 请求->绑定的银行卡列表
     */
    private void getBankList() {
//        PayHttpUtils.getInstance().getBankList()
//                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>compose())
//                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>handleResult())
//                .subscribe(new FGObserver<BaseResponse<List<BankBean>>>() {
//                    @Override
//                    public void onHandleSuccess(BaseResponse<List<BankBean>> baseResponse) {
//                        if (baseResponse.isSuccess()) {
//                            bankList.clear();
//                            if (baseResponse.getData() != null) {
//                                bankList.addAll(baseResponse.getData());
//                            }
//                            ifAddBankcard = bankList.size() != 0 ? true : false;
//                        } else {
//                            ToastUtil.show(activity, baseResponse.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onHandleError(BaseResponse baseResponse) {
//                        ToastUtil.show(activity, baseResponse.getMessage());
//                    }
//                });
    }

    /**
     * 发请求->充值接口
     */
    private void httpRecharge(int money) {
        PayHttpUtils.getInstance().toRecharge(money)
                .compose(RxSchedulers.<BaseResponse<UrlBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UrlBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UrlBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UrlBean> baseResponse) {
                        if (baseResponse.getData() != null) {
                            //1 成功 99 处理中
                            UrlBean urlBean = baseResponse.getData();
                            goWebActivity(RechargeActivity.this, urlBean.getUrl());
                        }
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onHandleError(BaseResponse<UrlBean> baseResponse) {
                        dismissLoadingDialog();
                    }
                });

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SELECT_BANKCARD) {
//            if (resultCode == RESULT_OK) {
//                selectBankcard = data.getParcelableExtra("bank_card");
//                if (!TextUtils.isEmpty(selectBankcard.getBankName())) {
//                    builder.setLength(0);
//                    builder.append(selectBankcard.getBankName());
//                    if (!TextUtils.isEmpty(selectBankcard.getCardNo())) {
//                        int length = selectBankcard.getCardNo().length();
//                        builder.append("(");
//                        builder.append(selectBankcard.getCardNo().substring(length - 4, length));
//                        builder.append(")");
//                    }
//                    tvBankNameTwo.setText(builder);//银行卡名称尾号
//                    if (!TextUtils.isEmpty(selectBankcard.getLogo())) {
//                        Glide.with(activity).load(selectBankcard.getLogo())
//                                .apply(options).into(ivBankIconTwo);
//                    }
//                }
//            }
//        }
//    }

    //显示密码错误弹窗
//    private void showPswErrorDialog(boolean canRetry, String msg) {
//        dialogErrorPassword = new DialogErrorPassword(this, R.style.MyDialogTheme);
//        dialogErrorPassword.setCanceledOnTouchOutside(false);
//        dialogErrorPassword.setCanRetry(canRetry);
//        dialogErrorPassword.setContent(msg);
//        dialogErrorPassword.setListener(new DialogErrorPassword.IErrorPasswordListener() {
//            @Override
//            public void onForget() {
//                startActivity(new Intent(activity, ForgetPswStepOneActivity.class).putExtra("from", 3));
//            }
//
//            @Override
//            public void onTry() {
//                if (dialogTwo != null) {
//                    dialogTwo.show();
//                }
//            }
//        });
//        dialogErrorPassword.show();
//    }

}
