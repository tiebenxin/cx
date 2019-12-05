package com.hm.cxpay.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.PopupSelectView;

import java.util.ArrayList;
import java.util.List;

public class RedPacketDetailsActivity extends BasePayActivity {
    private ActionbarView mActionBar;
    private ImageView mSdImageHead;
    private TextView mTvUserName;
    private TextView mTvContent;
    private TextView mTvMoney;
    private TextView mTvHint;
    private MultiListView mMtListView;
    private List<String> list = new ArrayList<>();

    private String[] strings = {"查看支付宝红包记录", "取消"};
    private PopupSelectView popupSelectView;

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

        mActionBar.getBtnRight().setImageResource(R.mipmap.ic_more);
        mMtListView.init(new RedPacketAdapter());
        mMtListView.getLoadView().setStateNormal();
        initData();
    }


    private void initData() {
        for (int i = 0; i < 30; i++) {
            list.add("1111");
        }
        mMtListView.getListView().getAdapter().notifyDataSetChanged();

    }


    private void initEvent() {
        mActionBar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                initPopup();
            }
        });
    }

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mActionBar, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:


                        break;
                }
                popupSelectView.dismiss();
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
            private ImageView mSdImageHead;
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
