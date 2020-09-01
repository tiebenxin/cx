package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.QuotedMessage;
import com.yanlong.im.chat.bean.ReplyMessage;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.utils.ChatBitmapCache;
import com.yanlong.im.utils.ExpressionUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;

/*
 * 回复图片消息
 * */
public class ChatCellReplyImage extends ChatCellImage {

    private TextView tv_content, tvRefName, tvRefContent;
    private ReplyMessage contentMessage;
    private ImageView ivImage;

    protected ChatCellReplyImage(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_content);
        tvRefName = getView().findViewById(R.id.tv_ref_name);
        tvRefContent = getView().findViewById(R.id.tv_ref_content);
        ivImage = getView().findViewById(R.id.iv_ref_image);
        //设置自定义文字大小
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        if (fontSize != null) {
            tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
//        updateWidth();
        contentMessage = message.getReplyMessage();
        QuotedMessage quotedMessage = contentMessage.getQuotedMessage();
        tvRefName.setText(quotedMessage.getNickName());
        if (quotedMessage.getMsgType() == ChatEnum.EMessageType.IMAGE || quotedMessage.getMsgType() == ChatEnum.EMessageType.SHIPPED_EXPRESSION
                || quotedMessage.getMsgType() == ChatEnum.EMessageType.BUSINESS_CARD) {
            glide(quotedMessage.getUrl());
        } else if (quotedMessage.getMsgType() == ChatEnum.EMessageType.VOICE) {
            ivImage.setImageResource(R.mipmap.ic_reply_voice);
        } else if (quotedMessage.getMsgType() == ChatEnum.EMessageType.MSG_VIDEO) {
            glide(quotedMessage.getUrl());
        } else if (quotedMessage.getMsgType() == ChatEnum.EMessageType.FILE) {
            ivImage.setImageResource(MessageManager.getInstance().getFileIconRid(quotedMessage.getUrl()));
        }
        tvRefContent.setText(getRefText(quotedMessage.getMsgType(), quotedMessage));
        String content = "";
        if (contentMessage.getChatMessage() != null) {
            content = contentMessage.getChatMessage().getMsg();
        } else if (contentMessage.getAtMessage() != null) {
            content = contentMessage.getAtMessage().getMsg();
        }
        tv_content.setText(getSpan(content));
    }

    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(getContext(), fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }

    private void updateWidth() {
        int width = ScreenUtil.getScreenWidth(getContext());
        double maxWidth = 0.6 * width;
        if (maxWidth > 0 && tv_content != null) {
            tv_content.setMaxWidth((int) maxWidth);
            LogUtil.getLog().i("ChatCellText", "");
        }
    }

    public void glide(String url) {
//        LogUtil.getLog().i(ChatCellImage.class.getSimpleName(), "--加载图片--url=" + url);
        Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(url);
        RequestOptions mRequestOptions = RequestOptions.centerInsideTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .skipMemoryCache(false)
                .centerCrop();
        if (isGif(url)) {
            Glide.with(getContext())
                    .load(url)
                    .apply(mRequestOptions)
                    .into(ivImage);
        } else {
            if (localBitmap == null) {
                Glide.with(getContext())
                        .asBitmap()
                        .load(url)
                        .apply(mRequestOptions)
                        .into(ivImage);
            } else {
                ivImage.setImageBitmap(localBitmap);
            }
        }
    }

    @Override
    public void onBubbleClick() {
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.REPLY_CLICK, model, contentMessage.getQuotedMessage());
        }
    }

    private String getRefText(@ChatEnum.EMessageType int msgType, QuotedMessage message) {
        String content = "";
        switch (msgType) {
            case ChatEnum.EMessageType.IMAGE:
                content = "图片";
                break;
            case ChatEnum.EMessageType.VOICE:
                content = "语音";
                break;
            case ChatEnum.EMessageType.MSG_VIDEO:
                content = "视频";
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD:
                content = message.getMsg();
                break;
            case ChatEnum.EMessageType.FILE:
                content = message.getMsg();
                break;
        }
        return content;
    }
}
