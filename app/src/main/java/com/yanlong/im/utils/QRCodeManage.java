package com.yanlong.im.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.zxing.Result;
import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.eventbus.EventMsgSync;
import com.yanlong.im.chat.ui.AddGroupActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.MyselfInfoActivity;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.QRCodeBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Response;

public class QRCodeManage {
    public static final String TAG = "QRCodeManage";
    //YLIM://ADDFRIEND?id=xxx
    //YLIM://ADDGROUP?id=xxx
    public static final String HEAD = "YLIM:"; //二维码头部
    public static final String ID = "id"; //群id
    public static final String UID = "uid"; //用户ID
    public static final String TIME = "time"; //时间戳
    public static final String NICK_NAME = "nickname";

    public static final String ADD_FRIEND_FUNCHTION = "ADDFRIEND"; //添加好友
    public static final String ADD_GROUP_FUNCHTION = "ADDGROUP"; //添加群

    public static final String DOWNLOAD_APP_URL = "https://www.zln365.com"; //下载地址
    public static final String PC_LOGIN_URL = "cx://login/"; //扫码登录地址
    private static String code = "";//扫码后的code
    private static String synck = "1";//是否同步  1同步 0不同步  默认改为同步


    /**
     * 扫描二维码转换bean
     *
     * @param QRCode 二维码字符串
     * @return 二维码bean
     */
    public static QRCodeBean getQRCodeBean(Context context, String QRCode) {
        QRCodeBean bean = null;
        LogUtil.getLog().e(TAG, "二维码" + QRCode);
        if (!TextUtils.isEmpty(QRCode)) {
            String oneStrs[] = QRCode.split("//");
            if (oneStrs == null || oneStrs.length > 2) {
                ToastUtil.show(context, "错误二维码");
            } else {
                if (!oneStrs[0].contains(HEAD)) {
                    ToastUtil.show(context, "错误二维码");
                } else {
                    bean = new QRCodeBean();
                    bean.setHead(oneStrs[0]);
                    String twoStrs[] = oneStrs[1].split("\\?");
                    if (twoStrs != null && twoStrs.length >= 2) {
                        bean.setFunction(twoStrs[0]);
                        String threeStrs[] = twoStrs[1].split("&");
                        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
                        if (threeStrs != null && threeStrs.length > 0) {
                            for (int i = 0; i < threeStrs.length; i++) {
                                String fourStrs[] = threeStrs[i].split("=");
                                parameters.put(fourStrs[0], fourStrs[1]);
                            }
                        }
                        bean.setParameter(parameters);
                    }
                }
            }
        }
        return bean;
    }

    /**
     * bean 转二维码
     *
     * @param bean 二维码bean
     * @return 二维码
     */
    public static String getQRcodeStr(QRCodeBean bean) {
        StringBuffer code = new StringBuffer();
        if (bean != null) {
            code.append(bean.getHead() + "//" + bean.getFunction() + "?");
            if (bean.getParameter() != null && bean.getParameter().size() > 0) {
                for (Map.Entry<String, String> value : bean.getParameter().entrySet()) {
                    code.append(value.getKey() + "=" + value.getValue() + "&");
                }
                code.delete(code.length() - 1, code.length());
            }
        }
        return code.toString();
    }


