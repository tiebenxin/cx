package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.utils.ChatBitmapCache;

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
        ivLocation.setImageResource(R.mipmap.ic_image_bg);
        if (StringUtil.isNotNull(message.getImg())) {//url链接
            Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(message.getImg());
            if(localBitmap==null){
                RequestOptions mRequestOptions = RequestOptions.centerInsideTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .centerCrop();
                Glide.with(getContext())
                        .asBitmap()
                        .load(message.getImg())
                        .apply(mRequestOptions)
                        .into(ivLocation);
            }else{
                ivLocation.setImageBitmap(localBitmap);
            }
        } else {
            String baiduImageUrl = LocationUtils.getLocationUrl(message.getLatitude(), message.getLongitude());
            Glide.with(mContext)
                    .asBitmap()
                    .load(baiduImageUrl)
//                    .apply(GlideOptionsUtil.imageOptions())
                    .into(ivLocation);
        }
    }

    @Override
    public void onBubbleClick() {
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.MAP_CLICK, model, contentMessage);
        }
    }
}
