package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.utils.HtmlTransitonUtils;

/*
 * 通知消息, 撤回消息
 * */
public class ChatCellLock extends ChatCellBase {

    private TextView tv_content;

    protected ChatCellLock(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_lock);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        if (messageType == ChatEnum.EMessageType.LOCK) {
            tv_content.setText(new HtmlTransitonUtils().getSpannableString(mContext, message.getChat().getMsg(), ChatEnum.ENoticeType.LOCK, actionTagClickListener));
            tv_content.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
