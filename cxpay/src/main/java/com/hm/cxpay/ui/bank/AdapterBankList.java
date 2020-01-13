package com.hm.cxpay.ui.bank;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hm.cxpay.R;
import com.hm.cxpay.bean.BankBean;

import net.cb.cb.library.base.AbstractRecyclerAdapter;

/**
 * @author Liszt
 * @date 2019/11/30
 * Description
 */
public class AdapterBankList extends AbstractRecyclerAdapter<BankBean> {

    private Context context;
    private DeleteClickListener listener;
    private int type;//  0 不显示移除(切换银行卡) 1 显示移除(我的银行卡)

    public AdapterBankList(Context c, int type) {
        super(c);
        context = c;
        this.type = type;
    }

    public void setDeleteClickListener(DeleteClickListener listener){
        this.listener = listener;
    }

    public void removeView(int position){
        mBeanList.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new BankViewHolder(mInflater.inflate(R.layout.item_bank, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof BankViewHolder) {
            BankViewHolder holder = (BankViewHolder) viewHolder;
            holder.bindData(mBeanList.get(i),i);
        }
    }

    @Override
    public int getItemCount() {
        return mBeanList == null ? 0 : mBeanList.size();
    }

    class BankViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivIcon;
        private final TextView tvBankName;
        private final TextView tvBankNum;
        private final View rootView;
        private final TextView tvDeleteBankcard;
        private RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        public BankViewHolder(@NonNull View v) {
            super(v);
            rootView = v;
            ivIcon = v.findViewById(R.id.iv_icon);
            tvBankName = v.findViewById(R.id.tv_bank_name);
            tvBankNum = v.findViewById(R.id.tv_bank_num);
            tvDeleteBankcard = v.findViewById(R.id.tv_delete_bankcard);
        }

        private void bindData(final BankBean bank, final int position) {
            if(!TextUtils.isEmpty(bank.getLogo())){
                Glide.with(context).load(bank.getLogo())
                        .apply(options).into(ivIcon);
            }else {
                ivIcon.setImageResource(R.mipmap.ic_bank_zs);
            }
            if(!TextUtils.isEmpty(bank.getBankName())){
                tvBankName.setText(bank.getBankName());
            }
            if(!TextUtils.isEmpty(bank.getCardNo())){
                tvBankNum.setText(bank.getCardNo());
            }

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(bank);
                    }
                }
            });
            if(type!=0){
                tvDeleteBankcard.setVisibility(View.VISIBLE);
                tvDeleteBankcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bank!=null){
                            listener.onDeleteClick(bank.getId()+"",position);
                        }
                    }
                });
            }
        }
    }

    public interface DeleteClickListener{
        void onDeleteClick(String bankcardId,int position);
    }


}
