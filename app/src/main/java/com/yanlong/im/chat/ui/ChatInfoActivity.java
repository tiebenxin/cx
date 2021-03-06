package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.ReadDestroyBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventSwitchSnapshot;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.DestroyTimeView;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.ReadDestroyUtil;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.CloseActivityEvent;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventIsShowRead;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 聊天详情
 */
public class ChatInfoActivity extends AppActivity {
    public static final String AGM_FUID = "fuid";
    private Long fuid;

    private HeadView headView;
    private ActionbarView actionbar;
    private RecyclerView topListView;
    private LinearLayout viewLog;
    private LinearLayout viewTop;
    private LinearLayout read_destroy_ll;
    private CheckBox ckTop;
    private LinearLayout viewDisturb;
    private CheckBox ckDisturb;
    private LinearLayout viewLogClean;
    private CheckBox ckScreenshot;//截屏通知
    //  private Session session;
    private UserInfo fUserInfo;
    boolean isSessionChange = false;

    private int destroyTime;

    private ReadDestroyUtil readDestroyUtil = new ReadDestroyUtil();
    private LinearLayout viewDestroyTime;
    private TextView tvDestroyTime, tvTwoWayClearChat, tvTwoWayClearChatHint;
    private CheckBox ckSetRead;
    private CommonSelectDialog.Builder builder;
    private CommonSelectDialog dialogOne;//提示弹框：该账号已注销

