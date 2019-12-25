package com.hm.cxpay.ui.transfer;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.TransferDetailBean;
import com.hm.cxpay.databinding.ActivityTransferDetailBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.utils.DateUtils;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;

/**
 * @author Liszt
 * @date 2019/12/19
 * Description 转账详情
 */
public class TransferDetailActivity extends BasePayActivity {

    private ActivityTransferDetailBinding ui;
    //    private CxTransferBean bean;
    private boolean isFromMe;
    private String tradeId;

    public static Intent newIntent(Context context, String tradeId, boolean isFromMe) {
        Intent intent = new Intent(context, TransferDetailActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("data", bean);
//        intent.putExtras(bundle);
        intent.putExtra("isFromMe", isFromMe);
        intent.putExtra("tradeId", tradeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_transfer_detail);
        Intent intent = getIntent();
//        bean = intent.getParcelableExtra("data");
        isFromMe = intent.getBooleanExtra("isFromMe", false);
        tradeId = intent.getStringExtra("tradeId");
        initView();
        httpGetDetail();
    }

    private void initView() {
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
            }
        });

        //提醒对方收款
        ui.tvNoticeReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //立即退还
        ui.tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //收款
        ui.tvReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void initData(TransferDetailBean detailBean) {
        if (detailBean != null) {
            //领取转账 stat: 1未领取 2已领取 3已拒收 4已过期
            int status = detailBean.getStat();
            updateUI(detailBean.getIncome(), status);
            ui.ivIcon.setImageResource(getDrawableId(status));
            ui.tvNote.setText(getNote(detailBean.getIncome(), status, detailBean.getRecvUser().getNickname()));
            ui.tvMoney.setText(UIUtils.getYuan(detailBean.getAmt()));
            updateTimeUI(detailBean, status);
        }

    }

    private void updateTimeUI(TransferDetailBean detailBean, int status) {
        ui.tvTimeTransfer.setText("转账时间：" + DateUtils.getTransferTime(detailBean.getTransTime()));
        if (status == 1) {
            ui.tvTimeReturn.setVisibility(View.GONE);
        } else if (status == 2) {
            ui.tvTimeReturn.setVisibility(View.VISIBLE);
            ui.tvTimeTransfer.setText("收款时间：" + DateUtils.getTransferTime(detailBean.getRecvTime()));
        } else if (status == 3) {
            ui.tvTimeReturn.setVisibility(View.VISIBLE);
            ui.tvTimeTransfer.setText("退还时间：" + DateUtils.getTransferTime(detailBean.getRejectTime()));
        } else if (status == 4) {
            ui.tvTimeReturn.setVisibility(View.GONE);
        }
    }

    private int getDrawableId(int status) {
        if (status == 1) {
            return R.mipmap.ic_wait_collection;
        } else if (status == 2) {
            return R.mipmap.ic_transfer_success;
        } else if (status == 3) {
            return R.mipmap.ic_return_transfer;
        } else if (status == 4) {//过期
            return R.mipmap.ic_return_transfer;
        } else {
            return R.mipmap.ic_wait_collection;
        }
    }

    private String getNote(int income, int status, String nick) {
        String note = "";
        if (status == 1) {
            if (income == 1) {
                note = "等待确认收款";
            } else {
                note = "等待" + nick + "确认收款";
            }
        } else if (status == 2) {
            if (income == 1) {
                note = "已收款";
            } else {
                note = nick + "已收款";
            }
        } else if (status == 3) {
            note = "已退还";
        } else if (status == 4) {//过期
            return "过期已退还";
        }
        return note;
    }

    private void updateUI(int income, int status) {
        if (income == 1) {
            if (status == 1) {
                ui.llReceive.setVisibility(View.VISIBLE);
                ui.llReturn.setVisibility(View.GONE);
                ui.llWaitReceive.setVisibility(View.GONE);
            } else if (status == 2) {
                ui.llReceive.setVisibility(View.GONE);
                ui.llReturn.setVisibility(View.GONE);
                ui.llWaitReceive.setVisibility(View.GONE);
            } else if (status == 3) {
                ui.llReceive.setVisibility(View.GONE);
                ui.llReturn.setVisibility(View.GONE);
                ui.llWaitReceive.setVisibility(View.GONE);
            } else if (status == 4) {
                ui.llReceive.setVisibility(View.GONE);
                ui.llReturn.setVisibility(View.GONE);
                ui.llWaitReceive.setVisibility(View.GONE);
            }
        } else {
            if (status == 1) {
                ui.llReceive.setVisibility(View.GONE);
                ui.llReturn.setVisibility(View.GONE);
                ui.llWaitReceive.setVisibility(View.VISIBLE);
            } else if (status == 2) {
                ui.llReceive.setVisibility(View.GONE);
                ui.llReturn.setVisibility(View.GONE);
                ui.llWaitReceive.setVisibility(View.GONE);
            } else if (status == 3) {
                ui.llReceive.setVisibility(View.GONE);
                ui.llReturn.setVisibility(View.VISIBLE);
                ui.llWaitReceive.setVisibility(View.GONE);
            } else if (status == 4) {
                ui.llReceive.setVisibility(View.GONE);
                ui.llReturn.setVisibility(View.GONE);
                ui.llWaitReceive.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 获取账单详情
     */
    private void httpGetDetail() {
        PayHttpUtils.getInstance().getTransferDetail(tradeId)
                .compose(RxSchedulers.<BaseResponse<TransferDetailBean>>compose())
                .compose(RxSchedulers.<BaseResponse<TransferDetailBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<TransferDetailBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<TransferDetailBean> baseResponse) {
                        if (baseResponse.getData() != null) {
                            //如果当前页有数据
                            TransferDetailBean detailBean = baseResponse.getData();
                            initData(detailBean);
                        } else {

                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse<TransferDetailBean> baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });

    }


}
