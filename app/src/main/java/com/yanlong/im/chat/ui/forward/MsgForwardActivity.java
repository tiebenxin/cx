package com.yanlong.im.chat.ui.forward;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.view.AlertForward;
import com.yanlong.im.databinding.ActivityMsgForwardBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.CustomTabView;

/***
 * 消息转换
 */
public class MsgForwardActivity extends AppActivity implements IForwardListener {
    public static final String AGM_JSON = "JSON";
    private ActionbarView actionbar;
    private ActivityMsgForwardBinding ui;
    //    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();
    private MsgAllBean msgAllBean;
    private MsgAllBean sendMesage;//转发消息


    @CustomTabView.ETabPosition
    private int currentPager = CustomTabView.ETabPosition.LEFT;
    private String json;
    boolean hasSendMessage = false;


    //自动寻找控件
    private void findViews() {
        actionbar = ui.headView.getActionbar();
    }


    //自动生成的控件事件
    private void initEvent() {
        json = getIntent().getStringExtra(AGM_JSON);
        msgAllBean = GsonUtils.getObject(json, MsgAllBean.class);

        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        ui.tabView.setTabSelectListener(new CustomTabView.OnTabSelectListener() {
            @Override
            public void onLeft() {
                showFragment(CustomTabView.ETabPosition.LEFT);
            }

            @Override
            public void onRight() {
                showFragment(CustomTabView.ETabPosition.RIGHT);
            }
        });

    }

    private void resetTitle(@CustomTabView.ETabPosition int tab) {
        if (tab == CustomTabView.ETabPosition.RIGHT) {
            ui.headView.setTitle("选择一个联系人");
        } else if (tab == CustomTabView.ETabPosition.LEFT) {
            ui.headView.setTitle("选择一个聊天");
        }
    }

