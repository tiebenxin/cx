package com.yanlong.im.chat.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;

import net.cb.cb.library.utils.DensityUtil;

/*
 * 图片消息
 * */
public class ChatCellImage extends ChatCellBase {
    //w/h = 3/4
    final int DEFAULT_W = DensityUtil.dip2px(getContext(), 135);
    final int DEFAULT_H = DensityUtil.dip2px(getContext(), 180);
    int width = DEFAULT_W;
    int height = DEFAULT_H;

    private ImageView imageView;
    private ImageMessage imageMessage;
    private ProgressBar progressBar;
    private TextView tv_progress;
    private LinearLayout ll_progress;

    protected ChatCellImage(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        super(context, cellLayout, listener, adapter, viewGroup);
    }

    @Override
    protected void initView() {
        super.initView();
        imageView = getView().findViewById(R.id.iv_img);
        ll_progress = getView().findViewById(R.id.ll_progress);
        progressBar = getView().findViewById(R.id.progress_bar);
        tv_progress = getView().findViewById(R.id.tv_progress);
//        imageView.setOnClickListener(this);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        imageMessage = message.getImage();
        if (imageMessage == null) {
            return;
        }
        String thumbnail = imageMessage.getThumbnailShow();
        resetSize();
        RequestOptions rOptions = new RequestOptions();
        rOptions.override(width, height);
        if (isGif(thumbnail)) {
            if (imageView.getTag() == null) {
                rOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(getContext())
                        .load(message.getImage().getPreview())
                        .apply(rOptions)
//                    .thumbnail(0.2f)
                        .into(imageView);
                imageView.setTag(currentPosition, message.getImage().getPreview());
            } else {
                String url = (String) imageView.getTag(currentPosition);
                if (url.equals(thumbnail)) {
                    rOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(getContext())
                            .load(message.getImage().getPreview())
                            .apply(rOptions)
//                    .thumbnail(0.2f)
                            .into(imageView);
                } else {
                    rOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(getContext())
                            .load(message.getImage().getPreview())
                            .apply(rOptions)
//                    .thumbnail(0.2f)
                            .into(imageView);
                    imageView.setTag(currentPosition, message.getImage().getPreview());
                }

            }
        } else {
            if (imageView.getTag() == null) {
                rOptions.centerCrop();
                rOptions.error(R.drawable.bg_btn_white);
                rOptions.placeholder(R.drawable.bg_btn_white);
                Glide.with(getContext())
                        .load(thumbnail)
                        .apply(rOptions)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                imageView.setImageDrawable(resource);
                            }
                        });
                imageView.setTag(currentPosition, thumbnail);
//            imageView.setTag(message.getImage().getPreview());
            } else {
                rOptions.centerCrop();
                rOptions.error(R.drawable.bg_btn_white);
                rOptions.placeholder(R.drawable.bg_btn_white);

                String url = (String) imageView.getTag(currentPosition);
                if (url.equals(thumbnail)) {
                    Glide.with(getContext())
                            .load(thumbnail)
                            .apply(rOptions)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    imageView.setImageDrawable(resource);
                                }
                            });
                } else {
                    Glide.with(getContext())
                            .load(thumbnail)
                            .apply(rOptions)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    imageView.setImageDrawable(resource);
                                }
                            });
                    imageView.setTag(currentPosition, thumbnail);

                }


            }
        }
    }


    private boolean isGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().endsWith(".gif")) {
                return true;
            }
        }
        return false;
    }

    private void resetSize() {
        int realW = (int) imageMessage.getWidth();
        int realH = (int) imageMessage.getHeight();
        if (realH > 0) {
            double scale = (realW * 1.00) / realH;
            if (realW > realH) {
//                width = getBitmapWidth();
                width = DEFAULT_W;
                height = (int) (width / scale);
            } else if (realW < realH) {
//                height = getBitmapHeight();
                height = DEFAULT_H;
                width = (int) (height * scale);
            } else {
                width = height = DEFAULT_H;
            }
        }
//        ViewGroup.LayoutParams lp = bubbleLayout.getLayoutParams();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.width = width;
        lp.height = height;
        imageView.setLayoutParams(lp);

        if (ll_progress != null) {
            ll_progress.setLayoutParams(lp);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == imageView.getId()) {
            if (mCellListener != null && model != null) {
                mCellListener.onEvent(ChatEnum.ECellEventType.IMAGE_CLICK, model, new Object());
            }
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.IMAGE_CLICK, model, new Object());
        }
    }

    public void updateProgress(@ChatEnum.ESendStatus int status, int progress) {
        if (ll_progress != null && progressBar != null && tv_progress != null) {
            if (status == ChatEnum.ESendStatus.SENDING && (progress > 0 && progress < 100)) {
                ll_progress.setVisibility(View.VISIBLE);
                tv_progress.setText(progress + "");
                setSendStatus();
            }
        } else {
            ll_progress.setVisibility(View.GONE);
        }
    }
}
