package com.yanlong.im.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.example.nim_lib.config.Preferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.FriendInfoBean;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：通用工具类
 * @Date：2020/4/7
 * @by zjy
 * @备注： 1 List拆分
 * 2 类型转换-protobuf转换为本地自定义类型
 */

public class CommonUtils {


    /**
     * List拆分
     *
     * @param source
     * @param len    集合的长度
     * @备注 按指定大小，分隔集合，将集合按规定个数分为多个部分
     */
    public static <T> List<List<T>> subWithLen(List<T> source, int len) {
        if (source == null || source.size() == 0 || len < 1) {
            return null;
        }

        List<List<T>> result = new ArrayList<List<T>>();
        int count = (source.size() + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> value = null;
            if ((i + 1) * len < source.size()) {
                value = source.subList(i * len, (i + 1) * len);
            } else {
                value = source.subList(i * len, source.size());
            }
            result.add(value);
        }
        return result;
    }

    /**
     * 类型转换-protobuf转换为本地自定义类型
     *
     * @param type 本地类型 ChatEnum.EMessageType
     * @return
     */
    public static int transformMsgType(int type) {
        MsgBean.MessageType messageType = MsgBean.MessageType.valueOf(type);
        return SocketData.getEMsgType(messageType);
    }

    /**
     * 保存通讯录匹配数据
     *
     * @param uid
     * @param phone
     */
    public static void saveFriendInfo(Long uid, String phone) {
        String friends = SpUtil.getSpUtil().getSPValue(Preferences.RECENT_FRIENDS_UIDS + UserAction.getMyId(), "");
        List<FriendInfoBean> list = new ArrayList<>();
        Gson gson = new Gson();
        if (TextUtils.isEmpty(friends)) {
            FriendInfoBean friendInfoBean = new FriendInfoBean();
            friendInfoBean.setUid(uid);
            friendInfoBean.setPhoneremark(phone);
            friendInfoBean.setCreateTime(System.currentTimeMillis());
            list.add(friendInfoBean);
            SpUtil.getSpUtil().putSPValue(Preferences.RECENT_FRIENDS_UIDS + UserAction.getMyId(), gson.toJson(list));
        } else {
            list.addAll(gson.fromJson(friends, new TypeToken<List<FriendInfoBean>>() {
            }.getType()));
            boolean isExist = false;
            // 判断是否存在
            for (FriendInfoBean friendInfoBean : list) {
                if (!TextUtils.isEmpty(phone) && phone.equals(friendInfoBean.getPhoneremark())) {
                    friendInfoBean.setCreateTime(System.currentTimeMillis());
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                FriendInfoBean friendInfoBean = new FriendInfoBean();
                friendInfoBean.setUid(uid);
                friendInfoBean.setPhoneremark(phone);
                friendInfoBean.setCreateTime(System.currentTimeMillis());
                list.add(friendInfoBean);
                SpUtil.getSpUtil().putSPValue(Preferences.RECENT_FRIENDS_UIDS + UserAction.getMyId(), gson.toJson(list));
            } else {
                SpUtil.getSpUtil().putSPValue(Preferences.RECENT_FRIENDS_UIDS + UserAction.getMyId(), gson.toJson(list));
            }
        }
    }

    /**
     * 用于判断红点数
     *
     * @param phone
     */
    public static void saveFriendInfo(String phone) {
        String friends = SpUtil.getSpUtil().getSPValue(Preferences.RECENT_FRIENDS_RED_NUMBER + UserAction.getMyId(), "");
        List<FriendInfoBean> list = new ArrayList<>();
        Gson gson = new Gson();
        if (TextUtils.isEmpty(friends)) {
            FriendInfoBean friendInfoBean = new FriendInfoBean();
            friendInfoBean.setPhone(phone);
            friendInfoBean.setCreateTime(System.currentTimeMillis());
            list.add(friendInfoBean);
            SpUtil.getSpUtil().putSPValue(Preferences.RECENT_FRIENDS_RED_NUMBER + UserAction.getMyId(), gson.toJson(list));
        } else {
            list.addAll(gson.fromJson(friends, new TypeToken<List<FriendInfoBean>>() {
            }.getType()));
            FriendInfoBean friendInfoBean = new FriendInfoBean();
            friendInfoBean.setPhone(phone);
            friendInfoBean.setCreateTime(System.currentTimeMillis());
            list.add(friendInfoBean);
            SpUtil.getSpUtil().putSPValue(Preferences.RECENT_FRIENDS_RED_NUMBER + UserAction.getMyId(), gson.toJson(list));
        }
    }

    /**
     * 获取展示红点的手机号
     *
     * @return
     */
    public static List<FriendInfoBean> getRedFriendInfo() {
        String friends = SpUtil.getSpUtil().getSPValue(Preferences.RECENT_FRIENDS_RED_NUMBER + UserAction.getMyId(), "");
        List<FriendInfoBean> list = new ArrayList<>();
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(friends)) {
            list.addAll(gson.fromJson(friends, new TypeToken<List<FriendInfoBean>>() {
            }.getType()));
        }
        return list;
    }

    /**
     * 方法描述：文本设置多颜色
     *
     * @param var1     字符串1
     * @param var2     字符串2
     * @param color1   颜色1
     * @param color2   颜色2
     * @param tv       控件
     * @param mContext 上下文
     */
    public static void setTextColor(String var1, String var2, int color1, int color2, TextView tv, Context mContext) {
        String var = var1 + var2;
        SpannableStringBuilder span = new SpannableStringBuilder(var);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, color1)), 0, var1.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//前包括后不包括
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, color2)), var1.length(), var.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(span);
    }

}
