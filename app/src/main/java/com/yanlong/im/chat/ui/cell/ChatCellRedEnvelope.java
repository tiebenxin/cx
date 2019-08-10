package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.utils.socket.MsgBean;

/*
 * 红包消息
 * */
public class ChatCellRedEnvelope extends ChatCellBase {

    private TextView tv_rb_title, tv_rb_info, tv_rb_type;
    private ImageView iv_rb_state, iv_rb_icon;
    private RedEnvelopeMessage redEnvelopeMessage;

    protected ChatCellRedEnvelope(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        super(context, cellLayout, listener, adapter, viewGroup);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_rb_title = getView().findViewById(R.id.tv_rb_title);
        tv_rb_info = getView().findViewById(R.id.tv_rb_info);
        tv_rb_type = getView().findViewById(R.id.tv_rb_type);
        iv_rb_state = getView().findViewById(R.id.iv_rb_state);
        iv_rb_icon = getView().findViewById(R.id.iv_rb_icon);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        redEnvelopeMessage = message.getRed_envelope();
        if (redEnvelopeMessage.isValid()) {//失效
            iv_rb_state.setImageResource(R.mipmap.ic_rb_zfb_n);
            if (message.isMe()) {
                bubbleLayout.setBackgroundResource(R.drawable.bg_chat_me_rp_h);
            } else {
                bubbleLayout.setBackgroundResource(R.drawable.bg_chat_other_rp_h);
            }
            tv_rb_info.setText("已领取");
        } else {
            iv_rb_state.setImageResource(R.mipmap.ic_rb_zfb_un);
            if (message.isMe()) {
                bubbleLayout.setBackgroundResource(R.drawable.bg_chat_me_rp);
            } else {
                bubbleLayout.setBackgroundResource(R.drawable.bg_chat_other_rp);
            }
            tv_rb_info.setText("领取红包");
        }
        tv_rb_title.setText(redEnvelopeMessage.getComment());

        if (redEnvelopeMessage.getRe_type().intValue() == MsgBean.RedEnvelopeMessage.RedEnvelopeType.MFPAY_VALUE) {
            tv_rb_type.setText("云红包");
        } else {
            tv_rb_type.setText("支付宝");
        }
        iv_rb_icon.setImageResource(R.color.transparent);
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
    }
}
