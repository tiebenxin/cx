package com.yanlong.im.share;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

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
    public static final int REQUEST_SHARE = 1 << 1;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        showLoadingDialog();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (Intent.ACTION_SEND.equals(action)) {
                    mode = ChatEnum.EForwardMode.SYS_SEND;
                } else {
                    mode = ChatEnum.EForwardMode.SHARE;
                }
                checkApp(extras);
            } else {

            }
        } else {

        }
    }

    private void goActivity(Bundle extras, int mode) {
        if (checkTokenValid()) {
            startChatServer();
            Intent intentShare = MsgForwardActivity.newIntent(this, mode, extras);
            intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (mode == ChatEnum.EForwardMode.SHARE) {
                startActivityForResult(intentShare, REQUEST_SHARE);
            } else {
                startActivity(intentShare);
            }
            finish();
        } else {
            startActivity(new Intent(CXEntryActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SHARE) {
                finish();
            }
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

    private void checkApp(Bundle extras) {
        if (true) {
            extras.putString("app_name","酷玩");
            extras.putString("app_icon","");
            goActivity(extras, mode);
        }
    }

}
