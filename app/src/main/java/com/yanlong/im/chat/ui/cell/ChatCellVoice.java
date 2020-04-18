package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VoiceMessage;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.LogUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/*
 * 语音
 * */
public class ChatCellVoice extends ChatCellBase {

    private VoiceMessage voiceMessage;
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
            updateUnread(voiceMessage.getPlayStatus());
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.VOICE_CLICK, model, voiceMessage, currentPosition);
        }
    }

    void updateVoice(boolean isPlay) {
        LogUtil.getLog().i(ChatCellVoice.class.getSimpleName(), "updateVoice");
        voiceMessage = model.getVoiceMessage();
        if (voiceMessage != null) {
            int playStatus = voiceMessage.getPlayStatus();
            updateUnread(playStatus);
            if (isPlay && playStatus == ChatEnum.EPlayStatus.PLAYING) {
                ((AnimationDrawable) ivVoice.getDrawable()).selectDrawable(2);
                ((AnimationDrawable) ivVoice.getDrawable()).start();
            } else {
                ((AnimationDrawable) ivVoice.getDrawable()).selectDrawable(0);
                ((AnimationDrawable) ivVoice.getDrawable()).stop();
            }
            if (!isMe && !model.isRead()) {
                setDownloadStatus(playStatus, model.isRead());
            }
        }
    }

    private void updateUnread(int playStatus) {
        if (ivStatus == null || isMe) {
            return;
        }
        ivStatus.setVisibility(playStatus != ChatEnum.EPlayStatus.NO_DOWNLOADED ? GONE : VISIBLE);

    }

    private void setDownloadStatus(int playStatus, boolean isRead) {
        switch (playStatus) {
            case ChatEnum.EPlayStatus.DOWNLOADING://正常
                if (!isRead) {
                    Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_circle_rotate);
                    ivStatus.startAnimation(rotateAnimation);
                    ivStatus.setVisibility(VISIBLE);
                }
                break;
            case ChatEnum.EPlayStatus.NO_PLAY:
                ivStatus.clearAnimation();
                ivStatus.setVisibility(GONE);
                break;
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
        LogUtil.getLog().i(ChatCellVoice.class.getSimpleName(), "updateBubbleWidth--len=" + len);
        if (len > 0) {
            int s = len > 60 ? 60 : len;
            int wsum = getScreenWidth() - DensityUtil.dip2px(getContext(), 74) * 2;
            float x = DensityUtil.dip2px(getContext(), 94);//viewOtP.getX();//原始值60
            int w = new Float((wsum - x) / 60 * (s)).intValue();
            ViewGroup.LayoutParams layoutParams = bubbleLayout.getLayoutParams();
            layoutParams.width = w + DensityUtil.dip2px(getContext(), 65);
            bubbleLayout.setMinimumWidth(w);
            LogUtil.getLog().i(ChatCellVoice.class.getSimpleName(), "updateBubbleWidth--width=" + w);
        }
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getOtherWidth() {
        int width = 0;
        if (ivVoice != null) {
            ivVoice.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ivVoice.getLayoutParams();
            width += ivVoice.getMaxWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
        }
        if (tvTime != null) {
            tvTime.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tvTime.getLayoutParams();
            width += tvTime.getMaxWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
        }
        LogUtil.getLog().i(ChatCellVoice.class.getSimpleName(), "updateBubbleWidth--other-width=" + width);
        return width;
    }
}
