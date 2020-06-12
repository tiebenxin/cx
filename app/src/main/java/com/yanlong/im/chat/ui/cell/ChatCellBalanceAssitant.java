package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.BalanceAssistantMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.ui.view.LabelItemView;

public class ChatCellBalanceAssitant extends ChatCellBase {


    private LabelItemView viewItem;
    private BalanceAssistantMessage contentMessage;

    protected ChatCellBalanceAssitant(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }


    @Override
    protected void initView() {
        super.initView();
        viewItem = getView().findViewById(R.id.view_balance_msg);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        contentMessage = message.getBalanceAssistantMessage();
        viewItem.bindData(contentMessage);
    }


    @Override
    public void onBubbleClick() {
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.BALANCE_ASSISTANT_CLICK, model, contentMessage);
        }
    }
}
