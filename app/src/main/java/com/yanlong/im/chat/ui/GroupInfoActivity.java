package com.yanlong.im.chat.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private android.support.v7.widget.RecyclerView topListView;
    private ImageView btnAdd;
    private ImageView btnRm;
    private LinearLayout viewGroupName;
    private TextView txtGroupName;
    private LinearLayout viewGroupNick;
    private TextView txtGroupNick;
    private LinearLayout viewGroupQr;
    private LinearLayout viewGroupNote;
    private TextView txtGroupNote;
    private LinearLayout viewLog;
    private LinearLayout viewTop;
    private CheckBox ckTop;
    private LinearLayout viewDisturb;
    private CheckBox ckDisturb;
    private LinearLayout viewGroupSave;
    private CheckBox ckGroupSave;
    private Button btnDel;



    //自动寻找控件
    private void findViews(){
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar=headView.getActionbar();
        topListView = (android.support.v7.widget.RecyclerView) findViewById(R.id.topListView);
        btnAdd = (ImageView) findViewById(R.id.btn_add);
        btnRm = (ImageView) findViewById(R.id.btn_rm);
        viewGroupName = (LinearLayout) findViewById(R.id.view_group_name);
        txtGroupName = (TextView) findViewById(R.id.txt_group_name);
        viewGroupNick = (LinearLayout) findViewById(R.id.view_group_nick);
        txtGroupNick = (TextView) findViewById(R.id.txt_group_nick);
        viewGroupQr = (LinearLayout) findViewById(R.id.view_group_qr);
        viewGroupNote = (LinearLayout) findViewById(R.id.view_group_note);
        txtGroupNote = (TextView) findViewById(R.id.txt_group_note);
        viewLog = (LinearLayout) findViewById(R.id.view_log);
        viewTop = (LinearLayout) findViewById(R.id.view_top);
        ckTop = (CheckBox) findViewById(R.id.ck_top);
        viewDisturb = (LinearLayout) findViewById(R.id.view_disturb);
        ckDisturb = (CheckBox) findViewById(R.id.ck_disturb);
        viewGroupSave = (LinearLayout) findViewById(R.id.view_group_save);
        ckGroupSave = (CheckBox) findViewById(R.id.ck_group_save);
        btnDel = (Button) findViewById(R.id.btn_del);
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

        btnDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }});

        //顶部处理
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        topListView.setLayoutManager(linearLayoutManager);
        topListView.setAdapter(new RecyclerViewTopAdapter());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        findViews();
        initEvent();
    }




    private List<String> listDataTop = new ArrayList<>();

    //自动生成RecyclerViewAdapter
    class RecyclerViewTopAdapter extends RecyclerView.Adapter<RecyclerViewTopAdapter.RCViewTopHolder> {

        @Override
        public int getItemCount() {
           // return listDataTop == null ? 0 : listDataTop.size();
            return 10;
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewTopHolder holder, int position) {
            //listDataTop.get(position)
            holder.imgHead.setImageURI(Uri.parse("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3853320162,146397336&fm=173&app=25&f=JPEG?w=640&h=360&s=22A066A44A5674C2528F1F7603000054"));
        }


        //自动寻找ViewHold
        @Override
        public RCViewTopHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewTopHolder holder = new RCViewTopHolder(inflater.inflate(R.layout.item_group_create_top, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewTopHolder extends RecyclerView.ViewHolder {
            private com.facebook.drawee.view.SimpleDraweeView imgHead;

            //自动寻找ViewHold
            public RCViewTopHolder(View convertView) {
                super(convertView);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
            }

        }
    }

}
