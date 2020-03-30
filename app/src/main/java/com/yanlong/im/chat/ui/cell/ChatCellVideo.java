package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.ui.RoundTransform;

import net.cb.cb.library.utils.DensityUtil;

import java.util.Locale;

import static android.view.View.VISIBLE;

public class ChatCellVideo extends ChatCellImage {
    final int DEFAULT_W = DensityUtil.dip2px(getContext(), 120);
    final int DEFAULT_H = DensityUtil.dip2px(getContext(), 180);
    int width = DEFAULT_W;
    int height = DEFAULT_H;

    private ImageView ivBg;
//    private LinearLayout ll_progress;
//    private ProgressBar progressBar;
//    private TextView tv_progress;
    private ImageView ivPlay;
    private TextView tv_video_time;//视频时长
    private VideoMessage videoMessage;

    protected ChatCellVideo(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        ivBg = getView().findViewById(R.id.iv_img);
//        ll_progress = getView().findViewById(R.id.ll_progress);
//        progressBar = getView().findViewById(R.id.progress_bar);
//        tv_progress = getView().findViewById(R.id.tv_progress);
        ivPlay = getView().findViewById(R.id.iv_play);
        tv_video_time = getView().findViewById(R.id.tv_video_time);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        videoMessage = message.getVideoMessage();
        loadImage(message);
    }

    @Override
    public void checkSendStatus() {
        if (ll_progress == null) {
            return;
        }
        setSendStatus(false);
        switch (model.getSend_state()) {
            case ChatEnum.ESendStatus.ERROR:
            case ChatEnum.ESendStatus.NORMAL:
                ll_progress.setVisibility(View.GONE);
                showPlayUI(true);
                break;
            case ChatEnum.ESendStatus.PRE_SEND:
            case ChatEnum.ESendStatus.SENDING:
                ll_progress.setVisibility(VISIBLE);
                showPlayUI(false);
                break;

        }
    }

    private void showPlayUI(boolean b) {
        if (videoMessage == null || ivPlay == null || tv_video_time == null) {
            return;
        }
        if (b) {
            ivPlay.setVisibility(VISIBLE);
            tv_video_time.setVisibility(VISIBLE);
            tv_video_time.setText(getTime(videoMessage.getDuration()));
        } else {
            ivPlay.setVisibility(View.GONE);
            tv_video_time.setVisibility(View.GONE);
        }
    }

    private void loadImage(MsgAllBean message) {
        VideoMessage video = message.getVideoMessage();
        if (video == null || ivBg == null) {
            return;
        }
        String url = video.getBg_url();
        RequestOptions rOptions = new RequestOptions().centerCrop().transform(new RoundTransform(mContext, 10));
        rOptions.override(width, height);
        String tag = (String) ivBg.getTag(R.id.tag_img);
        rOptions.error(R.mipmap.default_image);
        rOptions.placeholder(R.mipmap.default_image);
        if (TextUtils.isEmpty(tag) || !TextUtils.equals(tag, url)) {
            ivBg.setTag(R.id.tag_img, url);
            glide(rOptions, url);
        } else {
            glide(rOptions, tag);
        }
    }

    private String getTime(long time) {
        long currentTime = time;
        // 转成秒
        currentTime = currentTime / 1000;
        int mHour = (int) currentTime / 3600;
        int mMin = (int) currentTime % 3600 / 60;
        int mSecond = (int) currentTime % 60;
        if (mHour > 0) {
            return String.format(Locale.CHINESE, "%02d:%02d:%02d", mHour, mMin, mSecond);
        } else {
            return String.format(Locale.CHINESE, "%02d:%02d", mMin, mSecond);
        }
    }

}
