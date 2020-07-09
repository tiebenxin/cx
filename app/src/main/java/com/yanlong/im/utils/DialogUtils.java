package com.yanlong.im.utils;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hm.cxpay.global.PayEnvironment;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.ui.freeze.SealAccountActivity;

import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetIntrtceptor;
import net.cb.cb.library.utils.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Headers;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/7/6
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class DialogUtils {

    private static DialogUtils INSTANCE;

    public static DialogUtils instance() {
        if (INSTANCE == null) {
            INSTANCE = new DialogUtils();
        }
        return INSTANCE;
    }

    /**
     * 封号弹框
     *
     * @param context
     */
    public void sealAccountDilaog(Context context, TokenBean tokenBean) {
        setToken(tokenBean);
        DialogCommon dialogCommon = new DialogCommon(context);
        dialogCommon.setCanceledOnTouchOutside(false);
        dialogCommon.setTitleAndSure(true, true)
                .setTitle("提示")
                .setContent("该常信账号存在违规行为已被限制登录，如需继续使用，请点击确定查看违规详情。" +
                        "你可以在详情页面按相关指引进行操作“申请解封”或者申请临时登录。", false)
                .setListener(new DialogCommon.IDialogListener() {
                    @Override
                    public void onSure() {
                        Postcard postcard = ARouter.getInstance().build(SealAccountActivity.path);
                        postcard.withBoolean(Constants.STATUS, tokenBean.getAppealState() == 1 ? true : false);
                        postcard.navigation();
                    }

                    @Override
                    public void onCancel() {
                        cleanInfo();
                        dialogCommon.dismiss();
                        EventBus.getDefault().post(new EventFactory.ExitActivityEvent());
                    }
                });
        dialogCommon.show();
    }

    /***
     * 清理信息
     */
    public void cleanInfo() {
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).clear();
        LogUtil.getLog().i("Token", "清除token");
    }

    public void setToken(TokenBean token) {
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).save2Json(token);
        NetIntrtceptor.headers = Headers.of("X-Access-Token", token.getAccessToken());
        PayEnvironment.getInstance().setToken(token.getAccessToken());
        LogUtil.getLog().i("设置token", "--token=" + token.getAccessToken());
    }
}
