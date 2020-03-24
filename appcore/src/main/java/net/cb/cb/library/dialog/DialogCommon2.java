package net.cb.cb.library.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import net.cb.cb.library.R;
import net.cb.cb.library.base.BaseDialog;

/**
 * @author Liszt
 * @date 2019/12/4
 * Description 默认dialog 2
 */
public class DialogCommon2 extends BaseDialog {

    private TextView tvTitle, tvContent, tvCancel;
    private IDialogListener listener;
    private int colorSure = Color.parseColor("#32b053");

    public DialogCommon2(Context context) {
        this(context, R.style.MyDialogTheme);
    }

    public DialogCommon2(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_default2);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvCancel = findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
    }

    public DialogCommon2 setTitle(String title) {
        if (tvTitle != null && !TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        return this;
    }


    public DialogCommon2 setButtonTxt(String txt) {
        tvCancel.setText(txt);
        return this;
    }


    /**
     * @param center 是否内容居中，默认居右
     */
    public DialogCommon2 setContent(String txt, boolean center) {
        tvContent.setText(txt);
        if (center) {
            tvContent.setGravity(Gravity.CENTER);
        }
        return this;
    }


    @Override
    public void processClick(View view) {
        int id = view.getId();
        if (id == tvCancel.getId()) {
            if (listener != null) {
                listener.onCancel();
                dismiss();
            }
        }
    }

    public DialogCommon2 setListener(IDialogListener l) {
        listener = l;
        return this;
    }

    public interface IDialogListener {

        //取消
        void onCancel();
    }
}
