package com.yanlong.im.user.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ui.ChatActivity;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/***
 * 资料界面
 */
public class UserInfoActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewHead;
    private com.facebook.drawee.view.SimpleDraweeView imgHead;
    private TextView txtMkname;
    private TextView txtNkname;
    private TextView txtPrNo;
    private LinearLayout viewMkname;
    private LinearLayout viewBlack;
    private LinearLayout viewDel;
    private LinearLayout viewComplaint;
    private Button btnMsg;


    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewHead = (LinearLayout) findViewById(R.id.view_head);
        imgHead = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.img_head);
        txtMkname = (TextView) findViewById(R.id.txt_mkname);
        txtNkname = (TextView) findViewById(R.id.txt_nkname);
        txtPrNo = (TextView) findViewById(R.id.txt_pr_no);
        viewMkname = (LinearLayout) findViewById(R.id.view_mkname);
        viewBlack = (LinearLayout) findViewById(R.id.view_black);
        viewDel = (LinearLayout) findViewById(R.id.view_del);
        viewComplaint = (LinearLayout) findViewById(R.id.view_complaint);
        btnMsg = (Button) findViewById(R.id.btn_msg);
    }


    //自动生成的控件事件
    private void initEvent() {
        imgHead.setImageURI(Uri.parse(""));
        txtMkname.setText("备注名");
        txtPrNo.append("wow");
        txtNkname.append("nike");
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        btnMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                go(ChatActivity.class);

            }
        });
        viewBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtil.show(getContext(), "Black");
            }
        });

        viewComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(ComplaintActivity.class);
            }
        });
        viewDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getContext(), "del");
            }
        });
        viewMkname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getContext(), "mkname");
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        findViews();
        initEvent();
    }
}
