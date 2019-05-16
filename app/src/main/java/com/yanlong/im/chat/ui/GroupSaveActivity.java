package com.yanlong.im.chat.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/***
 * 保存的群聊
 */
public class GroupSaveActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;


    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mtListView);
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
        mtListView.getLoadView().setStateNormal();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_save);
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

            holder.imgHead.setImageURI(Uri.parse("https://gss2.bdstatic.com/-fo3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D220/sign=181c8583082442a7aa0efaa7e143ad95/a08b87d6277f9e2f8145a2081830e924b899f3ba.jpg"));
            holder.txtName.setText("曼舞手雷");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    go(ChatActivity.class);

                }
            });


            if (getItemCount() == (position + 1)) {
                holder.txtNum.setText(getItemCount() + "个群聊");
                holder.txtNum.setVisibility(View.VISIBLE);
            } else {
                holder.txtNum.setVisibility(View.GONE);
            }


        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_group_save, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private TextView txtName;
            private TextView txtNum;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                txtNum = convertView.findViewById(R.id.txt_num);
            }

        }
    }

}
