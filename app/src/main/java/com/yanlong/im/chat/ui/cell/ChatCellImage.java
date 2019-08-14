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

    protected ChatCellImage(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        super(context, cellLayout, listener, adapter, viewGroup);
    }

    @Override
    protected void initView() {
        super.initView();
        imageView = getView().findViewById(R.id.iv_img);
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
            rOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(getContext())
                    .load(message.getImage().getPreview())
                    .apply(rOptions)
//                    .thumbnail(0.2f)
                    .into(imageView);
        } else {
            rOptions.centerCrop();
            Glide.with(getContext())
                    .load(message.getImage().getPreview())
                    .apply(rOptions)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                        }
                    });
        }


    }

//    private void resetSize() {
//        double realW = (int) imageMessage.getWidth();
//        double realH = (int) imageMessage.getHeight();
//        if (realH > 0 && realW > 0) {
//            double scale = 1;
//            if (realW > realH) {
//                scale = DEFAULT_W / realW;
//            } else if (realW < realH) {
//                scale = DEFAULT_H / realH;
//            } else {
//                scale = 1;
//            }
//            width = (int) (realW * scale);
//            height = (int) (realH * scale);
//        }
//    }

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
        ViewGroup.LayoutParams lp = bubbleLayout.getLayoutParams();
        lp.width = width;
        lp.height = height;
        imageView.setLayoutParams(lp);

    }

    public int getBitmapWidth() {
        return getScreenWidth(getContext()) / 2;
    }

    public int getBitmapHeight() {
        return getScreenHeight(getContext()) / 4;
    }

    // 获取屏幕的宽度
    @SuppressWarnings("deprecation")
    public int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    // 获取屏幕的高度
    @SuppressWarnings("deprecation")
    public int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
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
}
