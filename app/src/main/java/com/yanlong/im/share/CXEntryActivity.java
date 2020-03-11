package com.yanlong.im.share;

import android.content.Intent;
import android.os.Bundle;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.ui.LoginActivity;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/3/7
 * Description  外部数据承接
 */
public class CXEntryActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        showLoadingDialog();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int mode;
                if (Intent.ACTION_SEND.equals(action)) {
                    mode = ChatEnum.EForwardMode.SYS_SEND;
                } else {
                    mode = ChatEnum.EForwardMode.SHARE;
                }
                goActivity(extras, mode);
            } else {

            }
        } else {

        }
    }

    private void goActivity(Bundle extras, int mode) {
        if (checkTokenValid()) {
            startChatServer();
            Intent intentShare = MsgForwardActivity.newIntent(this, mode, extras);
            startActivity(intentShare);
            finish();
        } else {
            startActivity(new Intent(CXEntryActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissLoadingDialog();
    }

    private void startChatServer() {
        // 启动聊天服务
        startService(new Intent(getContext(), ChatServer.class));
    }

    public boolean checkTokenValid() {
        boolean result = false;
        TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
        if (token != null) {
            if (!token.isTokenValid(uid)) {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = false;
        }
        return result;
    }

}
