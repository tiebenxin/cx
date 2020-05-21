package com.yanlong.im.user.ui.logout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/5/20
 * Description 注销常信账号
 */
public class LogoutAccountActivity extends AppActivity {

    private TextView tvConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
    }

    private void showConfirmDialog(){
        DialogCommon dialogConfirm = new DialogCommon(this);
        dialogConfirm.setTitleAndSure(false,true)
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
}
