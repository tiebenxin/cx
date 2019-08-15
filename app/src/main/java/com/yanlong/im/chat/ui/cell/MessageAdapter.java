package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.server.UpLoadService;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final ICellEventListener eventListener;
    private List<MsgAllBean> mList;
    private FactoryChatCell factoryChatCell;
    private final boolean isGroup;//是否群聊

    public MessageAdapter(Context c, ICellEventListener l, boolean isG) {
        context = c;
        eventListener = l;
        mList = new ArrayList<>();
        isGroup = isG;

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

    public boolean isGroup() {
        return isGroup;
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
        cellBase.putMessage(mList.get(position), position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(viewHolder, position, payloads);
        } else {
            MsgAllBean msg = mList.get(position);
            if (msg.getMsg_type() == ChatEnum.EMessageType.IMAGE) {
                ChatCellImage imageCell = (ChatCellImage) viewHolder.itemView.getTag();
                imageCell.updateMessage(msg);
                int progress = UpLoadService.getProgress(msg.getMsg_id());
                imageCell.updateProgress(msg.getSend_state(), progress);
            }
        }
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

    //获取某位置消息
    public MsgAllBean getPositionMessage(int position) {
        if (mList != null && mList.size() > position) {
            return mList.get(position);
        }
        return null;
    }

    public int getMessagePosition(MsgAllBean bean) {
        return mList.indexOf(bean);
    }

    //局部刷新
    public void notifyItemChanged(MsgAllBean bean, @NonNull List payloads) {
        if (mList.contains(bean)) {
            int position = mList.indexOf(bean);
            mList.set(position, bean);
            notifyItemChanged(position, payloads);
        }

    }

}
