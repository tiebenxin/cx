package com.yanlong.im.pay.ui.record;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hm.cxpay.ui.redenvelope.RedEnvelopeItemBean;
import com.hm.cxpay.utils.UIUtils;
import com.yanlong.im.R;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.TimeToString;

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

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(com.hm.cxpay.R.id.tv_user_name);
            tvMoney = itemView.findViewById(com.hm.cxpay.R.id.tv_money);
            tvTime = itemView.findViewById(com.hm.cxpay.R.id.tv_date);
        }

        public void bindData(RedEnvelopeItemBean bean) {
            tvName.setText(bean.getFromUser().getNickname());
            tvMoney.setText(UIUtils.getYuan(bean.getAmt()) + "元");
            tvTime.setText(TimeToString.YYYY_MM_DD_HH_MM_SS(bean.getTime()));
        }
    }
}
