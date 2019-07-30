package com.yanlong.im.user.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.ChatActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertTouch;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 资料界面
 */
public class UserInfoActivity extends AppActivity {
    public static final int SETING_REMARK = 1000;
    public static final String ID = "id";
    public static final String SAY_HI = "sayHi";
    public static final String IS_APPLY = "isApply";

    private HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewHead;
    private SimpleDraweeView imgHead;
    private TextView txtMkname;
    private TextView txtNkname;
    private TextView txtPrNo;
    private TextView mTvRemark;
    private LinearLayout viewMkname;
    private LinearLayout viewBlack;
    private LinearLayout viewDel;
    private LinearLayout viewComplaint;
    private LinearLayout mLayoutMsg;
    private LinearLayout mViewSettingName;
    private Button mBtnAdd;
    private Button btnMsg;

    private int type; //0.已经是好友 1.不是好友添加好友 2.黑名单
    private int isApply;//是否是好友申请 0 不是 1.是
    private int joinType;//0 不显示  1.显示
    private Long id;
    private String sayHi;
    private UserAction userAction;
    private String mkName;
    private String name;
    private TextView tvBlack;
    private LinearLayout viewJoinGroupType;
    private TextView tvJoinGroupType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        viewJoinGroupType = findViewById(R.id.view_join_group_type);
        tvJoinGroupType = findViewById(R.id.tv_join_group_type);
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewHead = findViewById(R.id.view_head);
        imgHead = findViewById(R.id.img_head);
        txtMkname = findViewById(R.id.txt_mkname);
        txtNkname = findViewById(R.id.txt_nkname);
        txtPrNo = findViewById(R.id.txt_pr_no);
        viewMkname = findViewById(R.id.view_mkname);
        viewBlack = findViewById(R.id.view_black);
        viewDel = findViewById(R.id.view_del);
        viewComplaint = findViewById(R.id.view_complaint);
        btnMsg = findViewById(R.id.btn_msg);
        mLayoutMsg = findViewById(R.id.layout_msg);
        mBtnAdd = findViewById(R.id.btn_add);
        mTvRemark = findViewById(R.id.tv_remark);
        mViewSettingName = findViewById(R.id.view_setting_name);
        tvBlack = findViewById(R.id.tv_black);
        id = getIntent().getLongExtra(ID, 0);
        sayHi = getIntent().getStringExtra(SAY_HI);
        isApply = getIntent().getIntExtra(IS_APPLY, 0);
        taskFindExist();
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
                    final AlertYesNo alertYesNo = new AlertYesNo();
                    alertYesNo.init(UserInfoActivity.this, "拉入黑名单",
                            "确定将此-好友拉入黑名单吗?", "确定", "取消", new AlertYesNo.Event() {
                                @Override
                                public void onON() {

                                }

                                @Override
                                public void onYes() {
                                    taskFriendBlack(id);
                                }
                            });
                    alertYesNo.show();
                } else if (type == 2) {
                    taskFriendBlackRemove(id);
                }
            }
        });

        viewComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(ComplaintActivity.class);
            }
        });
        viewDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertYesNo alertYesNo = new AlertYesNo();
                alertYesNo.init(UserInfoActivity.this, "删除好友",
                        "确定删除此好友吗?", "确定", "取消", new AlertYesNo.Event() {
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
                Intent intent = new Intent(UserInfoActivity.this, CommonSetingActivity.class);
                intent.putExtra(CommonSetingActivity.TITLE, "设置备注和描述");
                intent.putExtra(CommonSetingActivity.REMMARK, "设置备注和描述");
                intent.putExtra(CommonSetingActivity.HINT, "设置备注和描述");
                intent.putExtra(CommonSetingActivity.SIZE, 16);
                intent.putExtra(CommonSetingActivity.SETING, mkName);
                startActivityForResult(intent, SETING_REMARK);

            }
        });


        if (isApply == 0) {
            mBtnAdd.setText("添加好友");
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertTouch alertTouch = new AlertTouch();
                    alertTouch.init(UserInfoActivity.this, "好友验证", "确定", 0, new AlertTouch.Event() {
                        @Override
                        public void onON() {

                        }

                        @Override
                        public void onYes(String content) {
                            taskAddFriend(id, content);
                        }
                    });
                    alertTouch.show();
                    alertTouch.setContent("我是" + UserAction.getMyInfo().getName());
                    alertTouch.setEdHintOrSize(null, 60);
                }
            });
        } else {
            mBtnAdd.setText("接受邀请");
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskFriendAgree(id);
                }
            });
        }


    }


    private void initData() {
        userAction = new UserAction();
        id = getIntent().getLongExtra(ID, 0);
        taskUserInfo(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String content = data.getStringExtra(CommonSetingActivity.CONTENT);
            switch (requestCode) {
                case SETING_REMARK:
                    //6.15
                    //  if (!TextUtils.isEmpty(content)) {
                    taskFriendMark(id, content);
                    //  }
                    break;
            }
        }
    }


    private void setItemShow(int type) {
        if (type == 0) {
            mLayoutMsg.setVisibility(View.VISIBLE);
            mBtnAdd.setVisibility(View.GONE);
            mViewSettingName.setVisibility(View.VISIBLE);
            tvBlack.setText("加入黑名单");
        } else if (type == 1) {
            mLayoutMsg.setVisibility(View.GONE);
            mBtnAdd.setVisibility(View.VISIBLE);
            mViewSettingName.setVisibility(View.GONE);
            if (TextUtils.isEmpty(sayHi)) {
                mTvRemark.setVisibility(View.GONE);
            } else {
                mTvRemark.setVisibility(View.VISIBLE);
                mTvRemark.setText(sayHi);
            }
        } else if (type == 2) {
            mLayoutMsg.setVisibility(View.VISIBLE);
            mBtnAdd.setVisibility(View.GONE);
            mViewSettingName.setVisibility(View.VISIBLE);
            tvBlack.setText("解除黑名单");
        }
    }

    private void taskUserInfo(Long id) {
        userAction.getUserInfo4Id(id, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                if (response.body() == null || response.body().getData() == null) {
                    return;
                }
                final UserInfo info = response.body().getData();
                imgHead.setImageURI(Uri.parse("" + info.getHead()));
                txtMkname.setText(info.getName4Show());
                mkName = info.getMkName();
                txtPrNo.setText("常聊号: " + info.getImid());
                txtNkname.setText("昵称: " + info.getName());
                name = info.getName();

                if ((info.getuType() != null && info.getuType() == 3) || (info.getStat() != null && info.getStat() == 2)) {
                    type = 2;
                }

                setItemShow(type);
                imgHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<LocalMedia> selectList = new ArrayList<>();
                        LocalMedia lc = new LocalMedia();
                        lc.setCompressPath(info.getHead());
                        selectList.add(lc);
                        PictureSelector.create(UserInfoActivity.this)
                                .themeStyle(R.style.picture_default_style)
                                .isGif(true)
                                .openExternalPreview(0, selectList);
                    }
                });
            }
        });

    }


    private void taskAddFriend(Long id, String sayHi) {
        userAction.friendApply(id, sayHi, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                EventBus.getDefault().post(new EventRefreshFriend());
                if (response.body() == null) {
                    return;
                }

                ToastUtil.show(UserInfoActivity.this, response.body().getMsg());
                if (response.body().isOk()) {
                    finish();
                }

            }
        });
    }


    private void taskFriendBlack(final Long id) {
        userAction.friendBlack(id, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                EventBus.getDefault().post(new EventRefreshFriend());
                if (response.body() == null) {
                    return;
                }
                type = 2;
                tvBlack.setText("解除黑名单");
                userDao.updeteUserUtype(id, 3);
                ToastUtil.show(context, response.body().getMsg());

            }
        });
    }


    private void taskFriendBlackRemove(Long uid) {
        userAction.friendBlackRemove(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                type = 0;
                tvBlack.setText("加入黑名单");
                userDao.updeteUserUtype(id, 2);
                ToastUtil.show(context, response.body().getMsg());

            }
        });
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
                    userDao.updeteUserUtype(id, 0);
                    EventBus.getDefault().post(new EventRefreshFriend());
                    finish();
                }
            }
        });
    }

    private void taskFriendMark(final Long id, String mark) {
        userAction.friendMark(id, mark, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                //6.3
                if (response.body().isOk()) {
                    EventBus.getDefault().post(new EventRefreshFriend());
                    EventBus.getDefault().post(new EventRefreshMainMsg());
                }
                taskUserInfo(id);
                ToastUtil.show(UserInfoActivity.this, response.body().getMsg());
            }
        });

    }


    private void taskFriendAgree(Long uid) {
        userAction.friendAgree(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    finish();
                }

            }
        });
    }


    private UserDao userDao = new UserDao();

    /***
     * 判断用户是否在好友里面
     */
    private void taskFindExist() {
        type = userDao.findUserInfo4Friend(id) == null ? 1 : 0;
        if (type == 1) {
            UserInfo info = userDao.findUserInfo(id);
            if (info != null && info.getuType() != null && info.getuType() == 3) {
                type = 2;
            }
        }
    }


}
