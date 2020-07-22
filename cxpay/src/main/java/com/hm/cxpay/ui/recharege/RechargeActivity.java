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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hm.cxpay.R;
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.bean.UrlBean;
import com.hm.cxpay.dailog.DialogBalanceNoEnough;
import com.hm.cxpay.dailog.DialogDefault;
import com.hm.cxpay.dailog.DialogEnvelope;
import com.hm.cxpay.dailog.DialogErrorPassword;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.YiBaoWebActivity;
import com.hm.cxpay.utils.UIUtils;
import com.hm.cxpay.widget.PswView;

import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogCommon2;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.WebPageActivity;

import java.util.ArrayList;
import java.util.List;

import static com.hm.cxpay.global.PayConstants.REQUEST_PAY;
import static com.hm.cxpay.global.PayConstants.RESULT;


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
    private TextView tvQuestion;//常见问题
    private DialogCommon2 noticeDialog;//弹框提示
    private DialogErrorPassword dialogErrorPassword;
    public static final int SELECT_BANKCARD = 99;

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
        tvQuestion = findViewById(R.id.tv_question);
        actionbar = headView.getActionbar();
        noticeDialog = new DialogCommon2(RechargeActivity.this);
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
        //显示余额
        if (PayEnvironment.getInstance().getUser() != null) {
            tvBalance.setText("当前零钱余额  ¥ " + UIUtils.getYuan(Long.valueOf(PayEnvironment.getInstance().getUser().getBalance())));
        }

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                //1 充值金额不能为空
                if (!TextUtils.isEmpty(etRecharge.getText().toString())) {
                    //2 最低充值10元
                    double yuan = Double.valueOf(etRecharge.getText().toString());
                    //TODO:备注最低充值金额为10元, 开发时改为1元
                    if (yuan >= 10.00) {
                        //3 单笔充值最高不能超过500元
                        if (yuan <= 500.00) {
                            showLoadingDialog();
                            httpRecharge(yuan);
                        } else {
                            noticeDialog.setContent("单笔充值最高不能超过500元", true)
                                    .setButtonTxt("确定")
                                    .hasTitle(false)
                                    .setListener(new DialogCommon2.IDialogListener() {
                                        @Override
                                        public void onClick() {
                                            noticeDialog.dismiss();
                                        }
                                    }).show();
                        }
                    } else {
                        noticeDialog.setContent("最低充值金额10元", true)
                                .setButtonTxt("确定")
                                .hasTitle(false)
                                .setListener(new DialogCommon2.IDialogListener() {
                                    @Override
                                    public void onClick() {
                                        noticeDialog.dismiss();
                                    }
                                }).show();
                    }
                } else {
                    noticeDialog.setContent("充值金额不能为空", true)
                            .setButtonTxt("确定")
                            .hasTitle(false)
                            .setListener(new DialogCommon2.IDialogListener() {
                                @Override
                                public void onClick() {
                                    noticeDialog.dismiss();
                                }
                            }).show();
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
                    //根据输入的数字更新选中状态
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

    /**
     * 发请求->充值接口
     */
    private void httpRecharge(final double money) {
        PayHttpUtils.getInstance().toRecharge(money)
                .compose(RxSchedulers.<BaseResponse<UrlBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UrlBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UrlBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UrlBean> baseResponse) {
                        if (baseResponse.getData() != null) {
                            LogUtil.writeLog("支付--充值--money=" + money + "--time" + System.currentTimeMillis());
                            //1 成功 99 处理中
                            UrlBean urlBean = baseResponse.getData();
                            Intent intent = new Intent(RechargeActivity.this, YiBaoWebActivity.class);
                            intent.putExtra(YiBaoWebActivity.AGM_URL, urlBean.getUrl());
                            startActivityForResult(intent, REQUEST_PAY);
                        }
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onHandleError(BaseResponse<UrlBean> baseResponse) {
                        if (context != null) {
                            ToastUtil.show(context, baseResponse.getMessage());
                        }
                        dismissLoadingDialog();
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_PAY) {
            int result = data.getIntExtra(RESULT, 0);
            if (result == 99) {
                startActivity(new Intent(activity, RechargeSuccessActivity.class).putExtra("money", etRecharge.getText().toString()));
            } else {
                ToastUtil.show("充值失败");
            }
        }
    }
}
