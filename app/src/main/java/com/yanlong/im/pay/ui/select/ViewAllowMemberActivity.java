package com.yanlong.im.pay.ui.select;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.databinding.ActivityAllowMemberBinding;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/8/25
 * Description
 */
public class ViewAllowMemberActivity extends AppActivity {

    private ActivityAllowMemberBinding ui;
    private MsgDao msgDao = new MsgDao();
    private String gid;
    private String[] memberIds;
    private AdapterAllowMember mAdapter;

    public static Intent newIntent(Context context, String gid, String[] memberIds) {
        Intent intent = new Intent(context, ViewAllowMemberActivity.class);
        intent.putExtra("gid", gid);
        intent.putExtra("memberIds", memberIds);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_allow_member);
        gid = getIntent().getStringExtra("gid");
        memberIds = getIntent().getStringArrayExtra("memberIds");
        initView();
        initAdapter();
        initData();

    }

    private void initView() {
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
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
        List<MemberUser> members = msgDao.getMembers(gid, memberIds);
        mAdapter.bindData(members);
    }

    private void initAdapter() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerView.setLayoutManager(manager);
        mAdapter = new AdapterAllowMember(this);
        ui.recyclerView.setAdapter(mAdapter);
    }
}
