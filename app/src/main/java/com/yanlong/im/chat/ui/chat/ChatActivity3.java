package com.yanlong.im.chat.ui.chat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityChat2Binding;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.base.BaseMvpActivity;
import net.cb.cb.library.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class ChatActivity3 extends BaseMvpActivity<ChatModel, ChatView, ChatPresenter> implements SocketEvent {
    public static final String AGM_TOUID = "toUId";
    public static final String AGM_TOGID = "toGId";
    private ChatModel mChatModel;
    private ChatView mChatView;
    private boolean isGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChat2Binding ui = DataBindingUtil.setContentView(this, R.layout.activity_chat2);
        EventBus.getDefault().register(this);
        init();
        mChatView.initView(ui, this,isGroup);


    }

    private void init() {
        initIntent();
        initEvent();
    }

    private void initEvent() {
        SocketUtil.getSocketUtil().addEvent(this);

    }

    private void initIntent() {
        String toGid = getIntent().getStringExtra(AGM_TOGID);
        long toUId = getIntent().getLongExtra(AGM_TOUID, 0);
        toUId = toUId == 0 ? -1L : toUId;
        isGroup = StringUtil.isNotNull(toGid);
        mChatModel.init(toGid, toUId);
    }

    @Override
    public ChatModel createModel() {
        mChatModel = new ChatModel();
        return mChatModel;
    }

    @Override
    public ChatView createView() {
        mChatView = new ChatView();
        return mChatView;
    }

    @Override
    public ChatPresenter createPresenter() {
        return new ChatPresenter();
    }

    @Override
    public void onHeartbeat() {

    }

    @Override
    public void onACK(MsgBean.AckMessage bean) {

    }

    @Override
    public void onMsg(MsgBean.UniversalMessage bean) {

    }

    @Override
    public void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean) {

    }

    @Override
    public void onLine(boolean state) {

    }
}
