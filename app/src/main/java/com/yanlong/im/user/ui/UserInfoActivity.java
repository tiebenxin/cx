package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.nim_lib.config.Preferences;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.SingleMeberInfoBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.chat.ui.groupmanager.GroupMemPowerSetActivity;
import com.yanlong.im.chat.ui.groupmanager.SetupGroupMemberLableActivity;
import com.yanlong.im.circle.mycircle.FriendTrendsActivity;
import com.yanlong.im.circle.mycircle.MyTrendsActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DataUtils;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.CloseActivityEvent;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.RefreshApplyEvent;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static net.cb.cb.library.CoreEnum.ERosterAction.REMOVE_FRIEND;
import static net.cb.cb.library.CoreEnum.ERosterAction.UPDATE_INFO;

/***
 * 资料界面
 * txtMkname显示优先级;
 * 是好友，有备注，优先显示
 * 非好友，有群聊备注
 * 用户昵称
 */
public class UserInfoActivity extends AppActivity {
    public static final int SETING_REMARK = 1000;
    public static final int SEND_VERIFY = 1;
    public static final String ID = "id";
    public static final String SAY_HI = "sayHi";
    public static final String IS_APPLY = "isApply";
    public static final String GID = "gid";
    public static final String JION_TYPE_SHOW = "joinTypeShow";
    public static final String IS_BUSINESS_CARD = "isBusinessCard"; //是否是群名片路径
    public static final String MUC_NICK = "mucNick";//
    public static final String FROM = "from";//从哪个页面跳转过来
    public static final String IS_GROUP = "isGroup";// 是否是群跳转过来
    public static final String IS_ADMINS = "isAdmins";// 是否是群主
    public static final String ALIAS = "alias";
    public static final String CONTACT_NAME = "contactName";// 通讯录名称

    private HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewHead;
    private ImageView imgHead;
    private TextView tvFirstName;
    private TextView tvSecondName;
    private TextView tvThirdName;
    private TextView mTvRemark;
    private EditText mEtNote;
    private LinearLayout viewMkname;
    private LinearLayout viewBlack;
    private LinearLayout viewDel;
    private LinearLayout viewComplaint;
    private LinearLayout mLayoutMsg;
    private LinearLayout mViewSettingName;
    private LinearLayout mViewSettingPower;
    private LinearLayout mviewSettingLabel;
    private LinearLayout mViewLabel;
    private LinearLayout mViewPower;
    private LinearLayout mViewSettingNote;
    private Button mBtnAdd;
    private Button btnMsg;
    private TextView txtPower;
    //是否操作了删除联系人
    private boolean isDeleteUser = false;

    private int type; //0.已经是好友 1.不是好友添加好友 2.黑名单 3.自己
    private int isApply;//是否是好友申请 0 不是 1.是
    private int joinTypeShow;//0 不显示  1.显示
    private int joinType;
    private int fromPosition;//从广场哪个位置跳转过来
    private String gid;
    private String mAlias;// 通讯录昵称
    private String inviterName;
    private boolean mIsFromGroup;// 是否是来自群聊
    private boolean mIsAdmin;// 是否是群主或管理员
    private long inviter;
    private long id;
    private String sayHi, userNote;
    private UserAction userAction;
    private String mkName;
    private String name;
    private String contactName;
    private TextView tvBlack;
    private LinearLayout viewJoinGroupType;
    private TextView tvJoinGroupType;
    private LinearLayout viewIntroduce;
    private TextView tv_introduce;
    private TextView tvJoinGroupName;
    private LinearLayout layoutTrends;
    private ImageView ivOne;//朋友圈最近4条动态
    private ImageView ivTwo;
    private ImageView ivThree;
    private ImageView ivFour;
    private String mucNick;
    private int contactIntimately;
    private UserInfo userInfoLocal;
    private Group group;
    private SingleMeberInfoBean singleMeberInfoBean;// 单个群成员信息

    private String fromWhere;
    private AlertYesNo alertYesNo = null;

