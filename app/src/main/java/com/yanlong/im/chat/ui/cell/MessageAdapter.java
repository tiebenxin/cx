package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final ICellEventListener eventListener;
    private List<MsgAllBean> mList;
    private FactoryChatCell factoryChatCell;

    public MessageAdapter(Context c, ICellEventListener l) {
        context = c;
        eventListener = l;
        mList = new ArrayList<>();

    }

    public MessageAdapter setCellFactory(FactoryChatCell factory) {
        factoryChatCell = factory;
        return this;
    }

    public void bindData(List<MsgAllBean> list, int page) {
        if (mList == null) {
            mList = list;
        } else {
            if (page == 0) {
                mList.clear();
            }
            mList.addAll(0, list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ChatEnum.EChatCellLayout layout = ChatEnum.EChatCellLayout.fromOrdinal(viewType);
        ChatCellBase cell = factoryChatCell.createCell(layout, viewGroup);
        return new RecyclerViewHolder(cell.getView());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ChatCellBase cellBase = (ChatCellBase) viewHolder.itemView.getTag();
        cellBase.putMessage(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;

    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null && mList.size() > position) {
            return mList.get(position).getChatCellLayoutId().ordinal();
        }
        return super.getItemViewType(position);
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
