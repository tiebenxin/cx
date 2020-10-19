package com.yanlong.im.chat.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.luck.picture.lib.glide.CustomGlideModule;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.utils.ChatBitmapCache;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;

import java.io.File;

import static android.view.View.VISIBLE;

/*
 * 图片消息
 * */
public class ChatCellImage extends ChatCellFileBase {
    //w/h = 3/4
    final int DEFAULT_W = DensityUtil.dip2px(getContext(), 120);
    final int DEFAULT_H = DensityUtil.dip2px(getContext(), 180);
    int width = DEFAULT_W;
    int height = DEFAULT_H;

    private ImageView imageView;
    private ImageMessage imageMessage;
    String currentUrl = "";

    protected ChatCellImage(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }


    @Override
    protected void initView() {
        super.initView();
        imageView = getView().findViewById(R.id.iv_img);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        imageMessage = message.getImage();
        if (imageMessage == null || !isActivityValid()) {
            return;
        }
        loadImage(message);
    }

    @SuppressLint("CheckResult")
    private void loadImage(MsgAllBean message) {
        String thumbnail = imageMessage.getThumbnailShow();
        resetSize();
        checkSendStatus();
        //获取圆角
        RequestOptions rOptions = new RequestOptions().centerCrop()/*.transform(new RoundTransform(mContext, 1))*/;
        rOptions.dontAnimate();
        rOptions.override(width, height);
        String tag = (String) imageView.getTag(R.id.tag_img);
        if (isGif(thumbnail)) {
            String gif = message.getImage().getPreview();
            if (!TextUtils.equals(tag, gif)) {
                imageView.setTag(R.id.tag_img, gif);
                rOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//                rOptions.priority(Priority.LOW);
//                rOptions.skipMemoryCache(true);
                imageView.setImageResource(R.mipmap.ic_image_bg);
                File local = CustomGlideModule.getCacheFile(gif);
                if (local == null) {
                    glideGif(rOptions, gif);
                } else {
                    glideGif(rOptions, local.getAbsolutePath());
                }
            } else {
                glideGif(rOptions, gif);
            }
        } else {
            rOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            rOptions.skipMemoryCache(false);
            if (!TextUtils.equals(tag, thumbnail)) {//第一次加载
                imageView.setImageResource(R.mipmap.ic_image_bg);
                imageView.setTag(R.id.tag_img, thumbnail);
                glide(rOptions, thumbnail);
            } else {//复用
                glide(rOptions, tag);
            }
        }
    }

    public void glide(RequestOptions rOptions, String url) {
        if (!TextUtils.isEmpty(currentUrl) && url.equals(currentUrl) && model.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
            return;
        }
        currentUrl = url;
        LogUtil.getLog().i(ChatCellImage.class.getSimpleName(), "--加载图片--url=" + url);
        Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(url);
        if (localBitmap == null) {
            Glide.with(getContext())
                    .asBitmap()
                    .load(url)
                    .apply(rOptions)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            currentUrl= "";
                            if (e.getMessage().contains("FileNotFoundException")) {
//                                imageView.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        imageView.setImageResource(R.mipmap.ic_img_past);
//                                    }
//                                },100);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            imageView.setImageBitmap(localBitmap);
        }

    }

    private void glideGif(RequestOptions rOptions, String url) {
        if (!TextUtils.isEmpty(currentUrl) && url.equals(currentUrl) && model.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
            return;
        }
        currentUrl = url;
        LogUtil.getLog().i(ChatCellImage.class.getSimpleName(), "--加载gif图片--url=" + url);
        rOptions.skipMemoryCache(false);
        rOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        //TODO:设置options后gif图片不动
        Glide.with(getContext())
                .load(url)
//                .apply(rOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        currentUrl= "";
//                        if (e.getMessage().contains("FileNotFoundException")) {
                        imageView.setImageResource(R.mipmap.ic_img_past);
//                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }


    public boolean isGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().contains(".gif")) {
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
                width = DEFAULT_W;
                height = (int) (width / scale);
            } else if (realW < realH) {
                height = DEFAULT_H;
                width = (int) (height * scale);
            } else {
                width = height = DEFAULT_W;
            }
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.width = width;
        lp.height = height;
        imageView.setLayoutParams(lp);

        if (ll_progress != null) {
            FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp2.width = width;
            lp2.height = height;
            ll_progress.setLayoutParams(lp2);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (imageView == null) {
            return;
        }
        if (view.getId() == imageView.getId()) {
            if (mCellListener != null && model != null) {
                mCellListener.onEvent(ChatEnum.ECellEventType.IMAGE_CLICK, model, new Object());
            }
        }
    }

    @Override
    public void onBubbleClick() {
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.IMAGE_CLICK, model, model.getImage());
        }
    }

    public void checkSendStatus() {
        if (ll_progress == null) {
            return;
        }
        setSendStatus(false);
        switch (model.getSend_state()) {
            case ChatEnum.ESendStatus.ERROR:
            case ChatEnum.ESendStatus.NORMAL:
                ll_progress.setVisibility(View.GONE);
                break;
            case ChatEnum.ESendStatus.PRE_SEND:
            case ChatEnum.ESendStatus.SENDING:
                ll_progress.setVisibility(VISIBLE);
                break;
        }
    }
}
