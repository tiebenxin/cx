package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;

public class ChatCellImage extends ChatCellBase {

    private ImageView imageView;

    protected ChatCellImage(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        super(context, cellLayout, listener, adapter,viewGroup);
    }

    @Override
    protected void initView() {
        super.initView();
        imageView = getView().findViewById(R.id.iv_img);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        Glide.with(getContext())
                .load(message.getImage().getOrigin())
                .into(imageView);
    }
}
