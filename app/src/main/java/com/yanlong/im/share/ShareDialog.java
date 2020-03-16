package com.yanlong.im.share;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.base.BaseDialog;

/**
 * @author Liszt
 * @date 2020/3/13
 * Description
 */
public class ShareDialog extends BaseDialog {
    private TextView tvTitle, tvContent, tvLeft, tvRight;
    private ImageView ivOK;
    private IDialogListener listener;


    public ShareDialog(Context context, int theme) {
        super(context, theme);
    }

    public ShareDialog(Context context) {
        this(context, R.style.MyDialogTheme);
    }

    public ShareDialog initOtherAppName(String name) {
        if (!TextUtils.isEmpty(name)) {
            tvLeft.setText("留在" + name);
        }
        return this;
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_share_success);
        tvTitle = findViewById(com.hm.cxpay.R.id.tv_title);
        tvContent = findViewById(com.hm.cxpay.R.id.tv_content);
        tvLeft = findViewById(com.hm.cxpay.R.id.tv_left);
        tvRight = findViewById(com.hm.cxpay.R.id.tv_right);
        ivOK = findViewById(R.id.iv_ok);
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        int id = view.getId();
        if (id == tvLeft.getId()) {
            if (listener != null) {
                listener.onLeft();
                dismiss();
            }
        } else if (id == tvRight.getId()) {
            if (listener != null) {
                listener.onRight();
                dismiss();
            }
        }
    }

    public ShareDialog setListener(IDialogListener l) {
        listener = l;
        return this;
    }

    public interface IDialogListener {

        void onLeft();

        void onRight();
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(50, 0, 50, 0);
        getWindow().setAttributes(layoutParams);
    }
}
