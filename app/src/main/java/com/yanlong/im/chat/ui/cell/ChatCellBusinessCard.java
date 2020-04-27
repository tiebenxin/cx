package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.utils.ChatBitmapCache;

public class ChatCellBusinessCard extends ChatCellBase {

    private TextView tv_title;
    private ImageView iv_avatar_card;
    private TextView tv_info;
    private BusinessCardMessage cardMessage;

    protected ChatCellBusinessCard(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

//    protected ChatCellBusinessCard(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
//        super(context, cellLayout, listener, adapter, viewGroup);
//    }

    @Override
    protected void initView() {
        super.initView();
        tv_title = getView().findViewById(R.id.tv_title);
        iv_avatar_card = getView().findViewById(R.id.iv_avatar_card);
        tv_info = getView().findViewById(R.id.tv_info);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        cardMessage = message.getBusiness_card();
        showCard(cardMessage);
    }

    private void showCard(BusinessCardMessage cardMessage) {
        if (cardMessage == null) {
            return;
        }
        loadCardAvatar();
        tv_title.setText(cardMessage.getNickname());
        tv_info.setText(cardMessage.getComment());
    }

    /*
     * 加载发送者头像
     * */
    private void loadCardAvatar() {
        if (mContext == null || iv_avatar_card == null) {
            return;
        }
        Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(cardMessage.getAvatar());
        if (localBitmap == null) {
            RequestOptions mRequestOptions = RequestOptions.centerInsideTransform()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .centerCrop();
            Glide.with(getContext())
                    .asBitmap()
                    .load(cardMessage.getAvatar())
                    .apply(mRequestOptions)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            iv_avatar_card.setImageBitmap(resource);
                        }
                    });
        } else {
            iv_avatar_card.setImageBitmap(localBitmap);
        }

    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.CARD_CLICK, model, cardMessage);
        }
    }
}