    /**
     * 公用二维码跳转功能管理
     */
    public static void goToActivity(final Activity activity, QRCodeBean bean) {
        if (bean != null) {
            if (bean.getFunction().equals(ADD_FRIEND_FUNCHTION)) {
                if (!TextUtils.isEmpty(bean.getParameterValue(ID))) {
                    Long uid = UserAction.getMyInfo().getUid();
                    if (bean.getParameterValue(ID).equals(uid + "")) {
                        Intent intent = new Intent(activity, MyselfInfoActivity.class);
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(activity, UserInfoActivity.class);
                        intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getParameterValue(ID)));
                        activity.startActivity(intent);
                    }
                }
            } else if (bean.getFunction().equals(ADD_GROUP_FUNCHTION)) {
                LogUtil.getLog().e(TAG, "time------->" + DateUtils.timeStamp2Date(Long.valueOf(bean.getParameterValue(TIME)), null));
                if (DateUtils.isPastDue(Long.valueOf(bean.getParameterValue(TIME)))) {
                    ToastUtil.show(activity, "二维码已过期");
                } else {
                    if (!TextUtils.isEmpty(bean.getParameterValue(ID)) && !TextUtils.isEmpty(bean.getParameterValue(UID))) {
                        taskGroupInfo(bean.getParameterValue(ID), bean.getParameterValue(UID), bean.getParameterValue(NICK_NAME), activity);
                    }
                }
            }
        }
    }


    private static void taskGroupInfo(final String gid, final String inviter, final String inviterName, final Activity activity) {
        new MsgAction().groupInfo(gid, true, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {

                if (response.body() != null) {
                    if (response.body().isOk()) {
                        boolean isNot = false;
                        Group bean = response.body().getData();
                        RealmList<MemberUser> users = bean.getUsers();
                        IUser userInfo = new UserAction().getMyInfo();
                        for (MemberUser user : users) {
                            if (userInfo.getUid().longValue() == user.getUid()) {
                                isNot = true;
                            }
                        }

                        if (!isNot) {
                            toAddGourp(gid, inviter, inviterName, activity);
                        } else {
                            EventBus.getDefault().post(new EventExitChat());
                            Intent intent = new Intent(activity, ChatActivity.class);
                            intent.putExtra(ChatActivity.AGM_TOGID, gid);
                            activity.startActivity(intent);
                        }

                    } else {
                        toAddGourp(gid, inviter, inviterName, activity);
                    }
                } else {
                    toAddGourp(gid, inviter, inviterName, activity);
                }
            }
        });
    }

    private static void toAddGourp(String gid, String inviter, String inviterName, Activity activity) {
        Intent intent = new Intent(activity, AddGroupActivity.class);
        intent.putExtra(AddGroupActivity.INVITER, inviter);
        intent.putExtra(AddGroupActivity.GID, gid);
        intent.putExtra(AddGroupActivity.INVITER_NAME, inviterName);
        activity.startActivity(intent);
    }


    public static void toZhifubao(Context mContext, Result result) {
        if (result == null) {
            ToastUtil.showCenter(mContext, "无法识别二维码");
        } else {
            String text = result.getText();
            if (text.contains("qr.alipay.com") || text.contains("QR.ALIPAY.COM")) {
//                openAliPay2Pay(mContext, text);
                ToastUtil.showCenter(mContext, "无法识别二维码");
            } else if (text.contains(DOWNLOAD_APP_URL)) {
                openUri(mContext, text);
            } else {
                QRCodeBean bean = QRCodeManage.getQRCodeBean(mContext, text);
                QRCodeManage.goToActivity((Activity) mContext, bean);
            }

        }
    }


    public static void goToPage(Context mContext, String result) {
        if (result == null) {
            ToastUtil.showCenter(mContext, "无法识别二维码");
        } else {
            if (result.contains("qr.alipay.com") || result.contains("QR.ALIPAY.COM")) {
//                openAliPay2Pay(mContext, result);
                ToastUtil.showCenter(mContext, "无法识别二维码");
            } else if (result.contains(DOWNLOAD_APP_URL)) {
                openUri(mContext, result);
            } else if (result.contains(PC_LOGIN_URL)) {
                httpSweepCodeLoginCommit(result, (Activity) mContext);
            } else {
                QRCodeBean bean = QRCodeManage.getQRCodeBean(mContext, result);
                QRCodeManage.goToActivity((Activity) mContext, bean);
            }
        }
    }


    //判断是否安装支付宝
    private static void openAliPay2Pay(Context mContext, String qrCode) {
        if (openAlipayPayPage(mContext, qrCode)) {

        } else {
            ToastUtil.show(mContext, "请安装支付宝");
        }
    }

    //打开支付宝
    public static boolean openAlipayPayPage(Context context, String qrcode) {
        try {
            qrcode = URLEncoder.encode(qrcode, "utf-8");
        } catch (Exception e) {
        }
        try {
            final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrcode;
            openUri(context, alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private static void openUri(Context context, String s) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
        context.startActivity(intent);
    }


    public static String getTime(int distanceDay) {
        String time = "";
        Date date = new Date(System.currentTimeMillis());
        String changeTime = DateUtils.getOldDateByDay(date, distanceDay, "yyyy-MM-dd HH:mm:ss");
        time = DateUtils.date2TimeStamp(changeTime, "yyyy-MM-dd HH:mm:ss");
        LogUtil.getLog().e(TAG, "生成时间戳------>" + time);
        return time;
    }

    /**
     * 二维码登录 - 扫描认领
     *
     * @param result
     */
    private static void httpSweepCodeLoginCommit(String result, Activity activity) {
        code = result.substring(result.lastIndexOf("/") + 1);//截取参数
        new UserAction().sweepCodeLoginCommit(code, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    ToastUtil.show("扫码成功!");
                    showSweepCodeLoginDialog(activity);
                } else {
                    ToastUtil.show(response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show(t.toString());
            }
        });
    }


    /**
     * 扫码登录弹框(特殊样式/暂不复用/加底部弹出动画效果)
     */
    private static void showSweepCodeLoginDialog(Activity activity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(true);
        final AlertDialog dialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_sweep_code_login, null);
        //初始化控件
        TextView tvExit = dialogView.findViewById(R.id.tv_exit);
        TextView tvSure = dialogView.findViewById(R.id.tv_sure);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);
        ImageView ivCheck = dialogView.findViewById(R.id.iv_check);
        //是否同步
        ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (synck.equals("0")) {
                    ivCheck.setImageResource(R.drawable.ic_check);
                    synck = "1";
                } else {
                    ivCheck.setImageResource(R.drawable.ic_uncheck);
                    synck = "0";
                }
            }
        });
        //退出
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //确认
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new UserAction().sweepCodeLoginSure(code, synck, new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        if (response.body() == null) {
                            return;
                        }
                        if (response.body().isOk()) {
                            ToastUtil.show("登录成功!");
                            //TODO 如果选择了同步，则通知MainActivity同步消息
                            if (synck.equals("1")) {
                                EventBus.getDefault().post(new EventMsgSync(code));
                            }
                        } else {
                            ToastUtil.show(response.body().getMsg());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                        super.onFailure(call, t);
                        ToastUtil.show(t.toString());
                    }
                });
            }
        });
        //取消
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new UserAction().sweepCodeLoginCancel(code, new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        if (response.body() == null) {
                            return;
                        }
                        if (response.body().isOk()) {
                            LogUtil.getLog().d(TAG, "取消登录成功!");
                        } else {
                            ToastUtil.show(response.body().getMsg());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                        super.onFailure(call, t);
                        ToastUtil.show(t.toString());
                    }
                });
            }
        });
        //展示界面
        dialog.show();
        //解决圆角shape背景无效问题
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        //相关配置
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        WindowManager manager = window.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        //设置宽高，占满全屏
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(dialogView);
    }
}
