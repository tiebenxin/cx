package com.yanlong.im.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.GroupJoinBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.MyselfInfoActivity;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.bean.QRCodeBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class QRCodeManage {

    //YLIM://ADDFRIEND?id=xxx
    //YLIM://ADDGROUP?id=xxx
    public static final String HEAD = "YLIM:"; //二维码头部
    public static final String ID = "id";

    public static final String ADD_FRIEND_FUNCHTION = "ADDFRIEND"; //添加好友
    public static final String ADD_GROUP_FUNCHTION = "ADDGROUP"; //添加群

    /**
     * 扫描二维码转换bean
     *
     * @param QRCode 二维码字符串
     * @return 二维码bean
     */
    public static QRCodeBean getQRCodeBean(Context context, String QRCode) {
        QRCodeBean bean = null;
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
                        Map<String, String> parameters = new HashMap<>();
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
                if (!TextUtils.isEmpty(bean.getParameterValue(ID))) {
                    UserInfo userInfo = UserAction.getMyInfo();
                    Long uid = userInfo.getUid();
                    String name = userInfo.getName();

                    new MsgAction().joinGroup(Long.valueOf(bean.getParameterValue(ID)), uid, name, new CallBack<ReturnBean<GroupJoinBean>>() {
                        @Override
                        public void onResponse(Call<ReturnBean<GroupJoinBean>> call, Response<ReturnBean<GroupJoinBean>> response) {
                            if(response.body() == null){
                                ToastUtil.show(activity,"加群失败");
                                return;
                            }
                            ToastUtil.show(activity,response.body().getMsg());
                        }
                    });
                }
            }
        }
    }

}
