package com.yanlong.im.chat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.WebPageActivity;

public class GroupRobotActivity extends AppActivity {
    public static final String AGM_SHOW_TYPE = "SHOW_TYPE";
    public static final int AGM_SHOW_TYPE_ADD =1; //待添加
    public static final String AGM_RID = "RID";

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewInfo;
    private com.facebook.drawee.view.SimpleDraweeView imgInfoIcon;
    private TextView txtInfoTitle;
    private Button btnInfoAdd;
    private Button btnInfoDel;
    private Button btnInfoChange;
    private TextView txtInfoMore;
    private TextView txtInfoNote;
    private Button btnConfig;
    private LinearLayout viewAdd;
    private Button btnAdd;


    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewInfo = (LinearLayout) findViewById(R.id.view_info);
        imgInfoIcon = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.img_info_icon);
        txtInfoTitle = (TextView) findViewById(R.id.txt_info_title);
        btnInfoAdd = (Button) findViewById(R.id.btn_info_add);
        btnInfoDel = (Button) findViewById(R.id.btn_info_del);
        btnInfoChange = (Button) findViewById(R.id.btn_info_change);
        txtInfoMore = (TextView) findViewById(R.id.txt_info_more);
        txtInfoNote = (TextView) findViewById(R.id.txt_info_note);
        btnConfig = (Button) findViewById(R.id.btn_config);
        viewAdd = (LinearLayout) findViewById(R.id.view_add);
        btnAdd = (Button) findViewById(R.id.btn_add);
    }


    //自动生成的控件事件
    private void initEvent() {


        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        btnInfoAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        btnInfoDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });



        btnConfig.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(getContext(), WebPageActivity.class).putExtra(WebPageActivity.AGM_URL, "http://www.techweb.com.cn/ucweb/news/id/2742774"));

            }
        });

        View.OnClickListener  listener;
        btnInfoChange.setOnClickListener(listener=new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GroupRobotSelecActivity.class));
            }
        });
        btnAdd.setOnClickListener(listener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_robot);
        findViews();
        initEvent();
        initData();
    }

    private void initData() {

        if (true) { //有添加
            viewAdd.setVisibility(View.GONE);
            viewInfo.setVisibility(View.VISIBLE);
        } else {
            viewAdd.setVisibility(View.VISIBLE);
            viewInfo.setVisibility(View.GONE);

        }


        if (false) {//查看详情界面
            btnInfoAdd.setVisibility(View.VISIBLE);
            btnInfoDel.setVisibility(View.GONE);
            btnInfoChange.setVisibility(View.GONE);
        } else {
            btnInfoAdd.setVisibility(View.GONE);
            btnInfoDel.setVisibility(View.VISIBLE);
            btnInfoChange.setVisibility(View.VISIBLE);
        }

    }


}
