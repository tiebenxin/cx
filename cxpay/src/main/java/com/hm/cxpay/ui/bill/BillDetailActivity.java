package com.hm.cxpay.ui.bill;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hm.cxpay.R;
import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.utils.UIUtils;
import com.luck.picture.lib.tools.DateUtils;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import java.math.BigDecimal;

/**
 * @类名：账单详情
 * @Date：2019/12/11
 * @by zjy
 * @备注：
 */

public class BillDetailActivity extends AppActivity {
    private HeadView headView;
    private ActionbarView actionbar;
    private Activity activity;
    private ImageView ivTitleImage;//顶部图标
    private TextView tvTitle;//顶部标题
    private TextView tvContent;//顶部内容

    private RelativeLayout layoutQuestion;//常见问题

    private RelativeLayout layoutTransferSend;//转账给别人布局展示
    private TextView tvTransferSendStatus;//转账给别人-交易状态
    private TextView tvTransferSendExplain;//转账给别人-转账说明
    private TextView tvTransferTime;//转账给别人-转账时间
    private TextView tvTransferSendTime;//转账给别人-收款时间
    private TextView tvTransferSendPayStyle;//转账给别人-支付方式
    private TextView tvTransferSendOrderId;//转账给别人-交易单号

    private RelativeLayout layoutTransferGet;//转账收款布局展示
    private TextView tvTransferGetStatus;//转账收款-交易状态
    private TextView tvTransferGetExplain;//转账收款-转账说明
    private TextView tvTransferGetTime;//转账收款-收款时间
    private TextView tvTransferGetOrderId;//转账收款-交易单号

    private RelativeLayout layoutRedPacket;//红包布局展示
    private ImageView ivRedpacketImage;//红包顶部图标
    private TextView tvRedPacketStatus;//红包-交易状态
    private TextView tvRedPacketDetail;//红包-红包详情
    private TextView tvRedPacketPayTime;//红包-支付时间
    private TextView tvRedPacketPayStyle;//红包-支付方式
    private TextView tvRedPacketGetMoneyTime;//红包-收款时间
    private TextView titleTvRedPacketGetMoneyTime;//红包-收款时间左侧标题
    private TextView titleTvRedPacketPayTime;//红包-支付时间左侧标题
    private TextView titleTvRedPacketPayStyle;//红包-支付方式左侧标题
    private TextView tvRedPacketOrderId;//红包-交易单号


    private RelativeLayout layoutRecharge;//充值布局展示
    private TextView tvRechargeStatus;//充值-交易状态
    private TextView tvRechargeTime;//充值-充值时间
    private TextView tvRechargeBank;//充值-支付银行
    private TextView tvRechargeOrderId;//充值-交易单号

    private RelativeLayout layoutWithdrawOne;//提现布局A展示
    private RelativeLayout layoutWithdrawTwo;//提现布局B展示
    private TextView tvWithdrawStatusOne;//提现状态1
    private TextView tvWithdrawStatusTwo;//提现状态2
    private TextView tvWithdrawStatusThree;//提现状态3
    private ImageView ivWithdrawStatusTwo;//提现状态下标线2
    private ImageView ivWithdrawFinished;//提现状态完成图标
    private TextView tvWithdrawMoney;//提现-提现金额
    private TextView tvWithdrawCreateTime;//提现-申请时间
    private TextView tvWithdrawGetTime;//提现-到账时间
    private TextView tvWithdrawRealMoney;//提现-到账金额
    private TextView tvWithdrawCharge;//提现-手续费
    private TextView tvWithdrawBank;//提现-提现银行
    private TextView tvWithdrawOrderId;//提现-交易单号


