package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;

/*
 * base文件消息
 * */
public abstract class ChatCellFileBase extends ChatCellBase {
    public ProgressBar progressBar;
    public TextView tv_progress;
    public LinearLayout ll_progress;

    protected ChatCellFileBase(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }


    @Override
    protected void initView() {
        super.initView();
        ll_progress = getView().findViewById(R.id.ll_progress);
        progressBar = getView().findViewById(R.id.progress_bar);
        tv_progress = getView().findViewById(R.id.tv_progress);
    }


    public abstract void checkSendStatus();

    public void updateProgress(@ChatEnum.ESendStatus int status, int progress) {
        if (ll_progress != null && progressBar != null && tv_progress != null) {
            checkSendStatus();
            if (progress > 0 && progress < 100) {
                ll_progress.setVisibility(View.VISIBLE);
                tv_progress.setText(progress + "%");
            } else {
                ll_progress.setVisibility(View.GONE);
            }
        }
    }
}
