package com.yanlong.im.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import com.yanlong.im.R;
import com.yanlong.im.listener.BaseListener;
import net.cb.cb.library.base.BaseDialogTwo;


/**
 * Author: zgd
 * Date: 2019/7/23
 */
public class TestDialog extends BaseDialogTwo implements View.OnClickListener {
    TextView commit_tv;

    private BaseListener listener;

    public TestDialog(@NonNull Context context, BaseListener listener) {
        super(R.layout.dialog_test, context);
        this.listener = listener;
    }

    @Override
    protected void initView() {
        super.initView();

        commit_tv=findViewById(R.id.commit_tv);
        commit_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.commit_tv: //
                break;
        }
    }
}
