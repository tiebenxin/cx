package net.cb.cb.library.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.cb.cb.library.R;
import net.cb.cb.library.inter.ICommonSelectClickListner;
import net.cb.cb.library.inter.ICustomerItemClick;
import net.cb.cb.library.inter.ITrendClickListner;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-10-15
 * @updateAuthor
 * @updateDate
 * @description 弹框工具类
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class DialogHelper {

    private static DialogHelper INSTANCE;

    public static DialogHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DialogHelper();
        }
        return INSTANCE;
    }

    /**
     * 音视频通话弹框
     *
     * @param context
     * @param iCustomerItemClick
     */
    public void createSelectDialog(Context context, final ICustomerItemClick iCustomerItemClick) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_select, null);
        final Dialog selectDialog = new Dialog(context, R.style.upload_image_methods_dialog);
        selectDialog.setContentView(dialogview);
        Window window = selectDialog.getWindow();
        WindowManager.LayoutParams dialogParams = window.getAttributes();
        dialogParams.gravity = Gravity.BOTTOM;
        dialogParams.width = width;
//        Rect rect = new Rect();
//        View view1 = window.getDecorView();
//        view1.getWindowVisibleDisplayFrame(rect);
//        window.setWindowAnimations(com.internalkye.express.R.style.Animation_Popup);
//        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(dialogParams);
        // 视频通话
        dialogview.findViewById(R.id.layout_video).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    iCustomerItemClick.onClickItemVideo();
                }
            }
        });
        // 语音通话
        dialogview.findViewById(R.id.layout_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    iCustomerItemClick.onClickItemVoice();
                }
            }
        });
        // 取消
        dialogview.findViewById(R.id.txt_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    iCustomerItemClick.onClickItemCancel();
                }
            }
        });
        selectDialog.show();
    }

    /**
     * 音视频通话弹框
     *
     * @param context
     * @param iCustomerItemClick
     */
    public void createFollowDialog(Context context, final ICustomerItemClick iCustomerItemClick) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_circle_follow, null);
        final Dialog selectDialog = new Dialog(context, R.style.upload_image_methods_dialog);
        selectDialog.setContentView(dialogview);
        Window window = selectDialog.getWindow();
        WindowManager.LayoutParams dialogParams = window.getAttributes();
        dialogParams.gravity = Gravity.BOTTOM;
        dialogParams.width = width;
        window.setAttributes(dialogParams);
        dialogview.findViewById(R.id.txt_cancle).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    iCustomerItemClick.onClickItemVoice();
                }
            }
        });
        dialogview.findViewById(R.id.tv_follow).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
            }
        });
        dialogview.findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
            }
        });
        dialogview.findViewById(R.id.tv_chat).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
            }
        });
        dialogview.findViewById(R.id.tv_report).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
            }
        });
        selectDialog.show();
    }

    /**
     * 我的动态(我的朋友圈) 底部弹框
     * @param context
     * @param clickListner
     */
    public void createTrendDialog(Context context, final ITrendClickListner clickListner) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_circle_trend_set, null);
        final Dialog selectDialog = new Dialog(context, R.style.upload_image_methods_dialog);
        selectDialog.setContentView(dialogview);
        Window window = selectDialog.getWindow();
        WindowManager.LayoutParams dialogParams = window.getAttributes();
        dialogParams.gravity = Gravity.BOTTOM;
        dialogParams.width = width;
        window.setAttributes(dialogParams);
        dialogview.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.clickCancle();
                }
            }
        });
        dialogview.findViewById(R.id.tv_istop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.clickIsTop();
                }
            }
        });
        dialogview.findViewById(R.id.tv_auth).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.clickAuthority();
                }
            }
        });
        dialogview.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.clickDelete();
                }
            }
        });
        selectDialog.show();
    }

    /**
     * 通用列表选择弹框 (默认4个选项，视实际情况自行添加)
     *
     * @param context
     * @param clickListner
     */
    public void createCommonSelectListDialog(Context context,List<String> items, final ICommonSelectClickListner clickListner) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_common_select, null);
        final Dialog selectDialog = new Dialog(context, R.style.upload_image_methods_dialog);
        selectDialog.setContentView(dialogview);
        Window window = selectDialog.getWindow();
        WindowManager.LayoutParams dialogParams = window.getAttributes();
        dialogParams.gravity = Gravity.BOTTOM;
        dialogParams.width = width;
        window.setAttributes(dialogParams);
        //初始化
        TextView tvOne = dialogview.findViewById(R.id.tv_one);
        TextView tvTwo = dialogview.findViewById(R.id.tv_two);
        TextView tvThree = dialogview.findViewById(R.id.tv_three);
        TextView tvFour = dialogview.findViewById(R.id.tv_four);
        View lineOne = dialogview.findViewById(R.id.line_one);
        View lineTwo = dialogview.findViewById(R.id.line_two);
        View lineThree = dialogview.findViewById(R.id.line_three);
        View lineFour = dialogview.findViewById(R.id.line_four);
        //展示数量 + 更改每项文案
        if(items.size()==1){
            tvOne.setVisibility(View.VISIBLE);
            tvOne.setText(items.get(0));
            tvTwo.setVisibility(View.GONE);
            tvThree.setVisibility(View.GONE);
            tvFour.setVisibility(View.GONE);
            lineOne.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.GONE);
            lineThree.setVisibility(View.GONE);
            lineFour.setVisibility(View.GONE);
        }else if(items.size()==2){
            tvOne.setVisibility(View.VISIBLE);
            tvOne.setText(items.get(0));
            tvTwo.setVisibility(View.VISIBLE);
            tvTwo.setText(items.get(1));
            tvThree.setVisibility(View.GONE);
            tvFour.setVisibility(View.GONE);
            lineOne.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.VISIBLE);
            lineThree.setVisibility(View.GONE);
            lineFour.setVisibility(View.GONE);
        }else if(items.size()==3){
            tvOne.setVisibility(View.VISIBLE);
            tvOne.setText(items.get(0));
            tvTwo.setVisibility(View.VISIBLE);
            tvTwo.setText(items.get(1));
            tvThree.setVisibility(View.VISIBLE);
            tvThree.setText(items.get(2));
            tvFour.setVisibility(View.GONE);
            lineOne.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.VISIBLE);
            lineThree.setVisibility(View.VISIBLE);
            lineFour.setVisibility(View.GONE);
        }else {
            tvOne.setVisibility(View.VISIBLE);
            tvOne.setText(items.get(0));
            tvTwo.setVisibility(View.VISIBLE);
            tvTwo.setText(items.get(1));
            tvThree.setVisibility(View.VISIBLE);
            tvThree.setText(items.get(2));
            tvFour.setVisibility(View.VISIBLE);
            tvFour.setText(items.get(3));
            lineOne.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.VISIBLE);
            lineThree.setVisibility(View.VISIBLE);
            lineFour.setVisibility(View.VISIBLE);
        }

        dialogview.findViewById(R.id.tv_one).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.selectOne();
                }
            }
        });
        dialogview.findViewById(R.id.tv_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.selectTwo();
                }
            }
        });
        dialogview.findViewById(R.id.tv_three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.selectThree();
                }
            }
        });
        dialogview.findViewById(R.id.tv_four).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.selectFour();
                }
            }
        });
        dialogview.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.onCancle();
                }
            }
        });
        selectDialog.show();
    }
}
