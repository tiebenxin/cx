package com.yanlong.im.user.ui;

import android.content.Intent;
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
    public static final String GID = "gid";
    public static final String UID = "uid";
    public static final String FROM_WHERE = "fromWhere";// 0 普通投诉  1 广场投诉
    public static final String COMMENT_ID = "commentId";//评论id
    public static final String DEFENDANT_UID = "defendantUid";//被投诉人的uid
    public static final String MOMENT_ID = "momentId";//说说id

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;
    private String[] strings = {
            "发布色情、广告对我造成骚扰",
            "存在欺诈骗钱行为",
            "违法内容",
            "侵犯著作权",
            "此账号可能被盗用了",
            "通过不正当手段获取他人或公司机密"
    };

    private String gid;
    private String uid;
    private int fromWhere;
    private long commentId;
    private long defendantUid;
    private long momentId;

    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mtListView);

        gid = getIntent().getStringExtra(GID);
        uid = getIntent().getStringExtra(UID);
        fromWhere = getIntent().getIntExtra(FROM_WHERE,0);
        commentId = getIntent().getLongExtra(COMMENT_ID,0);
        defendantUid = getIntent().getLongExtra(DEFENDANT_UID,0);
        momentId = getIntent().getLongExtra(MOMENT_ID,0);
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
        setContentView(R.layout.activity_complaint);
        findViews();
        initEvent();

    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            if(strings != null){
                return strings.length;
            }
            return 0;
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, final int position) {
            holder.txtComplaintTitle.setText(strings[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ComplaintUploadActivity.class);
                    intent.putExtra(ComplaintUploadActivity.COMPLATION_TYPE,getType(position));
                    intent.putExtra(ComplaintUploadActivity.UID,uid);
                    intent.putExtra(ComplaintUploadActivity.GID,gid);
                    intent.putExtra(FROM_WHERE,fromWhere);
                    intent.putExtra(COMMENT_ID,commentId);
                    intent.putExtra(DEFENDANT_UID,defendantUid);
                    intent.putExtra(MOMENT_ID,momentId);
                    startActivity(intent);
                }
            });
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
                txtComplaintTitle = convertView.findViewById(R.id.txt_complaint_title);
            }
        }
    }

    public int getType(int position){
        switch (position){
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 4;
            case 3:
                return 5;
            case 4:
                return 2;
            case 5:
                return 3;
        }
        return 0;
    }

}
