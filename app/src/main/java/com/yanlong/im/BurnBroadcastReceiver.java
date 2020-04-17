package com.yanlong.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/25 0025
 * @description
 */
public class BurnBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.yanlong.im.burn.action")){
            if(MyAppLication.INSTANCE().repository!=null)MyAppLication.INSTANCE().repository.notifyBurnQuene();
        }
    }
}
