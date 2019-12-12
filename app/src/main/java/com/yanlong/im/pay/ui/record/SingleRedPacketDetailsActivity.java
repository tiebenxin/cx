package com.yanlong.im.pay.ui.record;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.databinding.ActivityRedPacketDetailsBinding;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.ui.redenvelope.EnvelopeDetailBean;
import com.hm.cxpay.ui.redenvelope.EnvelopeReceiverBean;
import com.hm.cxpay.ui.redenvelope.FromUserBean;
import com.hm.cxpay.utils.DateUtils;
import com.hm.cxpay.utils.UIUtils;
import com.hm.cxpay.widget.CircleImageView;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PopupSelectView;

import java.util.ArrayList;
import java.util.List;


/**
 * 红包详情页面
 */
@Route(path = "/app/singleRedPacketDetailsActivity")
public class SingleRedPacketDetailsActivity extends BasePayActivity {
    private List<EnvelopeReceiverBean> list = new ArrayList<>();

    private String[] strings = {"查看支付宝红包记录", "取消"};
    private PopupSelectView popupSelectView;
    private EnvelopeDetailBean envelopeDetailBean;
    private ActivityRedPacketDetailsBinding ui;

    public static Intent newIntent(Context context, EnvelopeDetailBean bean) {
        Intent intent = new Intent(context, SingleRedPacketDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", bean);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_red_packet_details);
        ui.headView.getActionbar().setChangeStyleBg();
        ui.headView.getAppBarLayout().setBackgroundResource(com.hm.cxpay.R.color.c_c85749);
        envelopeDetailBean = getIntent().getParcelableExtra("data");
        initView();
        initEvent();
    }

    private void initView() {
        ui.headView.getActionbar().getBtnRight().setImageResource(R.mipmap.ic_more);
        ui.mtListView.init(new RedPacketAdapter());
        ui.mtListView.getLoadView().setStateNormal();
        initData();
    }


    private void initData() {
        if (envelopeDetailBean == null) {
            return;
        }
        FromUserBean userBean = envelopeDetailBean.getImUserInfo();
        if (userBean != null) {
            UIUtils.loadAvatar(userBean.getAvatar(), ui.ivAvatar);
            ui.tvName.setText(userBean.getNickname() + "的红包");
        }

        ui.tvContent.setText(TextUtils.isEmpty(envelopeDetailBean.getNote()) ? "恭喜发财，大吉大利" : envelopeDetailBean.getNote());
        ui.tvMoney.setText(UIUtils.getYuan(envelopeDetailBean.getAmt()));
        if (envelopeDetailBean.getType() == PayEnum.ERedEnvelopeType.NORMAL && envelopeDetailBean.getCnt() == 1) {
            ui.llRecord.setVisibility(View.GONE);
            ui.tvNote.setVisibility(View.GONE);
        } else {
            ui.llRecord.setVisibility(View.VISIBLE);
            ui.tvNote.setVisibility(View.VISIBLE);
            list = envelopeDetailBean.getRecvList();
            ui.mtListView.getListView().getAdapter().notifyDataSetChanged();
            UserBean user = PayEnvironment.getInstance().getUser();
            int remainCount = envelopeDetailBean.getRemainCnt();
            int totalCount = envelopeDetailBean.getCnt();
            String receivedMoney = UIUtils.getYuan(envelopeDetailBean.getAmt() - envelopeDetailBean.getRemainAmt());//已经抢了的钱
            String totalMoney = UIUtils.getYuan(envelopeDetailBean.getAmt());
            int receivedCount = totalCount - remainCount;
            if (user != null) {
                if (userBean.getUid() == user.getUid()) {//是自己发的
                    if (envelopeDetailBean.getRemainCnt() != 0) {//未抢完
                        ui.tvHint.setText("已领取" + receivedCount + "/" + totalCount + "个，共" + receivedMoney + "/" + totalMoney + "元");
                    } else {
                        String time = DateUtils.getGrabFinishedTime(envelopeDetailBean.getTime(), envelopeDetailBean.getFinishTime());
                        ui.tvHint.setText(totalCount + "个红包共" + totalMoney + "元，" + time + "被抢光");
                    }
                } else {
                    if (envelopeDetailBean.getRemainCnt() != 0) {//未抢完
                        ui.tvHint.setText("已领取" + receivedCount + "/" + totalCount + "个");
                    } else {
                        String time = DateUtils.getGrabFinishedTime(envelopeDetailBean.getTime(), envelopeDetailBean.getFinishTime());
                        ui.tvHint.setText(totalCount + "个红包，" + time + "被抢光");
                    }
                }
            }
        }

    }


    private void initEvent() {
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                initPopup();
            }
        });
    }

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(ui.headView.getActionbar(), Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:


                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }


    class RedPacketAdapter extends RecyclerView.Adapter<RedPacketAdapter.RbViewHolder> {


        @Override
        public RbViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            RbViewHolder holder = new RbViewHolder(inflater.inflate(R.layout.item_red_packet_details, viewGroup, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RbViewHolder viewHolder, int position) {
            RbViewHolder holder = viewHolder;
            EnvelopeReceiverBean envelopeReceiverBean = list.get(position);
            holder.bindData(envelopeReceiverBean);
        }

        @Override
        public int getItemCount() {
            if (list != null && list.size() > 0) {
                return list.size();
            }
            return 0;
        }


        class RbViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView ivAvatar;
            private TextView tvName;
            private TextView tvTime;
            private TextView tvMoney;


            public RbViewHolder(@NonNull View itemView) {
                super(itemView);
                ivAvatar = itemView.findViewById(R.id.sd_image_head);
                tvName = itemView.findViewById(R.id.tv_user_name);
                tvTime = itemView.findViewById(R.id.tv_date);
                tvMoney = itemView.findViewById(R.id.tv_money);
            }

            public void bindData(EnvelopeReceiverBean bean) {
                if (bean == null) {
                    return;
                }
                FromUserBean userBean = bean.getImUserInfo();
                if (userBean != null) {
                    UIUtils.loadAvatar(userBean.getAvatar(), ivAvatar);
                    tvName.setText(userBean.getNickname());
                }
                tvTime.setText(DateUtils.getGrabTime(bean.getTime()));
                tvMoney.setText(UIUtils.getYuan(bean.getAmt()));

            }
        }
    }


}
