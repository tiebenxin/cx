package com.yanlong.im.notify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.umeng.message.UmengNotifyClickActivity;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;

import org.android.agoo.common.AgooConstants;

public class PushToActivity extends UmengNotifyClickActivity {

    private static String TAG = PushToActivity.class.getName();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_push_to);

    }


    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i(TAG, body);
        Intent toIntent = new Intent(this, MainActivity.class);
        startActivity(toIntent);
        finish();

    }
}