    private int friendDeactivateStat = 0;//该用户的注销状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        alertYesNo = new AlertYesNo();
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        viewJoinGroupType = findViewById(R.id.view_join_group_type);
        tvJoinGroupType = findViewById(R.id.tv_join_group_type);
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewHead = findViewById(R.id.view_head);
        imgHead = findViewById(R.id.img_head);
        tvFirstName = findViewById(R.id.txt_mkname);
        tvSecondName = findViewById(R.id.txt_nkname);
        tvThirdName = findViewById(R.id.txt_pr_no);
        viewMkname = findViewById(R.id.view_mkname);
        viewBlack = findViewById(R.id.view_black);
        viewDel = findViewById(R.id.view_del);
        viewComplaint = findViewById(R.id.view_complaint);
        btnMsg = findViewById(R.id.btn_msg);
        mLayoutMsg = findViewById(R.id.layout_msg);
        mBtnAdd = findViewById(R.id.btn_add);
        mTvRemark = findViewById(R.id.tv_remark);
        mEtNote = findViewById(R.id.et_note);
        mViewSettingName = findViewById(R.id.view_setting_name);
        tvBlack = findViewById(R.id.tv_black);
        viewIntroduce = findViewById(R.id.view_introduce);
        tv_introduce = findViewById(R.id.tv_introduce);
        tvJoinGroupName = findViewById(R.id.tv_join_group_name);
        mViewSettingNote = findViewById(R.id.view_setting_note);

        mViewSettingPower = findViewById(R.id.view_setting_power);
        mviewSettingLabel = findViewById(R.id.view_setting_label);
        mViewLabel = findViewById(R.id.view_label);
        mViewPower = findViewById(R.id.view_power);
        txtPower = findViewById(R.id.txt_power);
        layoutTrends = findViewById(R.id.layout_trends);
        ivOne = findViewById(R.id.iv_one);
        ivTwo = findViewById(R.id.iv_two);
        ivThree = findViewById(R.id.iv_three);
        ivFour = findViewById(R.id.iv_four);
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

