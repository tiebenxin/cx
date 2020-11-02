package net.cb.cb.library.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.cb.cb.library.R;
import net.cb.cb.library.inter.ICircleSetupClick;
import net.cb.cb.library.inter.ICommonSelectClickListner;
import net.cb.cb.library.inter.ICustomerItemClick;
import net.cb.cb.library.inter.IFriendTrendClickListner;
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
     * 关注弹框
     *
     * @param context
     * @param isFriend
     * @param iCircleSetupClick
     */
    public void createFollowDialog(Context context, String txt, boolean isFriend, final ICircleSetupClick iCircleSetupClick) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_circle_follow, null);
        final Dialog selectDialog = new Dialog(context, R.style.upload_image_methods_dialog);
        TextView tvFollow = dialogview.findViewById(R.id.tv_follow);
        TextView tvNoLook = dialogview.findViewById(R.id.tv_no_look);
        TextView tvChat = dialogview.findViewById(R.id.tv_chat);
        TextView tvReport = dialogview.findViewById(R.id.tv_report);
        tvFollow.setText(txt);
        Drawable drawable = null;
        Drawable drawableDel = null;
        if ("取消关注".equals(txt)) {
            drawable = context.getResources().getDrawable(R.mipmap.ic_cancle_follow);
            tvNoLook.setVisibility(View.GONE);
        } else if ("关注TA".equals(txt)) {
            drawable = context.getResources().getDrawable(R.mipmap.ic_circle_details_follow);
            tvNoLook.setVisibility(View.VISIBLE);
        } else {
            drawable = context.getResources().getDrawable(R.mipmap.ic_circle_auth);
            drawableDel = context.getResources().getDrawable(R.mipmap.ic_circle_delete);
            drawableDel.setBounds(0, 0, drawableDel.getMinimumWidth(), drawableDel.getMinimumHeight());//必须设置图片大小，否则不显示
            tvNoLook.setCompoundDrawables(null, drawableDel, null, null);

            tvFollow.setText("权限");
            tvNoLook.setText("删除");
            tvChat.setVisibility(View.INVISIBLE);
            tvReport.setVisibility(View.INVISIBLE);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
        tvFollow.setCompoundDrawables(null, drawable, null, null);
        Drawable drawableChat;
        if (isFriend) {
            drawableChat = context.getResources().getDrawable(R.mipmap.ic_circle_chat);
            tvChat.setText("私聊");
        } else {
            drawableChat = context.getResources().getDrawable(R.mipmap.ic_circle_add_friend);
            tvChat.setText("添加好友");
        }
        drawableChat.setBounds(0, 0, drawableChat.getMinimumWidth(), drawableChat.getMinimumHeight());//必须设置图片大小，否则不显示
        tvChat.setCompoundDrawables(null, drawableChat, null, null);

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
            }
        });
        tvFollow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (iCircleSetupClick != null) {
                    iCircleSetupClick.onClickFollow();
                }
            }
        });
        tvNoLook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (iCircleSetupClick != null) {
                    if ("删除".equals(tvNoLook.getText().toString())) {
                        iCircleSetupClick.onClickNoLook(true);
                    } else {
                        iCircleSetupClick.onClickNoLook(false);
                    }
                }
            }
        });
        tvChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (iCircleSetupClick != null) {
                    iCircleSetupClick.onClickChat(isFriend);
                }
            }
        });
        tvReport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (iCircleSetupClick != null) {
                    iCircleSetupClick.onClickReport();
                }
            }
        });
        selectDialog.show();
    }

    /**
     * 我的动态(我的朋友圈) 底部弹框
     *
     * @param isTop        是否置顶 1是 0否
     * @param context
     * @param clickListner
     */
    public void createTrendDialog(int isTop, Context context, final ITrendClickListner clickListner) {

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
        TextView tvTop = dialogview.findViewById(R.id.tv_istop);
        if (isTop == 1) {
            tvTop.setText("取消置顶");
        } else {
            tvTop.setText("置顶");
        }
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
                    clickListner.clickIsTop(isTop);
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
     * @param items        支持修改每项文案
     * @param clickListner
     */
    public void createCommonSelectListDialog(Context context, List<String> items, final ICommonSelectClickListner clickListner) {

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
        if (items.size() == 1) {
            tvOne.setVisibility(View.VISIBLE);
            tvOne.setText(items.get(0));
            tvTwo.setVisibility(View.GONE);
            tvThree.setVisibility(View.GONE);
            tvFour.setVisibility(View.GONE);
            lineOne.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.GONE);
            lineThree.setVisibility(View.GONE);
            lineFour.setVisibility(View.GONE);
        } else if (items.size() == 2) {
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
        } else if (items.size() == 3) {
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
        } else {
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

    /**
     * 好友动态(好友朋友圈) 底部弹框
     *
     * @param context
     * @param clickListner
     */
    public void createFriendTrendDialog(boolean showCancleFollow, Context context, final IFriendTrendClickListner clickListner) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_friend_trend_set, null);
        final Dialog selectDialog = new Dialog(context, R.style.upload_image_methods_dialog);
        selectDialog.setContentView(dialogview);
        Window window = selectDialog.getWindow();
        WindowManager.LayoutParams dialogParams = window.getAttributes();
        dialogParams.gravity = Gravity.BOTTOM;
        dialogParams.width = width;
        window.setAttributes(dialogParams);
        TextView tvFollow = dialogview.findViewById(R.id.tv_follow);
        if (showCancleFollow) {
            tvFollow.setVisibility(View.VISIBLE);
        } else {
            tvFollow.setVisibility(View.GONE);
        }
        tvFollow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.clickFollow();
                }
            }
        });
        dialogview.findViewById(R.id.tv_report).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectDialog != null) {
                    selectDialog.dismiss();
                }
                if (!ViewUtils.isFastDoubleClick()) {
                    clickListner.clickReport();
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
                    clickListner.clickCancle();
                }
            }
        });
        selectDialog.show();
    }
}
