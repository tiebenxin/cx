package com.yanlong.im.chat.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.ui.RoundTransform;
import com.yanlong.im.view.face.FaceView;

import net.cb.cb.library.utils.DensityUtil;

import static android.view.View.VISIBLE;

/*
 * 表情消息
 * */
public class ChatCellExpress extends ChatCellFileBase {
    //w/h = 3/4
    final int DEFAULT_W = DensityUtil.dip2px(getContext(), 80);
    final int DEFAULT_H = DensityUtil.dip2px(getContext(), 80);
    int width = DEFAULT_W;
    int height = DEFAULT_H;

    private ImageView imageView;
    private ShippedExpressionMessage contentMessage;
    private final RequestOptions options;
    private String uri;

    protected ChatCellExpress(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
        options = new RequestOptions().centerCrop().transform(new RoundTransform(mContext, 10));
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
        contentMessage = message.getShippedExpressionMessage();
        if (contentMessage == null) {
            return;
        }
        checkSendStatus();
        resetSize();
        if (FaceView.map_FaceEmoji != null && FaceView.map_FaceEmoji.get(contentMessage.getId()) != null) {
            uri = FaceView.map_FaceEmoji.get(contentMessage.getId()).toString();
            imageView.setVisibility(VISIBLE);
            glide(options, Integer.parseInt(FaceView.map_FaceEmoji.get(contentMessage.getId()).toString()));
        } else {
            imageView.setVisibility(View.GONE);
        }
    }


    public void glide(RequestOptions rOptions, String url) {
        Glide.with(getContext())
                .load(url)
                .apply(rOptions)
                .thumbnail(0.2f)
                .into(imageView);
    }

    public void glide(RequestOptions rOptions, int id) {
        Glide.with(getContext())
                .load(id)
                .apply(rOptions)
//                    .thumbnail(0.2f)
                .into(imageView);
    }


    private boolean isGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().endsWith(".gif")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == imageView.getId()) {
            if (mCellListener != null && model != null && !TextUtils.isEmpty(uri)) {
                mCellListener.onEvent(ChatEnum.ECellEventType.EXPRESS_CLICK, model, uri);
            }
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null && model != null && !TextUtils.isEmpty(uri)) {
            mCellListener.onEvent(ChatEnum.ECellEventType.EXPRESS_CLICK, model, uri);
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
            case ChatEnum.ESendStatus.PRE_SEND:
                ll_progress.setVisibility(View.GONE);
                break;
            case ChatEnum.ESendStatus.SENDING:
                ll_progress.setVisibility(VISIBLE);
                break;

        }
    }

    private void resetSize() {
        int realW = width;
        int realH = height;
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
}
