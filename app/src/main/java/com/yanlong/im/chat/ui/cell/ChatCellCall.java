package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.tools.StringUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.P2PAuVideoMessage;
import com.yanlong.im.utils.socket.MsgBean;

/*
 * 音视频通话消息
 * */
public class ChatCellCall extends ChatCellBase {

    private TextView tv_content;
    private P2PAuVideoMessage contentMessage;

    protected ChatCellCall(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }


    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_content);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        contentMessage = message.getP2PAuVideoMessage();
        if (contentMessage != null) {
            tv_content.setText(contentMessage.getDesc());
            initIcon(contentMessage.getAv_type());
        }
    }

    private void initIcon(int type) {
        if (getContext() == null) {
            return;
        }
        Drawable drawableVoice = getContext().getResources().getDrawable(R.drawable.svg_small_voice2);
        Drawable drawableVideo = getContext().getResources().getDrawable(R.drawable.svg_small_video2);
        if (type == MsgBean.AuVideoType.Audio.getNumber()) {
            if (isMe) {
                StringUtils.modifyTextViewDrawable(tv_content, drawableVoice, 2);
            } else {
                StringUtils.modifyTextViewDrawable(tv_content, drawableVoice, 0);
            }
        } else {
            if (isMe) {
                StringUtils.modifyTextViewDrawable(tv_content, drawableVideo, 2);
            } else {
                StringUtils.modifyTextViewDrawable(tv_content, drawableVideo, 0);
            }
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.VOICE_VIDEO_CALL, model, contentMessage);
        }
    }
}
