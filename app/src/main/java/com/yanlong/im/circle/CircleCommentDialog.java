package com.yanlong.im.circle;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.yanlong.im.R;
import com.yanlong.im.databinding.DialogCircleCommentBinding;

import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.ToastUtil;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-10
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class CircleCommentDialog extends Dialog {

    DialogCircleCommentBinding binding;

    private Context mContext;
    private InputMethodManager inputMethodManager;
    private OnMessageListener mListener;

    void onClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_send:
                String msg = binding.etMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    if (mListener != null) {
                        mListener.OnMessage(msg);
                    }
                } else {
                    ToastUtil.show("请输入评论");
                }
                dismiss();
                break;
            case R.id.iv_expression:
                break;
        }
    }

    public CircleCommentDialog(Context context, OnMessageListener listener) {
        super(context, R.style.AppDialog);
        mContext = context;
        mListener = listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_circle_comment, null, false);
        setContentView(binding.getRoot());
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(mContext, 60));
        dialogWindow.setAttributes(lp);
        setSoftKeyboard();
    }

    private void setSoftKeyboard() {
        binding.etMessage.setFocusable(true);
        binding.etMessage.setFocusableInTouchMode(true);
        binding.etMessage.requestFocus();
        //为 commentEditText 设置监听器，在 DialogFragment 绘制完后立即呼出软键盘，呼出成功后即注销
        binding.etMessage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    if (inputMethodManager.showSoftInput(binding.etMessage, 0)) {
                        binding.etMessage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    public interface OnMessageListener {
        void OnMessage(String msg);
    }

}
