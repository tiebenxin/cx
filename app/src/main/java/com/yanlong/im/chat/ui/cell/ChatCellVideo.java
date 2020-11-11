package com.yanlong.im.chat.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.ui.RoundTransform;
import com.yanlong.im.utils.ChatBitmapCache;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.LogUtil;

import java.util.Locale;

import static android.view.View.VISIBLE;

public class ChatCellVideo extends ChatCellImage {
    final int DEFAULT_W = DensityUtil.dip2px(getContext(), 120);
    final int DEFAULT_H = DensityUtil.dip2px(getContext(), 180);
    int width = DEFAULT_W;
    int height = DEFAULT_H;

    private ImageView ivBg;
    private ImageView ivPlay;
    private TextView tv_video_time;//视频时长
    private VideoMessage videoMessage;
    private RequestOptions rOptions;
    private Bitmap localBitmap;

    protected ChatCellVideo(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        ivBg = getView().findViewById(R.id.iv_img);
        ivPlay = getView().findViewById(R.id.iv_play);
        tv_video_time = getView().findViewById(R.id.tv_video_time);
//        CardView cardView = getView().findViewById(R.id.card_view);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        videoMessage = message.getVideoMessage();
        loadImage(message);
        checkSendStatus();
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

    @SuppressLint("CheckResult")
    private void loadImage(MsgAllBean message) {
        VideoMessage video = message.getVideoMessage();
        if (video == null || ivBg == null) {
            return;
        }
        resetSize();
        String url = video.getBg_url();
        rOptions = new RequestOptions().centerCrop()/*.transform(new RoundTransform(mContext, 10))*/;
        if (width > 0 && height > 0) {
            rOptions.override(width, height);
        }
        rOptions.dontAnimate();
        String tag = (String) ivBg.getTag(R.id.tag_img);
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


    private void resetSize() {
        int realW = (int) videoMessage.getWidth();
        int realH = (int) videoMessage.getHeight();
        if (realH > 0) {
            double scale = (realW * 1.00) / realH;
            if (realW > realH) {
                width = DEFAULT_W;
                height = (int) (width / scale);
            } else if (realW < realH) {
                height = DEFAULT_H;
                width = (int) (height * scale);
            } else {
                width = height = DEFAULT_H;
            }
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.width = width;
        lp.height = height;
        ivBg.setLayoutParams(lp);

        if (ll_progress != null) {
            FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp2.width = width;
            lp2.height = height;
            ll_progress.setLayoutParams(lp2);
        }
    }

    @Override
    public void onBubbleClick() {
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.VIDEO_CLICK, model);
        }
    }

    @Override
    public void glide(RequestOptions rOptions, String url) {
        LogUtil.getLog().i("ChatCellVideo", "glide--url=" + url + "--width=" + width + "--height=" + height);
        localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(url);
        if (localBitmap == null) {
            Glide.with(getContext())
                    .asBitmap()
                    .load(url)
                    .apply(rOptions)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            if (e.getMessage().contains("FileNotFoundException")) {
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivBg);
        } else {
            ivBg.setImageBitmap(localBitmap);
        }
        ivBg.setVisibility(VISIBLE);

    }

    @Override
    public void recycler() {
        LogUtil.getLog().i("图片", "recycler");
        try {
//            Glide.with(getContext()).clear(ivBg);
            if (localBitmap != null) {
//                localBitmap.recycle();
                localBitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