        btnMsg.setText("发送消息");
        btnMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EventBus.getDefault().post(new CloseActivityEvent("ChatInfoActivity,GroupInfoActivity"));
                EventBus.getDefault().post(new EventExitChat());
                startActivity(new Intent(getContext(), ChatActivity.class)
                        .putExtra(ChatActivity.AGM_TOUID, id));
                finish();
            }
        });

        viewBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    alertYesNo.init(UserInfoActivity.this, "提示",
                            "加入黑名单，你将不再收到对方的消息", "确定", "取消", new AlertYesNo.Event() {
                                @Override
                                public void onON() {

                                }

                                @Override
                                public void onYes() {
                                    taskFriendBlack(id);
                                }
                            });
                } else if (type == 2) {
                    alertYesNo.init(UserInfoActivity.this, "提示",
                            "解除黑名单，你将可以与对方发送消息", "确定", "取消", new AlertYesNo.Event() {
                                @Override
                                public void onON() {

                                }

                                @Override
                                public void onYes() {
                                    taskFriendBlackRemove(id);
                                }
                            });
                }
                alertYesNo.show();
            }
        });

        viewComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, ComplaintActivity.class);
                intent.putExtra(ComplaintActivity.UID, id + "");
                intent.putExtra(ComplaintActivity.FROM_WHERE, 0);
                startActivity(intent);
            }
        });
        viewDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                    ToastUtil.show(getResources().getString(R.string.user_disable_message));
                    return;
                }
                alertYesNo.init(UserInfoActivity.this, "提示",
                        "删除联系人，将在双方好友列表里同时删除，并删除与该联系人的聊天记录", "确定", "取消", new AlertYesNo.Event() {
                            @Override
                            public void onON() {

                            }

                            @Override
                            public void onYes() {
                                taskDelFriend(id);
                            }
                        });
                alertYesNo.show();
            }
        });


        viewMkname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                    ToastUtil.show(getResources().getString(R.string.user_disable_message));
                    return;
                }
                Intent intent = new Intent(UserInfoActivity.this, CommonSetingActivity.class);
                intent.putExtra(CommonSetingActivity.TITLE, "设置备注");
                intent.putExtra(CommonSetingActivity.REMMARK, "备注");
                intent.putExtra(CommonSetingActivity.HINT, "备注名称");
                intent.putExtra(CommonSetingActivity.SIZE, 16);
                intent.putExtra(CommonSetingActivity.SETING, mkName);
                startActivityForResult(intent, SETING_REMARK);

            }
        });

        mEtNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && TextUtils.isEmpty(mEtNote.getText().toString())) {
                    if (!TextUtils.isEmpty(contactName)) {
                        mEtNote.setText(contactName);
                    } else if (!TextUtils.isEmpty(userNote)) {
                        mEtNote.setText(userNote);
                    }
                }
            }
        });

        mViewLabel.setOnClickListener(o -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(Preferences.TOGID, gid);
            bundle.putLong(Preferences.TOUID, id);
            IntentUtil.gotoActivity(this, SetupGroupMemberLableActivity.class, bundle);

        });
        mViewPower.setOnClickListener(o -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(Preferences.TOGID, gid);
            bundle.putLong(Preferences.TOUID, id);
            if (singleMeberInfoBean != null) {
                bundle.putString(Preferences.DATA, new Gson().toJson(singleMeberInfoBean));
            }
            IntentUtil.gotoActivity(this, GroupMemPowerSetActivity.class, bundle);
        });

        if (isApply == 0) {
            actionbar.setTitle("详细资料");
            mBtnAdd.setText("添加好友");
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                        ToastUtil.show(getResources().getString(R.string.user_disable_message));
                        return;
                    }
                    if (friendDeactivateStat == -1) {// 已注销
                        ToastUtil.show("该账号不存在");
                        return;
                    }
                    toSendVerifyActivity();
                }
            });
        } else {
            mViewSettingNote.setVisibility(View.VISIBLE);
            actionbar.setTitle("朋友验证");
            mBtnAdd.setText("通过验证");
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                        ToastUtil.show(getResources().getString(R.string.user_disable_message));
                        return;
                    }
                    if (TextUtils.isEmpty(mEtNote.getText().toString().trim())) {
                        if (TextUtils.isEmpty(contactName)) {
                            taskFriendAgree(id, userNote);
                        } else {
                            taskFriendAgree(id, contactName);
                        }
                    } else {
                        taskFriendAgree(id, mEtNote.getText().toString().trim());
                    }
                }
            });
        }

    }

    private void toSendVerifyActivity() {
        IUser myInfo = UserAction.getMyInfo();
        if (myInfo == null) {
            return;
        }
        String content = "我是" + myInfo.getName();
        if (group != null) {
            String name = group.getName();
            if (!TextUtils.isEmpty(name)) {
                String userName = group.getMygroupName();
                if (TextUtils.isEmpty(userName)) {
                    userName = myInfo.getName();
                }
                content = "我是群聊" + "\"" + name + "\"" + "的" + userName;
            }
        }
        Intent intent = new Intent(UserInfoActivity.this, FriendVerifyActivity.class);
        intent.putExtra(FriendVerifyActivity.CONTENT, content);
        intent.putExtra(FriendVerifyActivity.USER_ID, id);
        if (userInfoLocal != null) {
            if (TextUtils.isEmpty(mAlias)) {
                intent.putExtra(FriendVerifyActivity.NICK_NAME, userInfoLocal.getName());
            } else {
                intent.putExtra(FriendVerifyActivity.NICK_NAME, mAlias);
            }
        }
        startActivityForResult(intent, SEND_VERIFY);
    }

    private void initData() {
        userAction = new UserAction();
        Intent intent = getIntent();
        mucNick = intent.getStringExtra(MUC_NICK);
        id = intent.getLongExtra(ID, 0);
        sayHi = intent.getStringExtra(SAY_HI);
        isApply = intent.getIntExtra(IS_APPLY, 0);
        joinTypeShow = intent.getIntExtra(JION_TYPE_SHOW, 0);
        contactIntimately = intent.getIntExtra(IS_BUSINESS_CARD, 0);
        gid = intent.getStringExtra(GID);
        fromWhere = intent.getStringExtra(FROM);
        mIsFromGroup = intent.getBooleanExtra(IS_GROUP, false);
        mIsAdmin = intent.getBooleanExtra(IS_ADMINS, false);
        mAlias = intent.getStringExtra(ALIAS);
        contactName = intent.getStringExtra(CONTACT_NAME);
        fromPosition = intent.getIntExtra(FriendTrendsActivity.POSITION,0);

        taskFindExist();
        if (!TextUtils.isEmpty(gid)) {
            taskGroupInfo(gid);
            getSingleMemberInfo();
        }
        taskUserInfo(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SETING_REMARK:
                    String content = data.getStringExtra(CommonSetingActivity.CONTENT);
                    taskFriendMark(id, content);
                    break;
                case SEND_VERIFY:
                    finish();
                    break;
            }
        }
    }

    /**
     * @param type 0.已经是好友 1.不是好友添加好友 2.黑名单 3.自己
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setItemShow(int type) {
        try {
            System.out.println(UserInfoActivity.class.getSimpleName() + "--stat=" + type);
            viewComplaint.setVisibility(View.VISIBLE);
            if (type == 0) {
                mLayoutMsg.setVisibility(View.VISIBLE);
                btnMsg.setVisibility(View.VISIBLE);
                mBtnAdd.setVisibility(View.GONE);
                mViewSettingName.setVisibility(View.VISIBLE);
                tvBlack.setText("加入黑名单");
                viewIntroduce.setVisibility(View.GONE);
                checkPower();
            } else if (type == 1) {
                mLayoutMsg.setVisibility(View.GONE);
                btnMsg.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(gid)) {
                    if (group != null && group.getContactIntimately() != null) {
                        if (group.getContactIntimately() == 1 && !group.getMaster().equals("" + UserAction.getMyId())) {
                            mBtnAdd.setVisibility(View.GONE);
                        }
                    }
                } else {
                    mBtnAdd.setVisibility(View.VISIBLE);
                }
                mViewSettingName.setVisibility(View.GONE);
                String nameNote = mkName;
                if (TextUtils.isEmpty(nameNote)) {
                    nameNote = name;
                    userNote = nameNote;
                }
                if (TextUtils.isEmpty(sayHi)) {
                    mTvRemark.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(contactName)) {
                        mEtNote.setHint(nameNote);
                    } else {
                        mEtNote.setHint(contactName);
                    }
                } else {
                    mTvRemark.setVisibility(View.VISIBLE);
                    mTvRemark.setTextColor(AppConfig.getColor(R.color.gray_300));
                    mTvRemark.setText(sayHi);
                    if (TextUtils.isEmpty(contactName)) {
                        if (sayHi.startsWith("我是") && !sayHi.startsWith("我是群聊")) {
                            mEtNote.setHint(sayHi.substring(2));
                            userNote = sayHi.substring(2);
                        } else {
                            mEtNote.setHint(nameNote);
                        }
                    } else {
                        mEtNote.setHint(contactName);
                    }
                }
                mEtNote.setSelection(mEtNote.getText().toString().length());
                viewIntroduce.setVisibility(View.GONE);
                checkPower();
            } else if (type == 2) {
                mLayoutMsg.setVisibility(View.VISIBLE);
                btnMsg.setVisibility(View.VISIBLE);
                mBtnAdd.setVisibility(View.GONE);
                mViewSettingName.setVisibility(View.VISIBLE);
                tvBlack.setText("解除黑名单");
                viewIntroduce.setVisibility(View.GONE);
            }

            if (joinTypeShow != 0) {
                taskGroupInfo(gid);
            } else {
                if (contactIntimately == 1) {
                    mBtnAdd.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 更新用户信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshFriend(EventRefreshFriend event) {
        if (event.getUid() == id) {
            if (event.isLocal()) {
                @CoreEnum.ERosterAction int action = event.getRosterAction();
                if (action == UPDATE_INFO) {
                    taskUserInfo(id);
                } else if (action == REMOVE_FRIEND && !isDeleteUser) {//PC端删除了好友
                    userInfoLocal = userAction.getUserInfoInLocal(id);
                    if (userInfoLocal != null) {
                        if (alertYesNo != null && alertYesNo.isShowing()) alertYesNo.dismiss();
                        type = 1;
                        setData(userInfoLocal);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (alertYesNo != null && alertYesNo.isShowing()) alertYesNo.dismiss();
        super.onDestroy();
    }

    private void taskUserInfo(Long id) {
        if (id == 1L || id == 3L) {
            UserInfo info = userDao.findUserInfo(id);
            if (info != null) {
                setData(info);
            }
        } else {
            userInfoLocal = userAction.getUserInfoInLocal(id);
            if (userInfoLocal != null) {
                setData(userInfoLocal);
                userDao.updateUserinfo(userInfoLocal);
            }
            //系统用户不需要更新用户信息
            if (userInfoLocal != null && userInfoLocal.getuType() == ChatEnum.EUserType.ASSISTANT) {
                return;
            }
            userAction.getUserInfoByIdShowTrends(id, new CallBack<ReturnBean<UserInfo>>() {
                @Override
                public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                    if (response.body() == null || response.body().getData() == null) {
                        return;
                    }
                    UserInfo userInfo = response.body().getData();
                    if (userInfo.getStat() == 0) {
                        userInfo.setuType(ChatEnum.EUserType.FRIEND);
                    } else if (userInfo.getStat() == 2) {
                        userInfo.setuType(ChatEnum.EUserType.BLACK);
                    } else if (userInfo.getStat() == 1) {
                        userInfo.setuType(ChatEnum.EUserType.STRANGE);
                    } else if (userInfo.getStat() == 9) {
                        userInfo.setuType(ChatEnum.EUserType.ASSISTANT);
                    }
                    if (userInfoLocal == null) {
                        userInfoLocal = userInfo;
                    }
                    userDao.updateUserinfo(userInfo);//刷新用户数据，主要更新注销状态
                    setData(userInfo);
                    friendDeactivateStat = userInfo.getFriendDeactivateStat();
                }
            });
            layoutTrends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (id == UserAction.getMyInfo().getUid().longValue()) {
                        Intent intent = new Intent(UserInfoActivity.this, MyTrendsActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(UserInfoActivity.this, FriendTrendsActivity.class);
                        intent.putExtra("uid", id);
                        intent.putExtra(FriendTrendsActivity.POSITION, fromPosition);
                        intent.putExtra(FROM, fromWhere);
                        startActivity(intent);
                    }
                }
            });
        }
    }


    private void setData(final UserInfo info) {
        // 处理 You cannot start a load for a destroyed activity问题
        if (isFinishing()) {
            return;
        }
        Glide.with(this).load(info.getHead())
                .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);

        doGetAndSetName(info);
        mkName = info.getMkName();
        name = info.getName();
        //黑名单，且是好友
        if ((info.getuType() != null && info.getuType() == 3) || (info.getStat() != null && info.getStat() == 2 && info.getuType() != 0)) {
            type = 2;
        }
        if (info.getStat() != 9) {//不是常信小助手
            setItemShow(type);
        } else {
            tvFirstName.setVisibility(View.VISIBLE);
            tvSecondName.setVisibility(View.GONE);
            tvThirdName.setVisibility(View.GONE);
            mViewSettingName.setVisibility(View.GONE);
            mLayoutMsg.setVisibility(View.GONE);
            btnMsg.setVisibility(View.GONE);
            viewIntroduce.setVisibility(View.VISIBLE);
            mBtnAdd.setVisibility(View.GONE);
            viewComplaint.setVisibility(View.GONE);
        }
        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<LocalMedia> selectList = new ArrayList<>();
                LocalMedia lc = new LocalMedia();
                lc.setPath(info.getHead());
                selectList.add(lc);
                PictureSelector.create(UserInfoActivity.this)
                        .themeStyle(R.style.picture_default_style)
                        .isGif(false)
                        .openExternalPreviewImage(0, selectList);
            }
        });

        if (!TextUtils.isEmpty(info.getDescribe())) {
            tv_introduce.setText(info.getDescribe());
        }

        if (info.getMomentList() != null && info.getMomentList().size() > 0) {
            switch (info.getMomentList().size()) {
                case 1:
                    ivOne.setVisibility(View.VISIBLE);
                    ivTwo.setVisibility(View.GONE);
                    ivThree.setVisibility(View.GONE);
                    ivFour.setVisibility(View.GONE);
                    Glide.with(this).load(info.getMomentList().get(0))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivOne);
                    break;
                case 2:
                    ivOne.setVisibility(View.VISIBLE);
                    ivTwo.setVisibility(View.VISIBLE);
                    ivThree.setVisibility(View.GONE);
                    ivFour.setVisibility(View.GONE);
                    Glide.with(this).load(info.getMomentList().get(0))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivOne);
                    Glide.with(this).load(info.getMomentList().get(1))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivTwo);
                    break;
                case 3:
                    ivOne.setVisibility(View.VISIBLE);
                    ivTwo.setVisibility(View.VISIBLE);
                    ivThree.setVisibility(View.VISIBLE);
                    ivFour.setVisibility(View.GONE);
                    Glide.with(this).load(info.getMomentList().get(0))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivOne);
                    Glide.with(this).load(info.getMomentList().get(1))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivTwo);
                    Glide.with(this).load(info.getMomentList().get(2))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivThree);
                    break;
                case 4:
                    ivOne.setVisibility(View.VISIBLE);
                    ivTwo.setVisibility(View.VISIBLE);
                    ivThree.setVisibility(View.VISIBLE);
                    ivFour.setVisibility(View.VISIBLE);
                    Glide.with(this).load(info.getMomentList().get(0))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivOne);
                    Glide.with(this).load(info.getMomentList().get(1))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivTwo);
                    Glide.with(this).load(info.getMomentList().get(2))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivThree);
                    Glide.with(this).load(info.getMomentList().get(3))
                            .apply(GlideOptionsUtil.headImageOptions()).into(ivFour);
                    break;
            }
        }

    }


    private void taskGroupInfo(String gid) {
        if (TextUtils.isEmpty(gid)) {
            LogUtil.writeLog("UserInfoActivity--请求群信息gid=null");
            return;
        }
        group = msgDao.getGroup4Id(gid);
        if (group == null) {
            new MsgAction().groupInfo4UserInfo(gid, new CallBack<ReturnBean<Group>>() {
                @Override
                public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                    super.onResponse(call, response);
                    if (response.body() == null) {
                        return;
                    }
                    if (response.body().isOk()) {
                        group = response.body().getData();
                        setGroupData(group);
                    }
                }
            });
        } else {
            setGroupData(group);
        }
    }


    private void setGroupData(Group group) {
        //9.2 开启保护就隐藏加好友
        if (group.getContactIntimately() != null) {
            if (group.getContactIntimately() == 1 && !group.getMaster().equals("" + UserAction.getMyId())) {
                mBtnAdd.setVisibility(View.GONE);
            } else {
                //uType=2 表示是好友
                if (userInfoLocal != null && userInfoLocal.getuType() != null && userInfoLocal.getuType() != ChatEnum.EUserType.FRIEND && userInfoLocal.getuType() != ChatEnum.EUserType.BLACK) {
                    mBtnAdd.setVisibility(View.VISIBLE);
                } else {
                    mBtnAdd.setVisibility(View.GONE);
                }
            }
        }

        checkPower();

        for (MemberUser bean : group.getUsers()) {
            if (bean.getUid() == id) {
                viewJoinGroupType.setVisibility(View.VISIBLE);
                inviterName = bean.getInviterName();
                joinType = bean.getJoinType();
                if (!TextUtils.isEmpty(bean.getInviter())) {
                    inviter = Long.valueOf(bean.getInviter());
                }

                if (joinType == 0) {
                    tvJoinGroupName.setText(inviterName);
                    tvJoinGroupType.setText("分享二维码邀请进群");
                } else {
                    tvJoinGroupName.setText(inviterName);
                    tvJoinGroupType.setText("邀请进群");
                }


                tvJoinGroupName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inviter == (UserAction.getMyId())) {
                            Intent intent = new Intent(UserInfoActivity.this, MyselfInfoActivity.class);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(UserInfoActivity.this, UserInfoActivity.class)
                                    .putExtra(UserInfoActivity.ID, inviter));
                        }
                    }
                });
            }
        }
    }

    /**
     * 检查是否有权限设置功能
     */
    private void checkPower() {

        if (group != null) {
            if (isAdmin()) {
                mViewSettingPower.setVisibility(View.VISIBLE);
                // mviewSettingLabel.setVisibility(View.VISIBLE);
            } else {
                if (isAdministrators(UserAction.getMyId())) {// 同级别不允许设置权限
                    if (!isAdministrators(id) && !group.getMaster().equals(id + "")) {
                        mViewSettingPower.setVisibility(View.VISIBLE);
                        // mviewSettingLabel.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private boolean isAdmin() {
        if (!StringUtil.isNotNull(group.getMaster()))
            return false;
        return group.getMaster().equals("" + UserAction.getMyId());
    }

    private boolean isAdministrators(Long uid) {
        boolean isManager = false;
        if (group.getViceAdmins() != null && group.getViceAdmins().size() > 0) {
            for (Long user : group.getViceAdmins()) {
                if (user.equals(uid)) {
                    isManager = true;
                    break;
                }
            }
        }
        return isManager;
    }


    private void taskAddFriend(Long id, String sayHi) {
        userAction.friendApply(id, sayHi, null, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
//                notifyRefreshRoster(id);
                if (response.body() == null) {
                    return;
                }

                ToastUtil.show(UserInfoActivity.this, response.body().getMsg());
                if (response.body().isOk()) {
                    finish();
//                    notifyRefreshRoster();
                }
            }
        });
    }

    /*
     * 加入黑名单
     * */
    private void taskFriendBlack(final Long id) {
        userAction.friendBlack(id, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                type = 2;
                tvBlack.setText("解除黑名单");
                userDao.updateUserUtype(id, 3);
                new MsgDao().sessionDel(id, "");
                ToastUtil.show(context, response.body().getMsg());
                notifyRefreshRoster(id, CoreEnum.ERosterAction.BLACK);
            }
        });
    }


    private void taskFriendBlackRemove(final Long uid) {
        userAction.friendBlackRemove(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                type = 0;
                tvBlack.setText("加入黑名单");
                userDao.updateUserUtype(id, 2);
                new MsgDao().sessionCreate("", id, SocketData.getCurrentTime());
                ToastUtil.show(context, response.body().getMsg());
                notifyRefreshRoster(uid, CoreEnum.ERosterAction.BLACK);
            }
        });
    }

    private void notifyRefreshRoster(long uid, @CoreEnum.ERosterAction int action) {
        EventRefreshFriend eventRefreshFriend = new EventRefreshFriend();
        eventRefreshFriend.setLocal(true);
        eventRefreshFriend.setUid(uid);
        eventRefreshFriend.setRosterAction(action);
        EventBus.getDefault().post(eventRefreshFriend);
    }


    private void taskDelFriend(final Long id) {
        userAction.friendDel(id, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(UserInfoActivity.this, response.body().getMsg());
                //刷新好友和退出
                if (response.body().isOk()) {
                    isDeleteUser = true;
                    //删除好友后 取消阅后即焚状态
                    userDao.updateReadDestroy(id, 0);
                    // 删除好友后，取消置顶状态
                    msgDao.updateUserSessionTop(id, 0);
                    if (MyAppLication.INSTANCE().repository != null)
                        MyAppLication.INSTANCE().repository.deleteSession(id, "");
                    MessageManager.getInstance().setMessageChange(true);
                    notifyRefreshRoster(id, REMOVE_FRIEND);
                    EventBus.getDefault().post(new CloseActivityEvent("ChatInfoActivity,GroupInfoActivity"));
                    EventBus.getDefault().post(new EventExitChat());
                    finish();
                }
            }
        });
    }

    private void taskFriendMark(final Long id, final String mark) {
        userAction.friendMark(id, mark, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                //6.3
                if (response.body().isOk()) {
                    updateUserInfo(mark);
                    notifyRefreshRoster(0, UPDATE_INFO);// TODO　id改成0 需要全部刷新，改变通讯录的位置
                    /********通知更新sessionDetail************************************/
                    //因为msg对象 uid有两个，都得添加
                    List<Long> uids = new ArrayList<>();
                    uids.add(id);
                    //回主线程调用更新session详情
                    if (MyAppLication.INSTANCE().repository != null)
                        MyAppLication.INSTANCE().repository.updateSessionDetail(null, uids);
                    /********通知更新sessionDetail end************************************/
                }
                taskUserInfo(id);
                ToastUtil.show(UserInfoActivity.this, response.body().getMsg());
            }
        });

    }

    private void updateUserInfo(String mark) {
        if (userInfoLocal != null) {
            userInfoLocal.setMkName(mark);
            userDao.updateUserinfo(userInfoLocal);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventFactory.UpdateUserInfoEvent event) {
        getSingleMemberInfo();
    }


    private void taskFriendAgree(final Long uid, String contactName) {
        userAction.friendAgree(uid, mAlias, contactName, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    notifyRefreshRoster(uid, CoreEnum.ERosterAction.ACCEPT_BE_FRIENDS);
                    EventBus.getDefault().post(new RefreshApplyEvent(uid, CoreEnum.EChatType.PRIVATE, 1));
                    finish();
                }
            }
        });
    }

    /**
     * 获取单个群成员信息
     */
    private void getSingleMemberInfo() {
        userAction.getSingleMemberInfo(gid, Integer.parseInt(id + ""), new CallBack<ReturnBean<SingleMeberInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SingleMeberInfoBean>> call, Response<ReturnBean<SingleMeberInfoBean>> response) {
                super.onResponse(call, response);
                if (response != null && response.body() != null && response.body().isOk()) {
                    singleMeberInfoBean = response.body().getData();
                    updateUserInfo();
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<SingleMeberInfoBean>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void updateUserInfo() {
        boolean value = singleMeberInfoBean.isCantOpenUpRedEnv();
        String time = GroupMemPowerSetActivity.getSurvivaltime(singleMeberInfoBean.getShutUpDuration());
        StringBuffer stringBuffer = new StringBuffer();
        if (value) {
            stringBuffer.append("禁领红包");
        }
        if (time.equals("关闭")) {
            txtPower.setText("关闭");
        } else {
            if (TextUtils.isEmpty(stringBuffer)) {
                stringBuffer.append("禁言" + time);
            } else {
                stringBuffer.append(",禁言" + time);
            }
            txtPower.setText(stringBuffer.toString());
        }

    }


    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();

    /***
     * 判断用户是否在好友里面
     */
    private void taskFindExist() {
        if (id == UserAction.getMyId()) {
            type = 3;
            return;
        }

        type = userDao.findUserInfo4Friend(id) == null ? 1 : 0;
        if (type == 1) {
            UserInfo info = userDao.findUserInfo(id);
            if (info != null && info.getuType() != null && info.getuType() == 3) {
                type = 2;
            }
        }
    }

    /*
     * 设置名字
     * */
    private void doGetAndSetName(UserInfo userInfo) {
        String userRemark = "";
        String userNick = "";
        String imId = "";
        if (userInfo != null) {
            userRemark = userInfo.getMkName();
            userNick = userInfo.getName();
            imId = userInfo.getImid();

        }
        if (userInfo.getuType() == ChatEnum.EUserType.FRIEND || userInfo.getStat() == 0) {//有好友关系
            if (!TextUtils.isEmpty(userRemark)) {
                if (!TextUtils.isEmpty(mucNick) && mIsFromGroup) {
                    tvFirstName.setText(userRemark);
                    tvSecondName.setText("昵称: " + userNick);
                    tvThirdName.setText("群昵称: " + mucNick);
                } else {
                    tvFirstName.setText(userRemark);
                    tvSecondName.setText("昵称: " + userNick);
                    tvThirdName.setText("常信号: " + imId);
                }
            } else {
                if (!TextUtils.isEmpty(mucNick) && mIsFromGroup) {
                    tvFirstName.setText(userNick);
                    tvSecondName.setText("常信号: " + imId);
                    tvThirdName.setText("群昵称: " + mucNick);
                } else {
                    tvFirstName.setText(userNick);
                    tvSecondName.setText("昵称: " + userNick);
                    tvThirdName.setText("常信号: " + imId);
                }
            }
        } else {//无好友关系
            if (!TextUtils.isEmpty(userRemark)) {// 拉黑名单后需要优先显示备注名称
                if (!TextUtils.isEmpty(mucNick) && mIsFromGroup) {
                    tvFirstName.setText(userRemark);
                    tvSecondName.setText("昵称: " + userNick);
                    tvThirdName.setText("群昵称: " + mucNick);
                } else {
                    tvFirstName.setText(userRemark);
                    tvSecondName.setText("昵称: " + userNick);
                    tvThirdName.setText("常信号: " + DataUtils.getHideData(imId, 3));
                }
            } else if (!TextUtils.isEmpty(mucNick) && mIsFromGroup) {
                tvFirstName.setText(userNick);
                tvSecondName.setText("群昵称: " + mucNick);
                tvThirdName.setText("常信号: " + DataUtils.getHideData(imId, 3));
            } else {
                tvFirstName.setText(userNick);
                tvSecondName.setText("昵称: " + userNick);
                tvThirdName.setText("常信号: " + DataUtils.getHideData(imId, 3));
            }
        }
    }

}
