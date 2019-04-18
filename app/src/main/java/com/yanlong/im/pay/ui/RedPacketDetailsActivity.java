package com.yanlong.im.pay.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.MultiListView;

import java.util.ArrayList;
import java.util.List;

public class RedPacketDetailsActivity extends AppActivity {


    private ActionbarView mActionBar;
    private SimpleDraweeView mSdImageHead;
    private TextView mTvUserName;
    private TextView mTvContent;
    private TextView mTvMoney;
    private TextView mTvHint;
    private MultiListView mMtListView;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_packet_details);
        initView();
        initEvent();
    }

    private void initView() {
        mActionBar = findViewById(R.id.action_bar);
        mSdImageHead = findViewById(R.id.sd_image_head);
        mTvUserName = findViewById(R.id.tv_user_name);
        mTvContent = findViewById(R.id.tv_content);
        mTvMoney = findViewById(R.id.tv_money);
        mTvHint = findViewById(R.id.tv_hint);
        mMtListView = findViewById(R.id.mtListView);

        mActionBar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        mMtListView.init(new RedPacketAdapter());
        mMtListView.getLoadView().setStateNormal();
    }

    private void initEvent() {
        mActionBar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
    }


    class RedPacketAdapter extends RecyclerView.Adapter<RedPacketAdapter.ViewHodler> {


        @Override
        public ViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ViewHodler holder = new ViewHodler(inflater.inflate(R.layout.item_red_packet_details, viewGroup, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHodler viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            if (list != null && list.size() > 0) {
                return list.size();
            }
            return 0;
        }


        class ViewHodler extends RecyclerView.ViewHolder {
            private SimpleDraweeView mSdImageHead;
            private TextView mTvUserName;
            private TextView mTvDete;
            private TextView mTvMoney;


            public ViewHodler(@NonNull View itemView) {
                super(itemView);
                mSdImageHead = itemView.findViewById(R.id.sd_image_head);
                mTvUserName = itemView.findViewById(R.id.tv_user_name);
                mTvDete = itemView.findViewById(R.id.tv_dete);
                mTvMoney = itemView.findViewById(R.id.tv_money);
            }
        }
    }


}
