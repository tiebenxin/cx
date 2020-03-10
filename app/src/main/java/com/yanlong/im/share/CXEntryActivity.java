package com.yanlong.im.share;

import android.content.Intent;
import android.os.Bundle;

import com.yanlong.im.chat.ui.forward.MsgForwardActivity;

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
        showLoadingDialog();
        Intent intent = getIntent();
        if (intent != null) {
            startActivity(new Intent(this, MsgForwardActivity.class));
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
    }
}
