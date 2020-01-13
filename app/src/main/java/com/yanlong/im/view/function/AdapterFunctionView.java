package com.yanlong.im.view.function;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.base.AbstractRecyclerAdapter;

/**
 * @author Liszt
 * @date 2020/1/11
 * Description
 */
public class AdapterFunctionView extends AbstractRecyclerAdapter<FunctionItemModel> {


    private ChatExtendMenuView.OnFunctionListener functionListener;

    public AdapterFunctionView(Context context) {
        super(context);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FunctionViewHolder(mInflater.inflate(R.layout.item_function, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        FunctionViewHolder holder = (FunctionViewHolder) viewHolder;
        holder.bindData(mBeanList.get(position));
    }

    class FunctionViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvContent;

        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            initView();

        }

        private void initView() {
            ivImage = itemView.findViewById(R.id.iv_img);
            tvContent = itemView.findViewById(R.id.tv_content);
        }

        private void bindData(FunctionItemModel model) {
            ivImage.setImageResource(model.getDrawableId());
            tvContent.setText(model.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (functionListener != null) {
                        functionListener.onClick(model.getId());
                    }
                }
            });
        }
    }

    public void setFunctionListner(ChatExtendMenuView.OnFunctionListener l) {
        functionListener = l;
    }
}