    private void showFragment(@CustomTabView.ETabPosition int tab) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = fragmentManager.findFragmentByTag(tab + "");
        if (newFragment == null) {
            newFragment = createFragment(tab);
        }
        if (newFragment == null) {
            return;
        }
        prepareFragment(newFragment);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fl_content, newFragment, tab + "");
        ft.attach(newFragment);
        ft.commitAllowingStateLoss();
        currentPager = tab;
        resetTitle(currentPager);
    }

    private void prepareFragment(Fragment fragment) {
        if (fragment instanceof ForwardSessionFragment) {
            ForwardSessionFragment sessionFragment = (ForwardSessionFragment) fragment;
            sessionFragment.setForwardListener(this);
        } else if (fragment instanceof ForwardRosterFragment) {
            ForwardRosterFragment rosterFragment = (ForwardRosterFragment) fragment;
            rosterFragment.setForwardListener(new IForwardRosterListener() {
                @Override
                public void onSelectMuc() {
                    Intent intent = new Intent(MsgForwardActivity.this, GroupSelectActivity.class);
                    intent.putExtra(AGM_JSON, json);
                    startActivityForResult(intent, 0);

                }

                @Override
                public void onForward(long uid, String gid, String avatar, String nick) {
                    MsgForwardActivity.this.onForward(uid, gid, avatar, nick);
                }
            });
        }
    }

    private Fragment createFragment(@CustomTabView.ETabPosition int tab) {
        switch (tab) {
            case CustomTabView.ETabPosition.LEFT:
                return new ForwardSessionFragment();
            case CustomTabView.ETabPosition.RIGHT:
                return new ForwardRosterFragment();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_msg_forward);
        findViews();
        initEvent();
        showFragment(currentPager);

    }

    @Override
    public void onForward(final long toUid, final String toGid, String mIcon, String mName) {
        if (msgAllBean == null)
            return;
        AlertForward alertForward = new AlertForward();
        if (msgAllBean.getChat() != null) {//转换文字
            alertForward.init(MsgForwardActivity.this, mIcon, mName, msgAllBean.getChat().getMsg(), null, "发送", new AlertForward.Event() {


                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msgAllBean.getChat().getMsg());
                    MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        SocketData.sendAndSaveMessage(allBean);
                        sendMesage = allBean;
                    }

//                    sendMesage = SocketData.send4Chat(toUid, toGid, msgAllBean.getChat().getMsg());
                    if (StringUtil.isNotNull(content)) {
                        ChatMessage chat = SocketData.createChatMessage(SocketData.getUUID(), content);
                        MsgAllBean messageBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), chat);
                        if (messageBean != null) {
                            SocketData.sendAndSaveMessage(messageBean);
                            sendMesage = messageBean;

                        }
//                        sendMesage = SocketData.send4Chat(toUid, toGid, content);
                    }
                    doSendSuccess();
                    notifyRefreshMsg(toGid, toUid);
                }
            });
        } else if (msgAllBean.getImage() != null) {

            alertForward.init(MsgForwardActivity.this, mIcon, mName, null, msgAllBean.getImage().getThumbnail(), "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    // ToastUtil.show(context, msgAllBean.getImage().getThumbnail()+"---\n"+content);
//                    Long toUId = bean.getFrom_uid();
//                    String toGid = bean.getGid();
                    ImageMessage imagesrc = msgAllBean.getImage();
                    if (msgAllBean.getFrom_uid() == UserAction.getMyId().longValue()) {
                        imagesrc.setReadOrigin(true);
                    }
                    sendMesage = SocketData.send4Image(toUid, toGid, imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), new Long(imagesrc.getWidth()).intValue(), new Long(imagesrc.getHeight()).intValue(), new Long(imagesrc.getSize()).intValue());
                    msgDao.ImgReadStatSet(imagesrc.getOrigin(), imagesrc.isReadOrigin());
                    if (StringUtil.isNotNull(content)) {
                        sendMesage = SocketData.send4Chat(toUid, toGid, content);
                    }
                    doSendSuccess();
                    notifyRefreshMsg(toGid, toUid);

                }
            });

        } else if (msgAllBean.getAtMessage() != null) {

            alertForward.init(MsgForwardActivity.this, mIcon, mName, msgAllBean.getAtMessage().getMsg(), null, "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
//                    sendMesage = SocketData.send4Chat(toUid, toGid, msgAllBean.getAtMessage().getMsg());
//                    if (StringUtil.isNotNull(content)) {
//                        sendMesage = SocketData.send4Chat(toUid, toGid, content);
//                    }

                    ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msgAllBean.getAtMessage().getMsg());
                    MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        SocketData.sendAndSaveMessage(allBean);
                        sendMesage = allBean;
                    }
                    if (StringUtil.isNotNull(content)) {
                        ChatMessage chat = SocketData.createChatMessage(SocketData.getUUID(), content);
                        MsgAllBean messageBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), chat);
                        if (messageBean != null) {
                            SocketData.sendAndSaveMessage(messageBean);
                            sendMesage = messageBean;

                        }
                    }

                    doSendSuccess();
                    notifyRefreshMsg(toGid, toUid);
                }
            });
        } else if (msgAllBean.getVideoMessage() != null) {
            alertForward.init(MsgForwardActivity.this, mIcon, mName, null, msgAllBean.getVideoMessage().getBg_url(), "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {

                    sendMesage = SocketData.转发送视频整体信息(toUid, toGid, msgAllBean.getVideoMessage());

                    if (StringUtil.isNotNull(content)) {
                        sendMesage = SocketData.send4Chat(toUid, toGid, content);
                    }
                    doSendSuccess();
                    notifyRefreshMsg(toGid, toUid);
                }
            });

        }
        alertForward.show();

    }

    public void doSendSuccess() {
        ToastUtil.show(this, "转发成功");
        finish();
        //        ui.tvSuccess.setVisibility(View.VISIBLE);
//        ui.tvSuccess.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ui.tvSuccess.setVisibility(View.GONE);
//                    finish();
//
//                }
//        }, 1000);

    }

    private void notifyRefreshMsg(String toGid, long toUid) {
        MessageManager.getInstance().setMessageChange(true);
        MessageManager.getInstance().notifyRefreshMsg(!TextUtils.isEmpty(toGid) ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUid, toGid, CoreEnum.ESessionRefreshTag.SINGLE, sendMesage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                finish();
            }
        }
    }


}
