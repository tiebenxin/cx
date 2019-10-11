package com.example.nim_lib.ui.activity;

import android.os.Bundle;

import com.example.nim_lib.R;

import net.cb.cb.library.view.AppActivity;

/**
 * @anthor Liszt
 * @data 2019/10/9
 * Description 音视频通话被呼叫等待页面
 */
public class CallWaitActivity extends AppActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_wait);
    }
}
