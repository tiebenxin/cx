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
public class MapDialog extends BaseDialogTwo implements View.OnClickListener {
    TextView baidu_tv;
    TextView gaode_tv;
    TextView cancel_tv;

    private BaseListener listener;

    public MapDialog(@NonNull Context context, BaseListener listener) {
        super(R.layout.dialog_map, context);
        this.listener = listener;
    }

    @Override
    protected void initView() {
        super.initView();

        baidu_tv=findViewById(R.id.baidu_tv);
        gaode_tv=findViewById(R.id.gaode_tv);
        cancel_tv=findViewById(R.id.cancel_tv);

        baidu_tv.setOnClickListener(this);
        gaode_tv.setOnClickListener(this);
        cancel_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.baidu_tv: //
                listener.onSuccess("baidu");
                break;
            case R.id.gaode_tv: //
                listener.onSuccess("gaode");
                break;
            case R.id.cancel_tv: //
                break;
        }
    }
}
