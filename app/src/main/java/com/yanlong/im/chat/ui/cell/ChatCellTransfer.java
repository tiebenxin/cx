package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.utils.UIUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.utils.socket.MsgBean;

/*
 * 转账消息
 * */
public class ChatCellTransfer extends ChatCellBase {

    private TextView tv_rb_title, tv_rb_info, tv_rb_type;
    private ImageView iv_rb_state, iv_rb_icon;
    private RedEnvelopeMessage redEnvelopeMessage;
    private TransferMessage transfer;

    protected ChatCellTransfer(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
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
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        String title = "";
        String info = "";
        String typeName = "";
        int typeIcon = R.color.transparent;
        transfer = message.getTransfer();
        title = "¥" + UIUtils.getYuan(transfer.getTransaction_amount());
        info = getTransferInfo(transfer.getComment(), transfer.getOpType(), model.isMe(), model.getTo_user().getName());
        typeName = "零钱转账";
        setMessage(transfer.getOpType(), title, info, typeName, typeIcon);
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.TRANSFER_CLICK, model, transfer);
        }
    }

    private void setMessage(int transferStatus, String title, String info, String typeName, int typeIcon) {
        switch (transferStatus) {
            case PayEnum.ETransferOpType.TRANS_SEND:
                iv_rb_state.setImageResource(R.mipmap.ic_transfer_rb);
                bubbleLayout.setBackgroundResource(isMe ? R.drawable.bg_chat_me_rp : R.drawable.bg_chat_other_rp);
                break;
            case PayEnum.ETransferOpType.TRANS_RECEIVE:
                iv_rb_state.setImageResource(R.mipmap.ic_transfer_receive_rb);
                bubbleLayout.setBackgroundResource(isMe ? R.drawable.bg_chat_me_rp_h : R.drawable.bg_chat_other_rp_h);
                break;
            case PayEnum.ETransferOpType.TRANS_REJECT:
            case PayEnum.ETransferOpType.TRANS_PAST:
                iv_rb_state.setImageResource(R.mipmap.ic_transfer_return_rb);
                bubbleLayout.setBackgroundResource(isMe ? R.drawable.bg_chat_me_rp_h : R.drawable.bg_chat_other_rp_h);
                break;
        }
        tv_rb_title.setText(title);
        tv_rb_info.setText(info);
        tv_rb_type.setText(typeName);
        iv_rb_icon.setImageResource(typeIcon);
    }

    public String getTransferInfo(String info, int opType, boolean isMe, String nick) {
        String result = "";
        if (opType == PayEnum.ETransferOpType.TRANS_SEND) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "转账给" + nick;
                } else {
                    result = "转账给你";
                }
            } else {
                result = info;

            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_RECEIVE) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已收款";
                } else {
                    result = "已被领取";
                }
            } else {
                if (isMe) {
                    result = "已收款-" + info;
                } else {
                    result = "已被领取-" + info;
                }
            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_REJECT) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已退款";
                } else {
                    result = "已被退款";
                }
            } else {
                if (isMe) {
                    result = "已退款-" + info;
                } else {
                    result = "已被退款-" + info;
                }
            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_PAST) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已过期";
                } else {
                    result = "已过期";
                }
            } else {
                if (isMe) {
                    result = "已过期-" + info;
                } else {
                    result = "已过期-" + info;
                }
            }
        }
        return result;
    }

}