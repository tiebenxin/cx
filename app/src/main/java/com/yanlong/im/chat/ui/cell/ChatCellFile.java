package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.manager.MessageManager;

import net.cb.cb.library.utils.FileUtils;

import static android.view.View.VISIBLE;

public class ChatCellFile extends ChatCellFileBase {

    private TextView tvFileName;
    private ImageView ivFileIcon;
    private TextView tvFileSize;
    private SendFileMessage contentMessage;

    protected ChatCellFile(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tvFileName = getView().findViewById(R.id.tv_file_name);
        ivFileIcon = getView().findViewById(R.id.iv_file_icon);
        tvFileSize = getView().findViewById(R.id.tv_file_size);
    }

    @Override
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

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        contentMessage = message.getSendFileMessage();
        showContent(contentMessage);
    }

    private void showContent(SendFileMessage contentMessage) {
        tvFileName.setText(contentMessage.getFile_name());
        if (contentMessage.getSize() > 0) {
            tvFileSize.setText(FileUtils.getFileSizeString(contentMessage.getSize()));
        }
        ivFileIcon.setImageResource(MessageManager.getInstance().getFileIconRid(contentMessage.getFormat()));
    }


    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mCellListener != null && model != null && model.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
            mCellListener.onEvent(ChatEnum.ECellEventType.FILE_CLICK, model, contentMessage);
        }
    }
}
