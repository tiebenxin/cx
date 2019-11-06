package com.yanlong.im.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.base.BaseDialog;

/**
 * @anthor Liszt
 * @data 2019/8/29
 * Description 版本更新dialog
 */
public class UpdateVersionDialog extends BaseDialog {


    private TextView tv_content;

    public UpdateVersionDialog(Context context, int theme) {
        super(context, theme);
    }

    public UpdateVersionDialog(Context context) {
        super(context);
    }


    @Override
    public void initView() {
        setContentView(R.layout.dialog_update_version);
        tv_content = findViewById(R.id.tv_content);
    }

//    public void setTitle(String t) {
//        tv_title.setText(t);
//    }

    public void setMessage(String msg) {
        tv_content.setText(msg);
    }

    @Override
    public void processClick(View view) {

    }
}
