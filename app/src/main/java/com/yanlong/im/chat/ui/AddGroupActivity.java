package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupJoinBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.GroupHeadImageUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

//申请 加入群聊
public class AddGroupActivity extends AppActivity {
    public static final String GID = "gid";
    public static final String INVITER = "inviter";
    public static final String INVITER_NAME = "inviterName";
    private HeadView mHeadView;
    private ImageView mSdGroupHead;
    private TextView mTvGroupName;
    private TextView mTvGroupNum;
    private Button mBtnAddGroup;
    private EditText etContent;
    private MsgAction msgAction;
    private String gid;
    private String inviter;
    private String inviterName;
    private final MsgDao msgDao = new MsgDao();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mSdGroupHead = findViewById(R.id.sd_group_head);
        mTvGroupName = findViewById(R.id.tv_group_name);
        mTvGroupNum = findViewById(R.id.tv_group_num);
        mBtnAddGroup = findViewById(R.id.btn_add_group);
        etContent = findViewById(R.id.et_content);
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
        mBtnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskAddGroup(gid, inviter, inviterName);
                mBtnAddGroup.setEnabled(false);
            }
        });
    }


    private void initData() {
        msgAction = new MsgAction();
        gid = getIntent().getStringExtra(GID);
        inviter = getIntent().getStringExtra(INVITER);
        inviterName = getIntent().getStringExtra(INVITER_NAME);
        taskGroupInfo(gid);
    }


    private void taskGroupInfo(String gid) {
        msgAction.groupInfo(gid, true, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    Group bean = response.body().getData();
                    String url = "";
                    if (!TextUtils.isEmpty(bean.getAvatar())) {
                        url = bean.getAvatar();
                    }else {
                        MsgDao msgDao = new MsgDao();
                        url = msgDao.groupHeadImgGet(gid);
                        if(!StringUtil.isNotNull(url)){
                            //头像为空 创建一次
                            GroupHeadImageUtil.creatAndSaveImg(context,gid);
                            url = msgDao.groupHeadImgGet(gid);
                        }
                    }
                    Glide.with(context).load(url)
                            .apply(GlideOptionsUtil.headImageOptions()).into(mSdGroupHead);
                    mTvGroupName.setText(/*bean.getName()*/msgDao.getGroupName(bean));
                    mTvGroupNum.setText(bean.getUsers().size() + "人");
                } else {
                    ToastUtil.show(AddGroupActivity.this, response.body().getMsg());
                }
            }
        });
    }


    private void taskAddGroup(final String gid, final String inviter, String inviterName) {
        IUser userInfo = UserAction.getMyInfo();
        Long uid = userInfo.getUid();
        String path = userInfo.getHead();
        String name = userInfo.getName();
        String additional = etContent.getText().toString();
        new MsgAction().joinGroup(gid, uid, name, path, inviter, inviterName,additional, new CallBack<ReturnBean<GroupJoinBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<GroupJoinBean>> call, Response<ReturnBean<GroupJoinBean>> response) {
                if (response.body() == null) {
                    ToastUtil.show(AddGroupActivity.this, "加群失败");
                    mBtnAddGroup.setEnabled(true);
                    return;
                }
                if (response.body().isOk()) {
                    if (!response.body().getData().isPending()) {
                        ToastUtil.show(AddGroupActivity.this, response.body().getMsg());
                        Intent intent = new Intent(AddGroupActivity.this, ChatActivity.class);
                        intent.putExtra(ChatActivity.AGM_TOGID, gid);
                        startActivity(intent);
                        new MsgDao().sessionCreate(gid, null, SocketData.getCurrentTime());
                        MessageManager.getInstance().setMessageChange(true);
                    } else {
                        ToastUtil.show(AddGroupActivity.this, "申请成功,请等待群主验证");
                    }
                    finish();
                } else {
                    ToastUtil.show(context, response.body().getMsg());
                    mBtnAddGroup.setEnabled(true);
                }
            }
        });
    }


}
