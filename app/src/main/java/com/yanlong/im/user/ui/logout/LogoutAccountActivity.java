package com.yanlong.im.user.ui.logout;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityLogoutBinding;

import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogCommon2;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/5/20
 * Description 注销常信账号
 */
public class LogoutAccountActivity extends AppActivity {

    private TextView tvConfirm;
    private ActivityLogoutBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_logout);
        ui.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
    }

    private void showConfirmDialog() {
        DialogCommon dialogConfirm = new DialogCommon(this);
        dialogConfirm.setTitleAndSure(false, true)
                .setLeft("取消")
                .setRight("确定")
                .setListener(new DialogCommon.IDialogListener() {
                    @Override
                    public void onSure() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void showCommitedDialog() {
        DialogCommon2 dialogCommited = new DialogCommon2(this);
        dialogCommited.setButtonTxt("确定")
                .setTitle("已经提交注册申请")
                .setContent("工作人员将在30天内处理您的申请并删除账号下所有数据。在此期间，请不要登录常信。", false)
                .setListener(new DialogCommon2.IDialogListener() {
                    @Override
                    public void onClick() {

                    }
                });
    }
}
