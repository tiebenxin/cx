package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.utils.FileUtils;

import static android.view.View.VISIBLE;

public class ChatCellFile extends ChatCellFileBase {

    private TextView tvFileName;
    private ImageView ivFileIcon;
    private TextView tvFileSize;
    private SendFileMessage contentMessage;
    private boolean isFromMyPC;//是否为自己在PC端发的消息
    private LinearLayout layoutFromPC;

    protected ChatCellFile(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tvFileName = getView().findViewById(R.id.tv_file_name);
        ivFileIcon = getView().findViewById(R.id.iv_file_icon);
        tvFileSize = getView().findViewById(R.id.tv_file_size);
        layoutFromPC = getView().findViewById(R.id.layout_from_pc);
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
        // TODO　处理发送文件时出现的Attempt to invoke virtual method 'long java.lang.Long.longValue()' on a null object reference
        if (UserAction.getMyId() != null && message != null
                && message.getFrom_uid() != null && message.getTo_uid() != null) {
            isFromMyPC = message.getFrom_uid() == UserAction.getMyId().intValue() && message.getTo_uid() == 0L;
        } else {
            isFromMyPC = false;
        }
        showContent(contentMessage);
    }

    private void showContent(SendFileMessage contentMessage) {
        tvFileName.setText(contentMessage.getFile_name());
        if (contentMessage.getSize() > 0) {
            tvFileSize.setText(FileUtils.getFileSizeString(contentMessage.getSize()));
        }
        ivFileIcon.setImageResource(MessageManager.getInstance().getFileIconRid(contentMessage.getFormat()));
        //来自电脑端，且为我发送的
        if (isFromMyPC) {
            layoutFromPC.setVisibility(VISIBLE);
        } else {
            layoutFromPC.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBubbleClick() {
        if (mCellListener != null && model != null && model.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
            mCellListener.onEvent(ChatEnum.ECellEventType.FILE_CLICK, model, contentMessage);
        }
    }
}
