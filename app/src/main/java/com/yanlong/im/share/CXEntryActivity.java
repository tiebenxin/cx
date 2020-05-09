package com.yanlong.im.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.tcp.TcpConnection;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.ui.LoginActivity;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;

import java.util.List;

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
        //app处于前台的时候，连接并未断开，这时候，保持连接即可
        if (SocketUtil.getSocketUtil().isRun()) {
            SocketUtil.getSocketUtil().setKeepConnect(true);
        }
        super.onCreate(savedInstanceState);
//        showLoadingDialog();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (Intent.ACTION_SEND.equals(action)) {
                    mode = ChatEnum.EForwardMode.SYS_SEND;
                    if (!isSupportType(type)) {
                        ToastUtil.show(this, "分享失败，单文件分享仅支持照片，文件格式");
                        finish();
                        return;
                    }
                } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
                    mode = ChatEnum.EForwardMode.SYS_SEND_MULTI;
                    if (!isSupportMultiType(type)) {
                        ToastUtil.show(this, "分享失败，多文件分享仅支持照片格式");
                        finish();
                        return;
                    } else {
                        List<Uri> uriList = extras.getParcelableArrayList(Intent.EXTRA_STREAM);
                        if (uriList != null && uriList.size() > 9) {
                            ToastUtil.show(this, "分享失败，暂不支持分享超过9张图片给朋友");
                            finish();
                            return;
                        }
                    }
                } else {
                    mode = ChatEnum.EForwardMode.SHARE;
                }
                checkApp(extras, type);
            } else {
                ToastUtil.show(this, "分享失败，无分享数据");
                finish();
                return;
            }
        } else {
            ToastUtil.show(this, "分享失败，无分享数据");
            finish();
            return;
        }
    }

    private void goActivity(Bundle extras, int mode, String type) {
        if (checkTokenValid()) {
            startChatServer();
            Intent intentShare = MsgForwardActivity.newIntent(this, mode, extras);
            intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (!TextUtils.isEmpty(type)) {
                intentShare.setType(type);
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.getLog().i("跟踪--CXEntry", "onDestroy");
        TcpConnection.getInstance(AppConfig.getContext()).updateFrom(TcpConnection.EFrom.DEFAULT);
    }

    private void startChatServer() {
        // 启动聊天服务
        if (!SocketUtil.getSocketUtil().isRun()) {
            return;
        }
        TcpConnection.getInstance(AppConfig.getContext()).startConnect(TcpConnection.EFrom.OTHER);
    }

    public boolean checkTokenValid() {
        boolean result = false;
        TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
        if (token != null) {
            if (!token.isTokenValid(uid)) {
                result = false;
            } else {
                //初始化http请求中token
                new UserAction().login4tokenNotNet(token);
                result = true;
            }
        } else {
            result = false;
        }
        return result;
    }

    private void checkApp(Bundle extras, String type) {
        if (true) {
            extras.putString("app_name", "酷玩");
            extras.putString("app_icon", "");
            goActivity(extras, mode, type);
        }
    }

    private boolean isSupportType(String type) {
        if (!TextUtils.isEmpty(type)) {
            if (type.startsWith("image/") || type.startsWith("text/") || type.startsWith("application/")) {
                return true;
            }
        }
        return false;
    }

    private boolean isSupportMultiType(String type) {
        if (!TextUtils.isEmpty(type)) {
            if (type.startsWith("image/")) {
                return true;
            }
        }
        return false;
    }

}
