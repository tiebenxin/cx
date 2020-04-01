package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nim_lib.util.ScreenUtil;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VoiceMessage;

import net.cb.cb.library.utils.DensityUtil;

import static android.view.View.VISIBLE;

/*
 * 语音
 * */
public class ChatCellVoice extends ChatCellBase {

    private VoiceMessage voiceMessage;
    private Uri uri;
    private ImageView ivVoice;
    private TextView tvTime;
    private ImageView ivStatus;

    protected ChatCellVoice(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        ivVoice = getView().findViewById(R.id.iv_voice);
        tvTime = getView().findViewById(R.id.tv_voice_time);
        ivStatus = getView().findViewById(R.id.iv_voice_status);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        voiceMessage = message.getVoiceMessage();
        if (voiceMessage != null) {
            updateBubbleWidth(voiceMessage.getTime());
            initTime(voiceMessage.getTime());
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.VOICE_CLICK, model, voiceMessage, currentPosition);
        }
    }

    void updateVoice() {
        voiceMessage = model.getVoiceMessage();
        if (voiceMessage != null) {
            uri = Uri.parse(voiceMessage.getUrl());
        }
    }

    private void initTime(int len) {
        if (len > 0) {
            tvTime.setVisibility(VISIBLE);
            tvTime.setText(len + "''");
        }
    }


    private void updateBubbleWidth(int len) {
        if (bubbleLayout == null) {
            return;
        }
        if (len > 0) {
            int s = len > 60 ? 60 : len;
            int wsum = getScreenWidth() - DensityUtil.dip2px(getContext(), 74) * 2;//-DensityUtil.dip2px(getContext(),35);
            float x = DensityUtil.dip2px(getContext(), 94);//viewOtP.getX();//原始值60
            int w = new Float((wsum - x) / 60 * (s)).intValue();
            bubbleLayout.setMinimumWidth(w);
        }
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
