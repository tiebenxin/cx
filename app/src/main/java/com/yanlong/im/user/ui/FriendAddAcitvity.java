package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.TouchUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/***
 * 添加朋友
 */
public class FriendAddAcitvity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewSearch;
    private LinearLayout viewMatch;
    private LinearLayout viewQr;
    private LinearLayout viewWc;



    //自动寻找控件
    private void findViews(){
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar=headView.getActionbar();
        viewSearch = (LinearLayout) findViewById(R.id.view_search);
        viewMatch = (LinearLayout) findViewById(R.id.view_match);
        viewQr = (LinearLayout) findViewById(R.id.view_qr);
        viewWc = (LinearLayout) findViewById(R.id.view_wc);
    }



    //自动生成的控件事件
    private void initEvent(){
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed(); }
            @Override
            public void onRight() {

            } });

        viewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                go(FriendMatchActivity.class);
            }
        });

        viewQr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // ToastUtil.show(getContext(),"erwm");
                    }
                });
        viewWc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ToastUtil.show(getContext(),"wx");
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        findViews();
        initEvent();
    }



}
