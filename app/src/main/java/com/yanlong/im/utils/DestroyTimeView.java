package com.yanlong.im.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yanlong.im.R;
import com.yanlong.im.view.PickValueView;

/**
 * @创建人 shenxin
 * @创建时间 2019/10/23 0023 19:44
 */
public class DestroyTimeView implements View.OnClickListener {
    private Context context;
    TextView tvCancel;
    TextView tvConfirm;
    TextView tvContent;
    PickValueView pickString;
    private OnClickItem onClickItem;
    private Dialog dialog;
    String[] valueStr = new String[]{"关闭", "退出即焚", "5秒", "10秒", "30秒", "1分钟",
            "5分钟", "30分钟", "1小时", "6小时", "12小时", "1天", "一个星期"};
    private int survivaltime;
    private String content;


    public DestroyTimeView(Context context) {
        this.context = context;
    }

    public void initView() {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View contentView = LayoutInflater.from(context).inflate(R.layout.view_destroy_time, null);
        //获取组件
        tvCancel = contentView.findViewById(R.id.tv_cancel);
        tvContent = contentView.findViewById(R.id.tv_content);
        tvConfirm = contentView.findViewById(R.id.tv_confirm);
        pickString = contentView.findViewById(R.id.pickString);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        //获取Dialog的监听
        pickString.setOnSelectedChangeListener(new PickValueView.onSelectedChangeListener() {
            @Override
            public void onSelected(PickValueView view, Object leftValue, Object middleValue, Object rightValue) {
                content = (String) leftValue;
                tvContent.setText(content);
                survivaltime = getSurvivaltime(content);

            }
        });
        pickString.setValueData(valueStr, valueStr[0]);
        dialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        dialog.getWindow().setGravity(Gravity.BOTTOM);//弹窗位置
        dialog.getWindow().setWindowAnimations(R.style.ActionSheetDialogStyle);//弹窗样式
        dialog.show();//显示弹窗
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dialog.dismiss();
                break;
            case R.id.tv_confirm:
                if (onClickItem != null) {
                    onClickItem.onClickItem(content,survivaltime);
                }
                dialog.dismiss();
                break;
        }
    }


    public interface OnClickItem {
        void onClickItem(String content, int survivaltime);
    }


    public void setListener(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }


    private int getSurvivaltime(String content) {
        if (TextUtils.isEmpty(content)) {
            return 0;
        }
        if (content.equals("关闭")) {
            return 0;
        } else if (content.equals("退出即焚")) {
            return -1;
        } else if (content.equals("5秒")) {
            return 5;
        } else if (content.equals("10秒")) {
            return 10;
        } else if (content.equals("30秒")) {
            return 30;
        } else if (content.equals("1分钟")) {
            return 60;
        } else if (content.equals("5分钟")) {
            return 300;
        } else if (content.equals("30分钟")) {
            return 1800;
        } else if (content.equals("1小时")) {
            return 3600;
        } else if (content.equals("6小时")) {
            return 21000;
        } else if (content.equals("12小时")) {
            return 43200;
        } else if (content.equals("1天")) {
            return 86400;
        } else if (content.equals("一个星期")) {
            return 604800;
        } else {
            return 0;
        }
    }
}
