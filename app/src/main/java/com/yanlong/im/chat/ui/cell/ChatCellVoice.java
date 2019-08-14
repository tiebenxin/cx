package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.ui.view.VoiceView;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.IAudioPlayListener;

/*
 * 语音
 * */
public class ChatCellVoice extends ChatCellBase {

    private VoiceView v_voice;
    private VoiceMessage voiceMessage;
    private Uri uri;
    private Handler handler = new Handler(Looper.getMainLooper());

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
            AudioPlayManager.getInstance().startPlay(mContext, uri, new IAudioPlayListener() {
                @Override
                public void onStart(Uri var1) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v_voice.init(model.isMe(), voiceMessage.getTime(), model.isRead(), AudioPlayManager.getInstance().isPlay(uri));

                        }
                    }, 100);
                }


                @Override
                public void onStop(Uri var1) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v_voice.init(model.isMe(), voiceMessage.getTime(), model.isRead(), AudioPlayManager.getInstance().isPlay(uri));
                        }
                    }, 100);
                }

                @Override
                public void onComplete(Uri var1) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v_voice.init(model.isMe(), voiceMessage.getTime(), model.isRead(), AudioPlayManager.getInstance().isPlay(uri));
                        }

                    }, 100);
                }
            });
        }
        updateRead();

    }

    private void updateRead() {
        //设置为已读
        if (model.isRead() == false) {
            MsgAction action = new MsgAction();
            action.msgRead(model.getMsg_id(), true);
            model.setRead(true);
            v_voice.init(model.isMe(), voiceMessage.getTime(), model.isRead(), AudioPlayManager.getInstance().isPlay(uri));
        }
    }
}