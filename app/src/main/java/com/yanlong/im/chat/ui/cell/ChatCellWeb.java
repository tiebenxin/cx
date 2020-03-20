package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.WebMessage;

public class ChatCellWeb extends ChatCellBase {

    private TextView tvTitle, tvInfo, tvAppName;
    private ImageView ivIcon, ivAppIcon;
    private WebMessage contentMessage;

    protected ChatCellWeb(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle = getView().findViewById(R.id.tv_title);
        tvInfo = getView().findViewById(R.id.tv_info);
        ivIcon = getView().findViewById(R.id.iv_icon);
        tvAppName = getView().findViewById(R.id.tv_app_name);
        ivAppIcon = getView().findViewById(R.id.tv_app_name);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        contentMessage = message.getWebMessage();
        showContent(contentMessage);
    }

    private void showContent(WebMessage message) {
        if (message == null) {
            return;
        }
        loadIcon(message.getIconUrl(), ivIcon);
        tvTitle.setText(message.getTitle());
        tvInfo.setText(message.getDescription());
        tvAppName.setText(message.getAppName());
        tvAppName.setText(message.getAppName());
        loadIcon(message.getIconUrl(), ivAppIcon);
    }

    /*
     * 加载发送者头像
     * */
    private void loadIcon(String url, ImageView iv) {
        if (mContext == null || iv == null) {
            return;
        }
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(iv);
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null && model != null) {
            mCellListener.onEvent(ChatEnum.ECellEventType.CARD_CLICK, model, contentMessage);
        }
    }
}
