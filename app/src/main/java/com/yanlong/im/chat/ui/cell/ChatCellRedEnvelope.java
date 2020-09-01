package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.cxpay.global.PayEnum;
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
    private RedEnvelopeMessage contentMsg;
    private TextView tvVieMore;

    protected ChatCellRedEnvelope(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_rb_title = getView().findViewById(R.id.tv_rb_title);
        tv_rb_info = getView().findViewById(R.id.tv_rb_info);
        tv_rb_type = getView().findViewById(R.id.tv_rb_type);
        iv_rb_state = getView().findViewById(R.id.iv_rb_state);
        iv_rb_icon = getView().findViewById(R.id.iv_rb_icon);
        tvVieMore = getView().findViewById(R.id.tv_view_more);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        boolean isInvalid = false;
        String title = "";
        String info = "";
        String typeName = "";
        int typeIcon = R.color.transparent;
        if (message.getMsg_type() == ChatEnum.EMessageType.RED_ENVELOPE) {
            contentMsg = message.getRed_envelope();
            isInvalid = contentMsg.getIsInvalid() == 0 ? false : true;
            title = contentMsg.getComment();
            info = getEnvelopeInfo(contentMsg.getEnvelopStatus(), contentMsg.isHasPermission());
            if (contentMsg.getRe_type().intValue() == MsgBean.RedEnvelopeType.MFPAY_VALUE) {
                typeName = "云红包";
            } else {
                typeName = "零钱红包";
            }
        }
        setMessage(isInvalid, title, info, typeName, typeIcon);
        if (!isMe && !contentMsg.isHasPermission() && contentMsg.getCanReview() == 1) {
            tvVieMore.setVisibility(View.VISIBLE);
        } else {
            tvVieMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBubbleClick() {
        if (mCellListener != null) {
            if (messageType == ChatEnum.EMessageType.RED_ENVELOPE) {
                mCellListener.onEvent(ChatEnum.ECellEventType.RED_ENVELOPE_CLICK, model, contentMsg);
            }
        }
    }

    private void setMessage(boolean invalid, String title, String info, String typeName, int typeIcon) {
        if (invalid) {//失效
            if (!contentMsg.isHasPermission()) {
                iv_rb_state.setImageResource(R.mipmap.ic_rb_zfb_un);
            } else {
                iv_rb_state.setImageResource(R.mipmap.ic_rb_zfb_n);
            }
            bubbleLayout.setBackgroundResource(model.isMe() ? R.drawable.selector_rp_me_light : R.drawable.selector_rp_other_light);
        } else {
            iv_rb_state.setImageResource(R.mipmap.ic_rb_zfb_un);
            bubbleLayout.setBackgroundResource(model.isMe() ? R.drawable.selector_rp_me_deep : R.drawable.selector_rp_other_deep);
        }
        tv_rb_title.setText(title);
        tv_rb_info.setText(info);
        tv_rb_type.setText(typeName);
        iv_rb_icon.setImageResource(typeIcon);
    }

    private String getEnvelopeInfo(@PayEnum.EEnvelopeStatus int envelopStatus, boolean hasPermission) {
        String info = "";
        if (!hasPermission) {
            info = "权限限制，不可领取";
        } else {
            switch (envelopStatus) {
                case PayEnum.EEnvelopeStatus.NORMAL:
                case PayEnum.EEnvelopeStatus.ERROR:
                    info = "领取红包";
                    break;
                case PayEnum.EEnvelopeStatus.RECEIVED:
                    info = "已领取";
                    break;
                case PayEnum.EEnvelopeStatus.RECEIVED_FINISHED:
                    info = "已被领完";
                    break;
                case PayEnum.EEnvelopeStatus.PAST:
                    info = "已过期";
                    break;
            }
        }
        return info;
    }
}
