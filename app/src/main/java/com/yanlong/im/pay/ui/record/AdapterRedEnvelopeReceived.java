package com.yanlong.im.pay.ui.record;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hm.cxpay.bean.RedEnvelopeItemBean;
import com.hm.cxpay.utils.DateUtils;
import com.hm.cxpay.utils.UIUtils;
import com.yanlong.im.R;

import net.cb.cb.library.base.AbstractRecyclerAdapter;

/**
 * @author Liszt
 * @date 2019/12/3
 * Description
 */
public class AdapterRedEnvelopeReceived extends AbstractRecyclerAdapter<RedEnvelopeItemBean> {
    public AdapterRedEnvelopeReceived(Context ctx) {
        super(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReceivedViewHolder(mInflater.inflate(R.layout.item_red_packet_record, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        RedEnvelopeItemBean bean = mBeanList.get(position);
        ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;
        viewHolder.bindData(bean);
    }


    public class ReceivedViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvMoney;
        private TextView tvTime;
        private RedEnvelopeItemBean model;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(com.hm.cxpay.R.id.tv_user_name);
            tvMoney = itemView.findViewById(com.hm.cxpay.R.id.tv_money);
            tvTime = itemView.findViewById(com.hm.cxpay.R.id.tv_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null && model != null) {
                        mItemClickListener.onItemClick(model);
                    }
                }
            });
        }

        public void bindData(RedEnvelopeItemBean bean) {
            model = bean;
            if (bean.getFromUser() != null) {
                tvName.setText(bean.getFromUser().getNickname());
            }
            tvMoney.setText(UIUtils.getYuan(bean.getAmt()) + "元");
            tvTime.setText(DateUtils.getFullTime(bean.getTime()));
        }
    }
}
