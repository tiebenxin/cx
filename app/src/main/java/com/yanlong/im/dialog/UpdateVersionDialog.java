package com.yanlong.im.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.utils.update.UpdateAppDialog;

import net.cb.cb.library.base.BaseDialog;

/**
 * @anthor Liszt
 * @data 2019/8/29
 * Description 版本更新dialog
 */
public class UpdateVersionDialog extends BaseDialog {


    private TextView tv_content, tv_version, tv_update, tv_cancel, tv_cancel_download;
    private LinearLayout ll_update;
    private LinearLayout ll_progress;
    private ProgressBar progressBar;

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
        tv_version = findViewById(R.id.tv_version);
        ll_update = findViewById(R.id.ll_update);
        tv_update = findViewById(R.id.tv_update);
        tv_cancel = findViewById(R.id.tv_cancel);

        ll_progress = findViewById(R.id.ll_progress);
        progressBar = findViewById(R.id.progress_bar);
        tv_cancel_download = findViewById(R.id.tv_cancel_download);

    }

    private void init(Activity activity, String title, String msg, String version, UpdateAppDialog.Event e) {

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
