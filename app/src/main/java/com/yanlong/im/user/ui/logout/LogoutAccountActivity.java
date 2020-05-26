package com.yanlong.im.user.ui.logout;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.jrmf360.tools.utils.ThreadUtil;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityLogoutBinding;

import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogCommon2;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/5/20
 * Description 注销常信账号
 */
public class LogoutAccountActivity extends AppActivity {
    private ActivityLogoutBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_logout);
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        ui.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
    }

    private void showConfirmDialog() {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon dialogConfirm = new DialogCommon(LogoutAccountActivity.this);
                dialogConfirm.setTitleAndSure(false, true)
                        .setLeft("取消")
                        .setRight("确定")
                        .setContent("确定要注销账号吗？", true)
                        .setListener(new DialogCommon.IDialogListener() {
                            @Override
                            public void onSure() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        }).show();
            }
        });

    }

    private void showCommitedDialog() {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon2 dialogCommited = new DialogCommon2(LogoutAccountActivity.this);
                dialogCommited.setButtonTxt("确定")
                        .setTitle("已经提交注册申请")
                        .setContent("工作人员将在30天内处理您的申请并删除账号下所有数据。在此期间，请不要登录常信。", false)
                        .setListener(new DialogCommon2.IDialogListener() {
                            @Override
                            public void onClick() {

                            }
                        }).show();
            }
        });
    }
}
