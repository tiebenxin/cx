package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.utils.PhoneListUtil;

import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.CheckPermissionUtils;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.List;

public class FriendMatchActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewSearch;
    private net.cb.cb.library.view.MultiListView mtListView;
    private View viewType;
    private PhoneListUtil phoneListUtil = new PhoneListUtil();

    //自动寻找控件
    private void findViews(){
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar=headView.getActionbar();
        viewSearch = (LinearLayout) findViewById(R.id.view_search);
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
        viewType = (View) findViewById(R.id.view_type);
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

        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();



     phoneListUtil.getPhones(this, new PhoneListUtil.Event() {
          @Override
          public void onList(List<PhoneListUtil.PhoneBean> list) {
                Log.d("TAG", "initEvent: "+list.size());
          }
      });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       phoneListUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_match);
        findViews();
        initEvent();
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return null==null?10:0;
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            holder.txtType.setText("A");
            holder.imgHead.setImageURI(Uri.parse("https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D220/sign=63b408bba11ea8d38e227306a70a30cf/0824ab18972bd40765b46cfd7c899e510fb309ba.jpg"));
            holder.txtName.setText("提莫队长");

            if (position > 3 && position < 5) {
                holder.viewType.setVisibility(View.GONE);
            }

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                }});

        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_friend_match, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout viewType;
            private TextView txtType;
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private TextView txtName;
            private Button btnAdd;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewType = (LinearLayout) convertView.findViewById(R.id.view_type);
                txtType = (TextView) convertView.findViewById(R.id.txt_type);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
                txtName = (TextView) convertView.findViewById(R.id.txt_name);
                btnAdd = (Button) convertView.findViewById(R.id.btn_add);
            }

        }
    }

}