    private CommonBean data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_bill_detail);
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        layoutQuestion = findViewById(R.id.layout_question);
        ivTitleImage = findViewById(R.id.iv_title_image);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        layoutTransferSend = findViewById(R.id.layout_transfer_send);
        tvTransferSendStatus = findViewById(R.id.tv_transfer_send_status);
        tvTransferSendExplain = findViewById(R.id.tv_transfer_send_explain);
        tvTransferTime = findViewById(R.id.tv_transfer_time);
        tvTransferSendTime = findViewById(R.id.tv_transfer_send_time);
        tvTransferSendPayStyle = findViewById(R.id.tv_transfer_send_pay_style);
        tvTransferSendOrderId = findViewById(R.id.tv_transfer_send_order_id);
        layoutTransferGet = findViewById(R.id.layout_transfer_get);
        tvTransferGetStatus = findViewById(R.id.tv_transfer_get_status);
        tvTransferGetExplain = findViewById(R.id.tv_transfer_get_explain);
        tvTransferGetTime = findViewById(R.id.tv_transfer_get_time);
        tvTransferGetOrderId = findViewById(R.id.tv_transfer_get_order_id);
        layoutRedPacket = findViewById(R.id.layout_red_packet);
        tvRedPacketStatus = findViewById(R.id.tv_red_packet_status);
        tvRedPacketDetail = findViewById(R.id.tv_red_packet_detail);
        tvRedPacketPayTime = findViewById(R.id.tv_red_packet_pay_time);
        tvRedPacketPayStyle = findViewById(R.id.tv_red_packet_pay_style);
        tvRedPacketGetMoneyTime = findViewById(R.id.tv_red_packet_get_money_time);
        tvRedPacketOrderId = findViewById(R.id.tv_red_packet_order_id);
        layoutRecharge = findViewById(R.id.layout_recharge);
        tvRechargeStatus = findViewById(R.id.tv_recharge_status);
        tvRechargeTime = findViewById(R.id.tv_recharge_time);
        tvRechargeBank = findViewById(R.id.tv_recharge_bank);
        tvRechargeOrderId = findViewById(R.id.tv_recharge_order_id);
        layoutWithdrawOne = findViewById(R.id.layout_withdraw_one);
        layoutWithdrawTwo = findViewById(R.id.layout_withdraw_two);
        tvWithdrawStatusOne = findViewById(R.id.tv_withdraw_status_one);
        tvWithdrawStatusTwo = findViewById(R.id.tv_withdraw_status_two);
        tvWithdrawStatusThree = findViewById(R.id.tv_withdraw_status_three);
        ivWithdrawStatusTwo = findViewById(R.id.iv_withdraw_status_two);
        ivWithdrawFinished = findViewById(R.id.iv_withdraw_finished);
        tvWithdrawMoney = findViewById(R.id.tv_withdraw_money);
        tvWithdrawCreateTime = findViewById(R.id.tv_withdraw_create_time);
        tvWithdrawGetTime = findViewById(R.id.tv_withdraw_get_time);
        tvWithdrawRealMoney = findViewById(R.id.tv_withdraw_real_money);
        tvWithdrawCharge = findViewById(R.id.tv_withdraw_charge);
        tvWithdrawBank = findViewById(R.id.tv_withdraw_bank);
        tvWithdrawOrderId= findViewById(R.id.tv_withdraw_order_id);
        ivRedpacketImage= findViewById(R.id.iv_redpacket_image);
        titleTvRedPacketGetMoneyTime= findViewById(R.id.title_tv_red_packet_get_money_time);
        titleTvRedPacketPayTime= findViewById(R.id.title_tv_red_packet_pay_time);
        titleTvRedPacketPayStyle= findViewById(R.id.title_tv_red_packet_pay_style);
        actionbar = headView.getActionbar();
    }

    private void initData() {
        if (getIntent().getParcelableExtra("item_data") != null) {
            data = getIntent().getParcelableExtra("item_data");
        } else {
            data = new CommonBean();
        }
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        layoutQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/HelpActivity").navigation();
            }
        });
        //类型：1转账给 2发红包给 3充值 4提现 5红包退款 6消费(忽略) 7红包收款 8转账收款 9转账退款
        int type = data.getTradeType();
        if(type == 2 || type == 5 || type == 7){
            ivRedpacketImage.setVisibility(View.VISIBLE);
            ivTitleImage.setVisibility(View.GONE);
            ivRedpacketImage.setImageResource(R.mipmap.ic_redpackage);
        }else {
            ivRedpacketImage.setVisibility(View.GONE);
            ivTitleImage.setVisibility(View.VISIBLE);
            ivTitleImage.setImageResource(selectImg(type));
        }
        //1 转账给别人
        if (type == 1) {
            layoutTransferSend.setVisibility(View.VISIBLE);
            if (data.getOtherUser() != null && !TextUtils.isEmpty(data.getOtherUser().getNickname())) {
                tvTitle.setText("转账-转给" + data.getOtherUser().getNickname());
            } else {
                tvTitle.setText("转账-转给");
            }
            //根据收支类型->显示操作金额
            if (data.getIncome() == 1) { //1 收入 其他支出
                tvContent.setText("+" + UIUtils.getYuan(data.getAmt()));
            } else {
                tvContent.setText("-" + UIUtils.getYuan(data.getAmt()));
            }
            if (data.getStat() == 1) {
                tvTransferSendStatus.setText("朋友已收钱");
            } else if (data.getStat() == 2) {
                tvTransferSendStatus.setText("已部分退款");
            } else if (data.getStat() == 99) {
                tvTransferSendStatus.setText("处理中");
            } else if (data.getStat() == 200) {
                tvTransferSendStatus.setText("已全额退款");
            }
            if (!TextUtils.isEmpty(data.getNote())) {
                tvTransferSendExplain.setText(data.getNote());
            }
            tvTransferTime.setText(DateUtils.timeStamp2Date(data.getCreateTime(), ""));
            tvTransferSendTime.setText(DateUtils.timeStamp2Date(data.getStatConfirmTime(), ""));
            if (data.getBillType() == 1) {
                tvTransferSendPayStyle.setText("零钱");
            }
            tvTransferSendOrderId.setText(data.getTradeId() + "");

        } else if (type == 2 || type == 5 || type == 7) {
            layoutRedPacket.setVisibility(View.VISIBLE);
            if (type == 2) {
                if (data.getOtherUser() != null && !TextUtils.isEmpty(data.getOtherUser().getNickname())) {
                    tvTitle.setText("零钱红包-发给" + data.getOtherUser().getNickname());
                } else {
                    tvTitle.setText("零钱红包-发给");
                }
                tvRedPacketGetMoneyTime.setVisibility(View.GONE);
                titleTvRedPacketGetMoneyTime.setVisibility(View.GONE);
            } else if (type == 5) {
                tvTitle.setText("零钱红包-过期退款");
                tvRedPacketPayStyle.setVisibility(View.GONE);
                tvRedPacketPayTime.setVisibility(View.GONE);
                titleTvRedPacketPayTime.setVisibility(View.GONE);
                titleTvRedPacketPayStyle.setVisibility(View.GONE);
            } else if (type == 7) {
                if (data.getOtherUser() != null && !TextUtils.isEmpty(data.getOtherUser().getNickname())) {
                    tvTitle.setText("零钱红包-来自" + data.getOtherUser().getNickname());
                } else {
                    tvTitle.setText("零钱红包-来自");
                }
                tvRedPacketPayStyle.setVisibility(View.GONE);
                tvRedPacketPayTime.setVisibility(View.GONE);
                titleTvRedPacketPayTime.setVisibility(View.GONE);
                titleTvRedPacketPayStyle.setVisibility(View.GONE);

            }
            //根据收支类型->显示操作金额
            if (data.getIncome() == 1) { //1 收入 其他支出
                tvContent.setText("+" + UIUtils.getYuan(data.getAmt()));
            } else {
                tvContent.setText("-" + UIUtils.getYuan(data.getAmt()));
            }
            tvRedPacketDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 红包详情点击
                    ToastUtil.show(activity, "红包详情点击");
                }
            });
            if (data.getStat() == 1) {
                if (type == 2) {
                    tvRedPacketStatus.setText("支付成功");
                } else if (type == 7) {
                    tvRedPacketStatus.setText("已存入零钱");
                } else {
                    tvRedPacketStatus.setText("成功");
                }
            } else if (data.getStat() == 2) {
                tvRedPacketStatus.setText("已部分退款");
            } else if (data.getStat() == 99) {
                tvRedPacketStatus.setText("处理中");
            } else if (data.getStat() == 200) {
                tvRedPacketStatus.setText("已全额退款");
            }
            tvRedPacketPayTime.setText(DateUtils.timeStamp2Date(data.getCreateTime(), ""));
            if (data.getBillType() == 1) {
                tvRedPacketPayStyle.setText("零钱");
            }
            tvRedPacketGetMoneyTime.setText(DateUtils.timeStamp2Date(data.getStatConfirmTime(), ""));
            tvRedPacketOrderId.setText(data.getTradeId() + "");

            //3 充值
        } else if (type == 3) {
            layoutRecharge.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(data.getBankCardInfo())){
                tvTitle.setText("充值-来自"+data.getBankCardInfo());
            }else {
                tvTitle.setText("充值-来自");
            }
            tvContent.setText("¥"+UIUtils.getYuan(data.getAmt())+"元");
            if (data.getStat() == 1) {
                tvRechargeStatus.setText("充值成功");
            } else if (data.getStat() == 2) {
                tvRechargeStatus.setText("已部分退款");
            } else if (data.getStat() == 99) {
                tvRechargeStatus.setText("处理中");
            } else if (data.getStat() == 200) {
                tvRechargeStatus.setText("已全额退款");
            }
            tvRechargeTime.setText(DateUtils.timeStamp2Date(data.getCreateTime(), ""));
            tvRechargeBank.setText(data.getBankCardInfo());
            tvRechargeOrderId.setText(data.getTradeId() + "");

            //4 提现
        } else if (type == 4) {
            layoutWithdrawOne.setVisibility(View.VISIBLE);
            layoutWithdrawTwo.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(data.getBankCardInfo())){
                tvTitle.setText("提现-到"+data.getBankCardInfo());
            }else {
                tvTitle.setText("提现-到");
            }
            tvContent.setText("¥"+UIUtils.getYuan(data.getAmt())+"元");
            tvWithdrawStatusOne.setText("发起提现\n"+DateUtils.timeStamp2Date(data.getCreateTime(), ""));
            if (data.getStat() == 1) {
                tvWithdrawStatusTwo.setText("银行处理中");
                ivWithdrawStatusTwo.setVisibility(View.VISIBLE);
                tvWithdrawStatusThree.setText("到账\n"+DateUtils.timeStamp2Date(data.getStatConfirmTime(), ""));
                tvWithdrawStatusThree.setVisibility(View.VISIBLE);
                ivWithdrawFinished.setVisibility(View.VISIBLE);
            } else {
                tvWithdrawStatusTwo.setText("提现失败，请联系平台客服");
            }
            tvWithdrawMoney.setText("¥"+UIUtils.getYuan(data.getAmt())+"元");
            tvWithdrawCreateTime.setText(DateUtils.timeStamp2Date(data.getCreateTime(), ""));
            tvWithdrawGetTime.setText(DateUtils.timeStamp2Date(data.getStatConfirmTime(), ""));
            tvWithdrawCharge.setText("¥"+UIUtils.getYuan(data.getFee())+"元");

            BigDecimal b1 = new BigDecimal(UIUtils.getYuan(data.getAmt()));
            BigDecimal b2 = new BigDecimal(UIUtils.getYuan(data.getFee()));
            String realMoney =  b1.subtract(b2)+"";
            tvWithdrawRealMoney.setText("¥"+realMoney+"元");
            if(!TextUtils.isEmpty(data.getBankCardInfo())){
                tvWithdrawBank.setText(data.getBankCardInfo());
            }
            tvWithdrawOrderId.setText(data.getTradeId() + "");


            //8 转账收款 9转账退款 (TODO 没出退款设计图，暂用同一个样式)
        } else if (type == 8 || type == 9) {
            layoutTransferGet.setVisibility(View.VISIBLE);
            if (data.getOtherUser() != null && !TextUtils.isEmpty(data.getOtherUser().getNickname())) {
                tvTitle.setText("转账收款-来自" + data.getOtherUser().getNickname());
            } else {
                tvTitle.setText("转账收款-来自");
            }
            //根据收支类型->显示操作金额
            if (data.getIncome() == 1) { //1 收入 其他支出
                tvContent.setText("+" + UIUtils.getYuan(data.getAmt()));
            } else {
                tvContent.setText("-" + UIUtils.getYuan(data.getAmt()));
            }
            if (data.getStat() == 1) {
                tvTransferGetStatus.setText("已存入零钱");
            } else if (data.getStat() == 2) {
                tvTransferGetStatus.setText("已部分退款");
            } else if (data.getStat() == 99) {
                tvTransferGetStatus.setText("处理中");
            } else if (data.getStat() == 200) {
                tvTransferGetStatus.setText("已全额退款");
            }
            if (!TextUtils.isEmpty(data.getNote())) {
                tvTransferGetExplain.setText(data.getNote());
            }
            tvTransferGetTime.setText(DateUtils.timeStamp2Date(data.getStatConfirmTime(), ""));
            tvTransferGetOrderId.setText(data.getTradeId() + "");
        }

    }


    /**
     * 根据不同交易类型显示不同图标
     *
     * @return
     */
    private int selectImg(int ImageType) {
        switch (ImageType) {
            case 1:
            case 8:
            case 9:
                return R.mipmap.ic_transfer;
            case 3:
                return R.mipmap.ic_recharge_trade;
            case 4:
                return R.mipmap.ic_withdraw_trade;
            default:
                return R.mipmap.ic_transfer;
        }
    }

}
