package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.HeadView;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppActivity {

    private ClearEditText mEdtSearch;
    private RecyclerView mRecyclerViewHot;
    private RecyclerView mRecyclerViewType;
    private HelpAdapter hotAdapter;
    private HelpAdapter typeAdapter;
    private HeadView mHeadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hlep);
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        mEdtSearch = findViewById(R.id.edt_search);
        mHeadView = findViewById(R.id.headView);
        mRecyclerViewHot = findViewById(R.id.recyclerView_hot);
        LinearLayoutManager hotManger = new LinearLayoutManager(this);
        mRecyclerViewHot.setLayoutManager(hotManger);
        mRecyclerViewHot.setNestedScrollingEnabled(false);

        mRecyclerViewType = findViewById(R.id.recyclerView_type);
        LinearLayoutManager typeManger = new LinearLayoutManager(this);
        mRecyclerViewType.setLayoutManager(typeManger);
        mRecyclerViewType.setNestedScrollingEnabled(false);

        hotAdapter = new HelpAdapter();
        typeAdapter = new HelpAdapter();
        mRecyclerViewHot.setAdapter(hotAdapter);
        mRecyclerViewType.setAdapter(typeAdapter);
    }

    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
    }

    private void initData() {
        List<String> list = new ArrayList<>();
        list.add("帮助选项1111");
        list.add("帮助选项2222");
        list.add("帮助选项3333");
        list.add("帮助选项4444");
        list.add("帮助选项5555");
        list.add("帮助选项6666");
        list.add("帮助选项7777");

        hotAdapter.setData(list);
        typeAdapter.setData(list);
    }


    class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpViewHolder> {
        private List<String> list;


        public void setData(List<String> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public HelpViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new HelpViewHolder(inflater.inflate(R.layout.item_help_list, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(HelpViewHolder viewHolder, int i) {
            viewHolder.mTvTitle.setText(list.get(i));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HelpActivity.this,HelpInfoActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return position;

        }

        @Override
        public int getItemCount() {
            if (list != null && list.size() > 0) {
                return list.size();
            }
            return 0;
        }


        class HelpViewHolder extends RecyclerView.ViewHolder {
            private TextView mTvTitle;

            public HelpViewHolder(@NonNull View itemView) {
                super(itemView);
                mTvTitle = itemView.findViewById(R.id.tv_title);
            }
        }
    }


}
