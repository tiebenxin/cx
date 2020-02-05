package com.yanlong.im.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.base.BaseDialog;

/**
 * @author Liszt
 * @date 2020/2/4
 * Description 多选转发弹窗
 */
public class ForwardDialog extends BaseDialog {

    private TextView tvOne;
    private TextView tvMerge;
    private TextView tvCancel;
    private IForwardListener listener;

    public ForwardDialog(Context context) {
        super(context, com.hm.cxpay.R.style.MyDialogTheme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_forward);
        tvOne = findViewById(R.id.tv_one);
        tvMerge = findViewById(R.id.tv_merge);
        tvCancel = findViewById(R.id.tv_cancel);
        tvOne.setOnClickListener(this);
        tvMerge.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        int id = view.getId();
        if (id == tvOne.getId()) {
            if (listener != null) {
                listener.onOneForward();
            }
            dismiss();
        } else if (id == tvMerge.getId()) {
            if (listener != null) {
                listener.onMergeForward();
            }
            dismiss();
        } else if (id == tvCancel.getId()) {
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        }
    }

    public interface IForwardListener {
        void onOneForward();

        void onMergeForward();

        void onCancel();
    }

    public void setListener(IForwardListener l) {
        listener = l;
    }
}
