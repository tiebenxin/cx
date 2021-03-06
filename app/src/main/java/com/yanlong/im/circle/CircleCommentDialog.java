package com.yanlong.im.circle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.luck.picture.lib.face.FaceViewPager;
import com.luck.picture.lib.face.bean.FaceBean;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.utils.InputUtil;
import com.luck.picture.lib.utils.PatternUtil;
import com.yanlong.im.R;
import com.yanlong.im.databinding.DialogCircleCommentBinding;

import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
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
public class CircleCommentDialog extends Dialog implements View.OnClickListener {

    DialogCircleCommentBinding binding;

    private Context mContext;
    private InputMethodManager inputMethodManager;
    private OnMessageListener mListener;
    private boolean isKeyboard = false;
    private int mKeyboardHeight = 0;// 记录软键盘的高度
    private int mFuncHeight = 0;// 功能面板默认高度
    private Window dialogWindow;
    private String mReplyName;// 回复人昵称
    private boolean isShowSoft;// 是否弹起软键盘

    public CircleCommentDialog(Context context, String replyName, boolean isShow, OnMessageListener listener) {
        super(context, R.style.AppDialogNoFade);
        mContext = context;
        mListener = listener;
        mReplyName = replyName;
        isShowSoft = isShow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    public void initView() {
        mFuncHeight = ScreenUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.circle_details_face_height));
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_circle_comment, null, false);
        if (!TextUtils.isEmpty(mReplyName)) {
            binding.etMessage.setHint("回复" + mReplyName + ":");
        }
        setCanceledOnTouchOutside(false);
        setContentView(binding.getRoot());
        dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(mContext, 60));
        dialogWindow.setAttributes(lp);
        if (isShowSoft) {
            setSoftKeyboard();
        }
    }

    public void initEvent() {
        binding.ivEmj.setOnClickListener(this);
        binding.tvSend.setOnClickListener(this);
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener((Activity) mContext);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                setRecyclerViewHeight(60);
            }

            @Override
            public void keyBoardHide(int h) {
                if (isKeyboard) {
                    setRecyclerViewHeight(257);
                } else {
                    setRecyclerViewHeight(60);
                }
            }
        });

        // 表情点击事件
        binding.viewFaceview.setOnItemClickListener(new FaceViewPager.FaceClickListener() {

            @Override
            public void OnItemClick(FaceBean bean) {
                if ((binding.etMessage.getText().toString().length() + bean.getName().length()) < 150) {
                    binding.etMessage.addEmojSpan(bean.getName());
                }
            }
        });
        // 删除表情按钮
        binding.viewFaceview.setOnDeleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int selection = binding.etMessage.getSelectionStart();
                    String msg = binding.etMessage.getText().toString().trim();
                    if (selection >= 1) {
                        if (selection >= PatternUtil.FACE_EMOJI_LENGTH) {
                            String emoji = msg.substring(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                            if (PatternUtil.isExpression(emoji)) {
                                binding.etMessage.getText().delete(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                                return;
                            }
                        }
                        binding.etMessage.getText().delete(selection - 1, selection);
                    }
                } catch (Exception e) {
                }
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send:
                String msg = binding.etMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    if (mListener != null) {
                        mListener.OnMessage(msg);
                    }
                } else {
                    ToastUtil.show("请输入评论");
                }
                break;
            case R.id.iv_emj:
                isKeyboard = !isKeyboard;
                showOrHideInput();
                break;
        }
    }

    private void setRecyclerViewHeight(int height) {
        if (height > 0) {
            mKeyboardHeight = height;
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(mContext, height));
            dialogWindow.setAttributes(lp);
        }
    }

    private void showOrHideInput() {
        if (isKeyboard) {
            binding.ivEmj.setImageLevel(1);
            InputUtil.hideKeyboard(binding.etMessage);
        } else {
            binding.ivEmj.setImageLevel(0);
            binding.etMessage.requestFocus();
            setRecyclerViewHeight(60);
            InputUtil.showKeyboard(binding.etMessage);
        }
    }

    public void clearEdit() {
        binding.etMessage.setText("");
        binding.etMessage.clearFocus();
        InputUtil.hideKeyboard(binding.etMessage);
    }

    public interface OnMessageListener {
        void OnMessage(String msg);
    }

}
