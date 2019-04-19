package com.yanlong.im.pay.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yanlong.im.R;

import net.cb.cb.library.view.MultiListView;

import java.util.ArrayList;
import java.util.List;

public class RedpacketRecordFragment extends Fragment {
    private View rootView;
    private MultiListView mMtListView;

    private List<String> list = new ArrayList<>();

    public RedpacketRecordFragment() {

    }


    public static RedpacketRecordFragment newInstance() {
        RedpacketRecordFragment fragment = new RedpacketRecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgm_red_packet_record, null);
        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layparm);
        initView();
        initData();
        return rootView;
    }

    private void initView() {
        mMtListView = rootView.findViewById(R.id.mtListView);
        mMtListView.init(new RedpacketRecordFragment.RedPacketAdapter());
        mMtListView.getLoadView().setStateNormal();
    }

    private void initData(){
        for (int i = 0; i < 15; i++) {
            list.add("1111");
        }
        mMtListView.getListView().getAdapter().notifyDataSetChanged();

    }


    class RedPacketAdapter extends RecyclerView.Adapter<RedPacketAdapter.ViewHodler> {


        @Override
        public ViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = getLayoutInflater();
            ViewHodler holder = new ViewHodler(inflater.inflate(R.layout.item_red_packet_record, viewGroup, false));
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
            private TextView mTvUserName;
            private TextView mTvMoney;
            private TextView mTvDate;


            public ViewHodler(@NonNull View itemView) {
                super(itemView);
                mTvUserName =  itemView.findViewById(R.id.tv_user_name);
                mTvMoney =  itemView.findViewById(R.id.tv_money);
                mTvDate =  itemView.findViewById(R.id.tv_date);
            }
        }
    }


}
