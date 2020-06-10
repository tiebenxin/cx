package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.AdMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.utils.ChatBitmapCache;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.StringUtil;


/**
 * 小助手推广（广告）消息
 */
public class ChatCellAd extends ChatCellBase {
    private TextView tvTitle;
    private ImageView ivImage;
    private TextView tvDesc;
    private AdMessage contentMessage;
    private Button btGo;

    protected ChatCellAd(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle = getView().findViewById(R.id.tv_title);
        ivImage = getView().findViewById(R.id.iv_img);
        tvDesc = getView().findViewById(R.id.tv_description);
        btGo = getView().findViewById(R.id.bt_go);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        contentMessage = message.getAdMessage();
        showContent(contentMessage);
    }

    private void showContent(AdMessage message) {
        if (message == null) {
            return;
        }
        setText(tvTitle, message.getTitle());
        setText(tvDesc, message.getSummary());
        if (StringUtil.isNotNull(message.getThumbnail())) {//url链接
            ivImage.setVisibility(View.VISIBLE);
            ivImage.setImageResource(R.mipmap.ic_image_bg);
            Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(message.getThumbnail());
            if (localBitmap == null) {
                RequestOptions mRequestOptions = RequestOptions.centerInsideTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .centerCrop();
                Glide.with(getContext())
                        .asBitmap()
                        .load(message.getThumbnail())
                        .apply(mRequestOptions)
                        .into(ivImage);
            } else {
                ivImage.setImageBitmap(localBitmap);
            }
        } else {
            ivImage.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(message.getButtonTxt())) {
            btGo.setVisibility(View.VISIBLE);
            btGo.setText(message.getButtonTxt());
        } else {
            btGo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBubbleClick() {
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.AD_CLICK, model, contentMessage);
        }
    }

    private void setText(TextView tv, String txt) {
        if (!TextUtils.isEmpty(txt)) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(txt);
        } else {
            tv.setVisibility(View.GONE);
        }
    }
}
