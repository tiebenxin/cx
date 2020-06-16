package com.hm.cxpay.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hm.cxpay.ui.YiBaoWebActivity;

import net.cb.cb.library.dialog.DialogLoadingProgress;

import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.WebPageActivity;

/**
 * @author Liszt
 * @date 2019/11/27
 * Description
 */
public class BasePayActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void goWebActivity(Context context, String webUrl) {
        Intent intent = new Intent(context, YiBaoWebActivity.class);
        intent.putExtra(YiBaoWebActivity.AGM_URL, webUrl);
        startActivity(intent);
    }
}