    private boolean canAdd = true;//是否允许点击+号拉人


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);
        findViews();
        initEvent();
        initData();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        topListView = findViewById(R.id.topListView);
        viewLog = findViewById(R.id.view_log);
        viewTop = findViewById(R.id.view_top);
        ckTop = findViewById(R.id.ck_top);
        viewDisturb = findViewById(R.id.view_disturb);
        ckDisturb = findViewById(R.id.ck_disturb);
        viewLogClean = findViewById(R.id.view_log_clean);
        viewDestroyTime = findViewById(R.id.view_destroy_time);
        tvDestroyTime = findViewById(R.id.tv_destroy_time);
        ckSetRead = findViewById(R.id.ck_set_read);
        read_destroy_ll = findViewById(R.id.read_destroy_ll);
        ckScreenshot = findViewById(R.id.ck_screenshot);
        tvTwoWayClearChat = findViewById(R.id.tv_two_way_clear_chat);
        tvTwoWayClearChatHint = findViewById(R.id.tv_two_way_clear_chat_hint);
        builder = new CommonSelectDialog.Builder(ChatInfoActivity.this);
    }

    private final String IS_VIP = "1";// (0:普通|1:vip)

    //自动生成的控件事件
    private void initEvent() {
        IUser userInfo = UserAction.getMyInfo();
        if (userInfo != null && IS_VIP.equals(userInfo.getVip())) {
            //vip才开启双向清除功能
            tvTwoWayClearChat.setVisibility(View.VISIBLE);
            tvTwoWayClearChatHint.setVisibility(View.VISIBLE);
        } else {
            if (userInfo != null && userInfo.getHistoryClear() == 1) {
                tvTwoWayClearChat.setVisibility(View.VISIBLE);
                tvTwoWayClearChatHint.setVisibility(View.VISIBLE);
            } else {
                tvTwoWayClearChat.setVisibility(View.GONE);
                tvTwoWayClearChatHint.setVisibility(View.GONE);
            }
        }
        fuid = getIntent().getLongExtra(AGM_FUID, 0);
        taskGetInfo();
        if (Constants.CX888_UID.equals(fuid)) {
            read_destroy_ll.setVisibility(View.GONE);
        }

        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        //顶部处理
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        topListView.setLayoutManager(linearLayoutManager);
        topListView.setAdapter(new RecyclerViewTopAdapter());

        ckTop.setChecked(fUserInfo != null && fUserInfo.getIstop() == 1);
        ckTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fUserInfo != null) {
                    fUserInfo.setIstop(isChecked ? 1 : 0);
                    taskSaveInfo();
                    taskUpSwitch(null, fUserInfo.getIstop());
                }
            }
        });

        ckDisturb.setChecked(fUserInfo != null && fUserInfo.getDisturb() == 1);
        ckDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fUserInfo != null) {
                    fUserInfo.setDisturb(isChecked ? 1 : 0);
                    taskSaveInfo();
                    taskUpSwitch(fUserInfo.getDisturb(), null);
                }
            }
        });

        ckSetRead.setChecked(fUserInfo != null && fUserInfo.getMyRead() == 1);
        ckSetRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fUserInfo != null) {
                    fUserInfo.setMyRead(isChecked ? 1 : 0);
                    taskSaveInfo();
                    taskFriendsSetRead(fuid, isChecked ? 1 : 0);
                }
            }
        });

        viewLogClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNo alertYesNo = new AlertYesNo();
                alertYesNo.init(ChatInfoActivity.this, "提示", "确定清空聊天记录？", "确定", "取消", new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        taskDelMsg("删除成功");
                    }
                });
                alertYesNo.show();

            }
        });

        viewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchMsgActivity.class)
                        .putExtra(SearchMsgActivity.AGM_FUID, fuid).putExtra(SearchMsgActivity.FROM, 1)
                );
            }
        });

        viewDestroyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DestroyTimeView destroyTimeView = new DestroyTimeView(ChatInfoActivity.this);
                destroyTimeView.initView();
                destroyTimeView.setPostion(destroyTime);
                destroyTimeView.setListener(new DestroyTimeView.OnClickItem() {
                    @Override
                    public void onClickItem(String content, int survivaltime) {
                        if (ChatInfoActivity.this.destroyTime != survivaltime) {
                            destroyTime = survivaltime;
                            tvDestroyTime.setText(content);
                            taskSurvivalTime(fuid, survivaltime);
                        }
                    }
                });
            }
        });

        tvTwoWayClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //双向清除(单聊),包括常信客服-显示提示框
                AlertYesNo alertYesNo = new AlertYesNo();
                alertYesNo.init(ChatInfoActivity.this, "提示", getString(R.string.two_way_clear_chat_dialog_hint), "确定", "取消", new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        taskDelMsg(getString(R.string.two_way_clear_chat_success));//删除本地记录
                        //发送双向删除请求
                        SocketData.send4TwoWayClean(fuid, System.currentTimeMillis());
                    }
                });
                alertYesNo.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ChatActivity.class).putExtra(ChatActivity.AGM_TOUID, fuid));
        finish();
    }


    private void initData() {
        readDestroyUtil = new ReadDestroyUtil();
        UserInfo userInfo = userDao.findUserInfo(fuid);
        if (userInfo != null) {
            destroyTime = userInfo.getDestroy();
            String content = readDestroyUtil.getDestroyTimeContent(destroyTime);
            tvDestroyTime.setText(content);
            //显示截屏通知切换开关状态
            if (fUserInfo != null) {
                ckScreenshot.setChecked(fUserInfo.getScreenshotNotification() == 1);
            }

        }
        //截屏通知切换开关
        ckScreenshot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //如果阅后即焚已关闭，则正常开关；如果阅后即焚开启中，则不允许关掉
                if (fUserInfo != null) {
                    //开
                    boolean currentStatus = fUserInfo.getScreenshotNotification() == 1;
                    if (currentStatus == isChecked) {
                        return;
                    }
                    if (isChecked) {
                        ckScreenshot.setChecked(true);//选中
                        fUserInfo.setScreenshotNotification(1);//截屏通知字段设置为打开
                    } else {
                        //关
                        ckScreenshot.setChecked(false);//取消选中
                        fUserInfo.setScreenshotNotification(0);//截屏通知字段设置为关闭
                    }
                    taskSaveInfo();//更新本地数据库
                    httpSingleScreenShotSwitch(fuid + "", isChecked ? 1 : 0);//调接口通知后台

                }
            }
        });
    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewTopAdapter extends RecyclerView.Adapter<RecyclerViewTopAdapter.RCViewTopHolder> {

        @Override
        public int getItemCount() {
            // return listDataTop == null ? 0 : listDataTop.size();
            if (UserUtil.isSystemUser(fuid)) {
                return 1;
            } else {
                return 2;
            }
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewTopHolder holder, int position) {
            //listDataTop.get(position)
            UserInfo userInfo = null;
            switch (position) {
                case 0:
                    userInfo = fUserInfo;
                    // holder.imgHead.setImageURI(Uri.parse("" + userInfo.getHead()));
                    if (userInfo != null) {
                        if (!TextUtils.isEmpty(userInfo.getHead())) {
                            Glide.with(context).load(userInfo.getHead())
                                    .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
                        }
                    }
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fUserInfo == null || fUserInfo.getUid() == null) {
                                return;
                            }
                            startActivity(new Intent(getContext(), UserInfoActivity.class)
                                    .putExtra(UserInfoActivity.ID, fUserInfo.getUid()));
                        }
                    });
                    break;
                case 1:

                    holder.imgHead.setImageResource(R.mipmap.ic_group_a);

                    holder.imgHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (canAdd == false) {
                                showAddDialog();
                                return;
                            }

                            if (fUserInfo.getuType() == 2) {//是好友
                                finish();
                                EventBus.getDefault().post(new EventExitChat());
                                startActivity(new Intent(getContext(), GroupCreateActivity.class).putExtra(GroupCreateActivity.AGM_SELECT_UID, "" + fUserInfo.getUid()));
                            }
                        }
                    });
                    break;
            }

        }


        //自动寻找ViewHold
        @Override
        public RCViewTopHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewTopHolder holder = new RCViewTopHolder(inflater.inflate(R.layout.item_group_create_top, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewTopHolder extends RecyclerView.ViewHolder {
            private ImageView imgHead;

            //自动寻找ViewHold
            public RCViewTopHolder(View convertView) {
                super(convertView);
                imgHead = convertView.findViewById(R.id.img_head);
            }

        }
    }

    private MsgDao msgDao = new MsgDao();
    private MsgAction msgAction = new MsgAction();
    private UserDao userDao = new UserDao();

    //获取会话和对方信息
    private void taskGetInfo() {
       /* session = DaoUtil.findOne(Session.class, "from_uid", fuid);
        if (session == null) {
            session = msgDao.sessionCreate(null, fuid);
        }*/
        fUserInfo = DaoUtil.findOne(UserInfo.class, "uid", fuid);
        if (fUserInfo != null && fUserInfo.getuType() != 2) {//非好友不能设置开关等
            ckDisturb.setEnabled(false);
            ckTop.setEnabled(false);

        } else {
            ckDisturb.setEnabled(true);
            ckTop.setEnabled(true);

        }
        //已注销的用户不允许点击+号
        if (fUserInfo != null && fUserInfo.getFriendDeactivateStat() == -1) {
            canAdd = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskGetInfo();
    }

    //更新配置
    private void taskSaveInfo() {
        DaoUtil.update(fUserInfo);
    }

    private void taskDelMsg(String hint) {
//        msgDao.msgDel(fuid, null);
        if (MyAppLication.INSTANCE().repository != null) {
            MyAppLication.INSTANCE().repository.deleteAllMessage(fuid, null);
        }
        EventBus.getDefault().post(new EventRefreshChat());
        ToastUtil.show(ChatInfoActivity.this, hint);
    }

    /*
     * 置顶和免打扰
     * */
    private void taskUpSwitch(Integer isMute, Integer istop) {
        msgAction.sessionSwitch(fuid, isMute, istop, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    Session session = null;
                    if (isMute == null && istop != null) {
                        session = msgDao.updateUserSessionTop(fuid, istop);
//                        msgDao.updateUserTop(fuid, istop.intValue()); TODO 消息列表没数据时会报java.lang.IllegalStateException异常
                    } else if (isMute != null && istop == null) {
                        session = msgDao.updateUserSessionDisturb(fuid, isMute);
//                        msgDao.updateUserDisturb(fuid, isMute.intValue());TODO 消息列表没数据时会报java.lang.IllegalStateException异常
                    }
                    MessageManager.getInstance().setMessageChange(true);
                    MessageManager.getInstance().notifySwitchDisturb();
                } else {
                    ToastUtil.show(getContext(), response.body().getMsg());

                }
            }
        });
    }

    private void taskSurvivalTime(long friend, int survivalTime) {
        msgAction.setSurvivalTime(friend, survivalTime, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    userDao.updateReadDestroy(fuid, survivalTime);
                    if (fUserInfo != null && fUserInfo.getFriendDeactivateStat() != -1) {//若该账号已注销，不显示本地通知消息
                        msgDao.noteMsgAddSurvivaltime(fuid, null);
                    }
                }
            }
        });
    }


    /**
     * 设置已读
     */
    private void taskFriendsSetRead(long uid, int read) {
        new UserAction().friendsSetRead(uid, read, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                EventBus.getDefault().post(new EventIsShowRead(uid, EventIsShowRead.EReadSwitchType.SWITCH_FRIEND, read));
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setingReadDestroy(ReadDestroyBean bean) {
        if (bean.uid == fuid) {
            destroyTime = bean.survivaltime;
            String content = readDestroyUtil.getDestroyTimeContent(destroyTime);
            tvDestroyTime.setText(content);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeActivityEvent(CloseActivityEvent event) {
        if (event.type.contains("ChatInfoActivity")) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventSwitchSnapshot(EventSwitchSnapshot event) {
        long uid = event.getUid();
        if (fUserInfo != null && fUserInfo.getUid() != null && uid > 0) {
            if (uid == fUserInfo.getUid().longValue()) {
                fUserInfo.setScreenshotNotification(event.getFlag());
                ckScreenshot.setChecked(fUserInfo.getScreenshotNotification() == 1);
            }
        }
    }


    //单聊-截屏通知开关
    private void httpSingleScreenShotSwitch(String friendId, int screenshot) {
        msgAction.singleScreenShotSwitch(friendId, screenshot, new Callback<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                } else {
                    if (response.body().isOk()) {
                        if (fUserInfo != null && fUserInfo.getFriendDeactivateStat() != -1) {//若该账号已注销，不显示本地通知消息
                            MsgNotice notice = SocketData.createMsgNoticeOfSnapshotSwitch(SocketData.getUUID(), screenshot);
                            MsgAllBean bean = SocketData.createMessageBean(fuid, "", ChatEnum.EMessageType.NOTICE, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), notice);
                            if (bean != null) {
                                SocketData.saveMessage(bean);
                            }
                        }
                    }
                }
                ToastUtil.show(getContext(), response.body().getMsg());
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {

            }
        });
    }

    /**
     * 确认是否退出弹框
     */
    private void showAddDialog() {
        dialogOne = builder.setTitle("该账号已注销，无法加入群聊。")
                .setShowLeftText(false)
                .setRightText("知道了")
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //继续认证
                        dialogOne.dismiss();
                    }
                })
                .build();
        dialogOne.show();
    }

}
