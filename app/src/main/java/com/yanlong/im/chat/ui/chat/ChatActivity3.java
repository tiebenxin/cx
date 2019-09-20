package com.yanlong.im.chat.ui.chat;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.ui.ChatInfoActivity;
import com.yanlong.im.chat.ui.GroupInfoActivity;
import com.yanlong.im.chat.ui.cell.FactoryChatCell;
import com.yanlong.im.chat.ui.cell.ICellEventListener;
import com.yanlong.im.chat.ui.cell.MessageAdapter;
import com.yanlong.im.databinding.ActivityChat2Binding;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.base.BaseMvpActivity;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.ActionbarView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class ChatActivity3 extends BaseMvpActivity<ChatModel, ChatView, ChatPresenter> implements ICellEventListener, ChatView {
    public static final String AGM_TOUID = "toUId";
    public static final String AGM_TOGID = "toGId";
    private ChatModel mChatModel;
    private boolean isGroup;
    private LinearLayoutManager layoutManager;
    private MessageAdapter adapter;
    private ActivityChat2Binding ui;
    private ActionbarView actionbar;
    private String gid;
    private long uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_chat2);
        init();
    }

    private void init() {
        initIntent();
        initEvent();
        intAdapter();
        initUIAndListener();
    }

    private void initEvent() {
        presenter.registerIMListener();
    }


    @Override
    protected void onStart() {
        super.onStart();
        presenter.checkLockMessage();
        presenter.loadAndSetData();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        presenter.unregisterIMListener();
        super.onDestroy();
    }

    private void initIntent() {
        gid = getIntent().getStringExtra(AGM_TOGID);
        uid = getIntent().getLongExtra(AGM_TOUID, 0);
        uid = uid == 0 ? -1L : uid;
        isGroup = StringUtil.isNotNull(gid);
        mChatModel.init(gid, uid);
    }

    @Override
    public ChatModel createModel() {
        mChatModel = new ChatModel();
        return mChatModel;
    }

    @Override
    public ChatView createView() {
        return this;
    }

    @Override
    public ChatPresenter createPresenter() {
        return new ChatPresenter();
    }

    private void intAdapter() {
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(context, this, isGroup);
        adapter.setCellFactory(new FactoryChatCell(context, adapter, this));
        ui.recyclerView.setAdapter(adapter);
    }

    @Override
    public void initUIAndListener() {
        actionbar = ui.headView.getActionbar();
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        if (isGroup) {
            actionbar.getBtnRight().setVisibility(View.GONE);
            ui.viewChatBottom.setVisibility(View.VISIBLE);
        } else {
            actionbar.getBtnRight().setVisibility(View.VISIBLE);
            if (uid == 1L) {
                ui.viewChatBottom.setVisibility(View.GONE);
            } else {
                ui.viewChatBottom.setVisibility(View.VISIBLE);
            }
        }

        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (isGroup) {//群聊,单聊
                    toGroupInfoActivity();
                } else {
                    if (uid == 1L) {
                        toUserInfoActivity();
                    } else {
                        toChatInfoActivity();
                    }
                }
            }
        });

    }

    private void toUserInfoActivity() {
        startActivity(new Intent(getContext(), UserInfoActivity.class).putExtra(UserInfoActivity.ID, uid).putExtra(UserInfoActivity.JION_TYPE_SHOW, 1));
    }

    private void toGroupInfoActivity() {
        startActivity(new Intent(getContext(), GroupInfoActivity.class).putExtra(GroupInfoActivity.AGM_GID, gid));
    }

    private void toChatInfoActivity() {
        startActivity(new Intent(getContext(), ChatInfoActivity.class).putExtra(ChatInfoActivity.AGM_FUID, uid));
    }

    @Override
    public void onEvent(int type, MsgAllBean message, Object... args) {

    }

    @Override
    public void setAndRefreshData(List<MsgAllBean> l) {
        adapter.bindData(l);
        ui.recyclerView.scrollToPosition(adapter.getItemCount() - 1);

    }
}
