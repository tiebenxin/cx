package com.yanlong.im.chat.ui.cell;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yanlong.im.R;

/**
 * Created by Liszt on 2020/1/6.
 * 新消息
 */

public class ControllerNewMessage {

    private View rootView;
    private final TextView tv_count;
    private OnControllerClickListener listenter;

    public ControllerNewMessage(View v) {
        rootView = v;
        tv_count = v.findViewById(R.id.tv_count);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenter != null) {
                    listenter.onClick();
                }
            }
        });
    }

    public void setVisible(boolean visible) {
        rootView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setCount(int count) {
        if (count <= 0) {
            setVisible(false);
        } else {
            setVisible(true);
            if (count > 99) {
                tv_count.setText(String.format("有%s条新消息", 99 + "+"));
            } else {
                tv_count.setText(String.format("有%s条新消息", count));
            }
        }
    }

    public void setClickListener(OnControllerClickListener l) {
        listenter = l;
    }
}
