package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.MultiListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class BlacklistActivity extends AppActivity {

    private List<UserInfo> blacklist = new ArrayList<>();
    private UserAction userAction = new UserAction();
    private HeadView mHeadView;
    private MultiListView mMtListView;
    private UserDao userDao = new UserDao();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mMtListView = findViewById(R.id.mtListView);
        mMtListView.init(new BlackAdapter());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new EventRefreshFriend());
    }

    private void initData() {
        taskBlacklist();
    }


    private void taskBlacklist() {
        userAction.friendGet4Black(new CallBack<ReturnBean<List<UserInfo>>>(mMtListView) {
            @Override
            public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    blacklist = response.body().getData();
                    mMtListView.notifyDataSetChange();
                }

            }
        });
    }


    private void taskFriendBlackRemove(final Long uid, final int postion) {
        userAction.friendBlackRemove(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(context, response.body().getMsg());
                if (response.body().isOk()) {
                    blacklist.remove(postion);
                    mMtListView.notifyDataSetChange();

                    if (userDao.isUserExist(uid)) {
                        updateUserStatus(uid);
                        MessageManager.getInstance().notifyRefreshFriend(true, uid, CoreEnum.ERosterAction.BLACK);
                        new MsgDao().sessionCreate("", uid, SocketData.getCurrentTime());
                    } else {
                        getUserInfo(uid);
                    }

                }
            }
        });
    }

    private void updateUserStatus(long uid) {
        UserDao dao = new UserDao();
        dao.updateUserUtype(uid, ChatEnum.EUserType.FRIEND);
    }


    class BlackAdapter extends RecyclerView.Adapter<BlackAdapter.BlackViewHodler> {


        @Override
        public BlackViewHodler onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = inflater.inflate(R.layout.item_blacklist, viewGroup, false);
            return new BlackViewHodler(view);
        }

        @Override
        public void onBindViewHolder(BlackViewHodler viewHolder, final int i) {
            final UserInfo userInfo = blacklist.get(i);
            //viewHolder.mImgHead.setImageURI(userInfo.getHead() + "");
            Glide.with(context).load(userInfo.getHead())
                    .apply(GlideOptionsUtil.headImageOptions()).into(viewHolder.mImgHead);

            viewHolder.mTxtTime.setText(userInfo.getName());
            viewHolder.mBtnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskFriendBlackRemove(userInfo.getUid(), i);
                }
            });

            viewHolder.mImgHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.ID, userInfo.getUid());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (blacklist != null) {
                return blacklist.size();
            }
            return 0;
        }


        class BlackViewHodler extends RecyclerView.ViewHolder {
            private ImageView mImgHead;
            private TextView mTxtTime;
            private Button mBtnDel;


            public BlackViewHodler(@NonNull View itemView) {
                super(itemView);
                mImgHead = itemView.findViewById(R.id.img_head);
                mTxtTime = itemView.findViewById(R.id.txt_time);
                mBtnDel = itemView.findViewById(R.id.btn_del);
            }
        }

    }

    public void getUserInfo(Long uid) {
        if (uid == null) {
            return;
        }
        userAction.getUserInfoById(uid, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                if (response.body() == null || response.body().getData() == null) {
                    return;
                }
                final UserInfo info = response.body().getData();
                if (info != null) {
                    info.setuType(ChatEnum.EUserType.FRIEND);
                    userDao.updateUserinfo(info);
                    MessageManager.getInstance().notifyRefreshFriend(true, uid, CoreEnum.ERosterAction.BLACK);
                    new MsgDao().sessionCreate("", uid, SocketData.getCurrentTime());
                }
            }
        });
    }


}
