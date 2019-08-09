package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.ui.view.VoiceView;
import com.yanlong.im.utils.audio.AudioPlayManager;

public class ChatCellVoice extends ChatCellBase {

    private VoiceView v_voice;
    private VoiceMessage voiceMessage;
    private Uri uri;

    protected ChatCellVoice(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        super(context, cellLayout, listener, adapter, viewGroup);
    }

    @Override
    protected void initView() {
        super.initView();
        v_voice = getView().findViewById(R.id.v_voice);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        voiceMessage = message.getVoiceMessage();
        if (voiceMessage != null) {
            uri = Uri.parse(voiceMessage.getUrl());
            v_voice.init(message.isMe(), voiceMessage.getTime(), message.isRead(), AudioPlayManager.getInstance().isPlay(uri));
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        playVoice();
    }

    private void playVoice() {
        if (uri == null) {
            return;
        }
        if (AudioPlayManager.getInstance().isPlay(uri)) {
            AudioPlayManager.getInstance().stopPlay();
        } else {

        }
    }
}
