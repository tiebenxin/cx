package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/***
 * 投诉
 */
public class ComplaintActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;



    //自动寻找控件
    private void findViews(){
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar=headView.getActionbar();
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
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
            holder.txtComplaintTitle.setText("xxxx");

        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_complaint, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private TextView txtComplaintTitle;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                txtComplaintTitle = (TextView) convertView.findViewById(R.id.txt_complaint_title);
            }

        }
    }

}
