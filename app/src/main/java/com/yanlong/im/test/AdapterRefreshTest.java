package com.yanlong.im.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.base.AbstractViewHolder;

/**
 * Created by LL130386 on 2018/8/28.
 */

public class AdapterRefreshTest extends AbstractRecyclerAdapter {

    public AdapterRefreshTest(Context ctx) {
        super(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_text, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mBeanList == null ? 0 : mBeanList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((AbstractViewHolder) holder).bindHolder(mBeanList.get(position));
    }


    class TextViewHolder extends AbstractViewHolder {

        private final TextView tv_content;

        public TextViewHolder(View itemView) {
            super(itemView);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }

        @Override
        public void bindHolder(Object bean) {
            if (bean instanceof String) {
                tv_content.setText((String) bean);
            }
        }
    }


}
