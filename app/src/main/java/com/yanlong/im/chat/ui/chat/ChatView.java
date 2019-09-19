package com.yanlong.im.chat.ui.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.ui.cell.ICellEventListener;
import com.yanlong.im.chat.ui.cell.MessageAdapter;
import com.yanlong.im.databinding.ActivityChat2Binding;

import net.cb.cb.library.base.IView;

import java.util.List;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class ChatView implements IView, ICellEventListener {

    private ActivityChat2Binding ui;
    private Context context;
    private boolean isGroup;
    private MessageAdapter adapter;
    private LinearLayoutManager layoutManager;

    public void initView(ActivityChat2Binding ui, Context c, boolean group) {
        this.ui = ui;
        context = c;
        isGroup = group;
        intAdapter();
    }

    private void intAdapter() {
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(context, this, isGroup);
        ui.recyclerView.setAdapter(adapter);

    }

    public void setAndRefreshData(List<MsgAllBean> l) {
        adapter.bindData(l);
        ui.recyclerView.scrollToPosition(adapter.getItemCount() - 1);

    }

    @Override
    public void onEvent(int type, MsgAllBean message, Object... args) {

    }
}
