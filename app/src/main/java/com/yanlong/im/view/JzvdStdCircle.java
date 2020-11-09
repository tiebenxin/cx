package com.yanlong.im.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.nim_lib.controll.AVChatProfile;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.AttachmentBean;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;

import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-10-30
 * @updateAuthor
 * @updateDate
 * @description 广场视频播放器
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class JzvdStdCircle extends JzvdStd {

    private String videoUrl, bgUrl;
    private AttachmentBean attachmentBean;

    public JzvdStdCircle(Context context) {
        super(context);
    }

    public JzvdStdCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == cn.jzvd.R.id.surface_container
                || v.getId() == cn.jzvd.R.id.poster || v.getId() == cn.jzvd.R.id.start) {
            // 进入播放视频界面
            gotoVideoPlay();
        }
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        // 自动播放完后释放内容
        releaseAllVideos();
    }

    @Override

    public void onPrepared() {
        try {
            super.onPrepared();
            // 设置wifi切4g不弹提示框
            WIFI_TIP_DIALOG_SHOWED = true;
            // 隐藏底部进度条
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            bottomContainer.setLayoutParams(layoutParams);
            mediaInterface.setVolume(0f, 0f);
        } catch (Exception e) {

        }
    }

    private void gotoVideoPlay() {
        // 判断是否正在音视频通话
        if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
            if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                ToastUtil.show(getContext(), getContext().getString(R.string.avchat_peer_busy_video));
            } else {
                ToastUtil.show(getContext(), getContext().getString(R.string.avchat_peer_busy_voice));
            }
        } else {
            AudioPlayUtil.stopAudioPlay();
            Intent intent = new Intent(getContext(), VideoPlayActivity.class);
            if (!TextUtils.isEmpty(videoUrl)) {
                intent.putExtra("json", GsonUtils.optObject(attachmentBean));
                intent.putExtra("bg_url", bgUrl);
                intent.putExtra("videopath", videoUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("from", PictureConfig.FROM_CIRCLE);
                getContext().startActivity(intent);
            }
        }
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public AttachmentBean getAttachmentBean() {
        return attachmentBean;
    }

    public void setAttachmentBean(AttachmentBean attachmentBean) {
        this.attachmentBean = attachmentBean;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
