package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.QuotedMessage;
import com.yanlong.im.chat.bean.ReplyMessage;
import com.yanlong.im.utils.ExpressionUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ScreenUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;

/*
 * 回复文本消息
 * */
public class ChatCellReplyText extends ChatCellBase {

    private TextView tv_content, tvRefName, tvRefMsg;
    private ReplyMessage contentMessage;

    protected ChatCellReplyText(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_content);
        tvRefName = getView().findViewById(R.id.tv_ref_name);
        tvRefMsg = getView().findViewById(R.id.tv_ref_msg);
        //设置自定义文字大小
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        if (fontSize != null) {
            tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        updateWidth();
        contentMessage = message.getReplyMessage();
        QuotedMessage quotedMessage = contentMessage.getQuotedMessage();
        tvRefName.setText(quotedMessage.getNickName());
        tvRefMsg.setText(getSpan(quotedMessage.getMsg()));
        String content = "";
        if (contentMessage.getChatMessage() != null) {
            content = contentMessage.getChatMessage().getMsg();
        } else if (contentMessage.getAtMessage() != null) {
            content = contentMessage.getAtMessage().getMsg();
        }
        tv_content.setText(getSpan(content));
    }

    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(getContext(), fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }

    private void updateWidth() {
        int width = ScreenUtils.getScreenWidth(getContext());
        double maxWidth = 0.6 * width;
        if (maxWidth > 0 && tv_content != null) {
            tv_content.setMaxWidth((int) maxWidth);
            LogUtil.getLog().i("ChatCellText", "");
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null && model != null){
            mCellListener.onEvent(ChatEnum.ECellEventType.REPLY_CLICK,model);
        }
    }
}
