package com.yanlong.im.chat.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;

import net.cb.cb.library.utils.DensityUtil;

public class ChatCellImage extends ChatCellBase {
    final int DEFAULT_W = DensityUtil.dip2px(getContext(), 150);
    final int DEFAULT_H = DensityUtil.dip2px(getContext(), 180);

    private ImageView imageView;
    private ImageMessage imageMessage;

    protected ChatCellImage(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        super(context, cellLayout, listener, adapter, viewGroup);
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
        if (imageMessage == null) {
            return;
        }
        String thumbnail = imageMessage.getThumbnailShow();
        RequestOptions rOptions = new RequestOptions();
        rOptions.override((int) imageMessage.getWidth(), (int) imageMessage.getHeight());
        if (isGif(thumbnail)) {
            rOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(getContext())
                    .load(message.getImage().getPreview())
                    .apply(rOptions)
                    .into(imageView);
        } else {
            rOptions.centerCrop();
            Glide.with(getContext())
                    .load(message.getImage().getPreview())
                    .apply(rOptions)
                    .into(imageView);
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
}
