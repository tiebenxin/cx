package com.yanlong.im.chat.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.AppActivity;

/**
 *
 */
public class ChatActionActivity extends AppActivity {
    private com.facebook.drawee.view.SimpleDraweeView imgHead;
    private TextView txtName;
    private TextView txtMsg;
    private LinearLayout viewNo;
    private LinearLayout viewYes;
private  Vibrator vibrator;

    //自动寻找控件
    private void findViews() {
        imgHead = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.img_head);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtMsg = (TextView) findViewById(R.id.txt_msg);
        viewNo = (LinearLayout) findViewById(R.id.view_no);
        viewYes = (LinearLayout) findViewById(R.id.view_yes);
    }


    //自动生成的控件事件
    private void initEvent() {
        imgHead.setImageURI(Uri.parse("http://wx1.sinaimg.cn/mw600/005YuSWBly1g1xvfqiu1dj30ge085aae.jpg"));
        txtName.setText("HUAWEI");
        txtMsg.setText("50倍超长焦摄像头");
        viewNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                go(ChatActivity.class);
                finish();
            }
        });

        //振动
         vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(2000);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_action);
        findViews();
        initEvent();
    }
}
