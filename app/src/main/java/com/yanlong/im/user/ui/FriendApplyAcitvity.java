package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/***
 * 新的朋友
 */
public class FriendApplyAcitvity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;


    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
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

        mtListView.init(new RecyclerViewAdapter());
        //test
        mtListView.getLoadView().setStateNormal();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_apply);
        findViews();
        initEvent();
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return null == null ? 10 : 0;
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            holder.txtName.setText("提莫队长");
            holder.imgHead.setImageURI("https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D500/sign=6346256a71310a55c024def487444387/7af40ad162d9f2d3c35c9b76a1ec8a136227ccde.jpg");

            holder.txtInfo.setText("只会放蘑菇");

            holder.txtState.setText("已添加");

            holder.btnComit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ToastUtil.show(context, "准了");
                }
            });

            if(position<3){
                holder.btnComit.setVisibility(View.VISIBLE);
                holder.txtState.setVisibility(View.GONE);
            }else{
                holder.btnComit.setVisibility(View.GONE);
                holder.txtState.setVisibility(View.VISIBLE);
            }

        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_friend_apply, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private TextView txtName;
            private TextView txtInfo;
            private TextView txtState;
            private Button btnComit;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
                txtName = (TextView) convertView.findViewById(R.id.txt_name);
                txtInfo = (TextView) convertView.findViewById(R.id.txt_info);
                txtState = (TextView) convertView.findViewById(R.id.txt_state);
                btnComit = (Button) convertView.findViewById(R.id.btn_comit);
            }

        }
    }

}
