package com.yanlong.im.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.ScreenUtil;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-10-09
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class DeletPopWindow extends PopupWindow {
    private Context mContext;
    private View mView;

    public DeletPopWindow(Context context, boolean isMe, OnClickListener listener) {
        mContext = context;
        mView = View.inflate(context, R.layout.view_pop_delete, null);
        TextView tvCopy = mView.findViewById(R.id.tv_copy);
        TextView tvDelete = mView.findViewById(R.id.tv_delete);
        setHeight(ScreenUtil.dip2px(mContext, 55));
        setWidth(ScreenUtil.getScreenWidth(mContext));
        if (!isMe) {
            tvDelete.setText("举报");
        }
        tvCopy.setOnClickListener(o -> {
            dismiss();
            listener.onClick(CoreEnum.ELongType.COPY);
        });
        tvDelete.setOnClickListener(o -> {
            dismiss();
            if (isMe) {
                listener.onClick(CoreEnum.ELongType.DELETE);
            } else {
                listener.onClick(CoreEnum.ELongType.REPORT);
            }
        });
        setContentView(mView);
        setOutsideTouchable(true);
    }

    public void showViewTop(View view) {
        // 重新获取自身的长宽高
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = mView.getMeasuredWidth();
        int popupHeight = mView.getMeasuredHeight();

        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = (location[0] + view.getWidth() / 2) - popupWidth / 2;
        //在控件上方显示
        showAtLocation(view, Gravity.NO_GRAVITY, x, location[1] - popupHeight);
    }

    public interface OnClickListener {
        void onClick(int type);
    }
}
