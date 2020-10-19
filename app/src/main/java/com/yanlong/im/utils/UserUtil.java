package com.yanlong.im.utils;

import android.text.TextUtils;

import com.example.nim_lib.config.Preferences;
import com.yanlong.im.BuildConfig;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.FriendInfoBean;
import com.yanlong.im.user.bean.PhoneBean;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-10-08
 * @updateAuthor
 * @updateDate
 * @description 用户处理类
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class UserUtil {

    /**
     * 获取用户的首字母列表
     *
     * @param userList 用户列表
     * @return
     */
    public static List<String> userParseString(List<UserInfo> userList) {
        List<String> list = new ArrayList<>();
        try {
            if (userList != null) {
                for (int i = 0; i < userList.size(); i++) {
                    list.add(userList.get(i).getTag());
                }
            }
        } catch (Exception e) {

        }
        return list;

    }

    /**
     * 获取用户的首字母列表
     *
     * @param friendList 用户列表
     * @return
     */
    public static List<String> friendParseString(List<FriendInfoBean> friendList) {
        List<String> list = new ArrayList<>();
        try {
            if (friendList != null) {
                for (int i = 0; i < friendList.size(); i++) {
                    list.add(friendList.get(i).getTag());
                }
            }
        } catch (Exception e) {

        }
        return list;

    }

    /**
     * 是否是常信客服、常信小助手
     *
     * @return
     */
    public static boolean isSystemUser(Long toUId) {
        if (toUId == null) {
            return false;
        }
        if (Constants.CX_HELPER_UID.equals(toUId) || Constants.CX888_UID.equals(toUId) || Constants.CX999_UID.equals(toUId) || Constants.CX_BALANCE_UID.equals(toUId)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是禁止发送消息的系统用户：常信小助手，零钱小助手
     *
     * @return
     */
    public static boolean isBanSendUser(Long toUId) {
        if (toUId == null) {
            return false;
        }
        if (Constants.CX_HELPER_UID.equals(toUId) || Constants.CX_BALANCE_UID.equals(toUId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取用户状态 0正常 1封号
     *
     * @return
     */
    public static int getUserStatus() {
        int status = 0;
        try {
            if (UserAction.getMyInfo() != null) {
                status = SpUtil.getSpUtil().getSPValue(Preferences.USER_STATUS + UserAction.getMyInfo().getUid() + BuildConfig.BUILD_TYPE, 0);
            }
        } catch (Exception e) {

        } finally {
            return status;
        }
    }

    /**
     * 保存用户状态 是否被封号的状态
     *
     * @param uid
     * @param lockUser 0正常 1封号
     */
    public static void saveUserStatus(Long uid, int lockUser) {
        SpUtil.getSpUtil().putSPValue(Preferences.USER_STATUS + uid + BuildConfig.BUILD_TYPE, lockUser);
    }

    public static boolean getUserStatus(int lockedstatus) {
        boolean isLockedstatus = false;
        if (lockedstatus == CoreEnum.EUserType.DISABLE) {
            isLockedstatus = true;
        }
        return isLockedstatus;
    }

    /**
     * 获取新增加的联系人
     *
     * @param newList
     * @param oldList
     * @return
     */
    public static List<String> getNewContentsPhone(List<PhoneBean> newList, List<PhoneBean> oldList) {
        List<String> tempList = new ArrayList<>();
        boolean isFlg;
        if (newList != null && oldList != null) {
            for (PhoneBean newBean : newList) {
                isFlg = false;
                for (PhoneBean oldBean : oldList) {
                    if (!TextUtils.isEmpty(newBean.getPhone()) && newBean.getPhone().equals(oldBean.getPhone())) {
                        isFlg = true;
                        break;
                    }
                }
                if (!isFlg) {
                    tempList.add(newBean.getPhone());
                }
            }
        }
        return tempList;
    }

    /**
     * 获取删除的联系人
     *
     * @param newList
     * @param oldList
     * @return
     */
    public static List<String> getDeleteContentsPhone(List<PhoneBean> newList, List<PhoneBean> oldList) {
        List<String> tempList = new ArrayList<>();
        boolean isFlg;
        if (newList != null && oldList != null) {
            for (PhoneBean oldBean : oldList) {
                isFlg = false;
                for (PhoneBean newBean : newList) {
                    if (!TextUtils.isEmpty(oldBean.getPhone()) && oldBean.getPhone().equals(newBean.getPhone())) {
                        isFlg = true;
                        break;
                    }
                }
                if (!isFlg) {
                    tempList.add(oldBean.getPhone());
                }
            }
        }
        return tempList;
    }

    // stat : 0:正常|1:待同意|2:黑名单|9:系统用户，如小助手
    public static int getUserType(int stat) {
        switch (stat) {
            case 0:
                return ChatEnum.EUserType.FRIEND;
            case 2:
                return ChatEnum.EUserType.BLACK;
            case 9:
                return ChatEnum.EUserType.ASSISTANT;
            default:
                return ChatEnum.EUserType.STRANGE;
        }
    }
}
