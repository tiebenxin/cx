package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;

/*
* 通知消息
* */
public class ChatCellNotice extends ChatCellBase {

    private TextView tv_content;

    protected ChatCellNotice(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        super(context, cellLayout, listener, adapter, viewGroup);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_broadcast);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        tv_content.setText(Html.fromHtml(message.getMsgNotice().getNote()));
    }
}
