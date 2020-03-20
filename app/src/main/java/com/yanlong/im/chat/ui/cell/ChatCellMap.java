package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.StringUtil;

public class ChatCellMap extends ChatCellBase {

    private TextView tvLocation;
    private ImageView ivLocation;
    private TextView tvLocationDesc;
    private LocationMessage contentMessage;

    protected ChatCellMap(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tvLocation = getView().findViewById(R.id.tv_location);
        ivLocation = getView().findViewById(R.id.iv_location_bg);
        tvLocationDesc = getView().findViewById(R.id.tv_location_desc);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        contentMessage = message.getLocationMessage();
        showContent(contentMessage);
    }

    private void showContent(LocationMessage message) {
        if (message == null) {
            return;
        }
        tvLocation.setText(message.getAddress());
        tvLocationDesc.setText(message.getAddressDescribe());
        if (StringUtil.isNotNull(message.getImg())) {
            Glide.with(mContext).load(message.getImg()).apply(GlideOptionsUtil.imageOptions()).into(ivLocation);
        } else {
            String baiduImageUrl = LocationUtils.getLocationUrl(message.getLatitude(), message.getLongitude());
            Glide.with(mContext).load(baiduImageUrl).apply(GlideOptionsUtil.imageOptions()).into(ivLocation);
        }
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.MAP_CLICK, model, contentMessage);
        }
    }
}
