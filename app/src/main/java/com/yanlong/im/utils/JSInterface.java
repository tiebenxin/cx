package com.yanlong.im.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.yanlong.im.user.ui.FeedbackActivity;

public class JSInterface {
    private Context mContext;
    public JSInterface(Context context){
        this.mContext=context;
    }

    @JavascriptInterface
    public void callAndroidMethod(){
        Log.e("TAG","callMETHOD"+"");
        mContext.startActivity(new Intent(mContext, FeedbackActivity.class));
    }
}
