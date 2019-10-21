package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ReadDestroyBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.ReadDestroyUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
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

public class ChatInfoActivity extends AppActivity {
    public static final String AGM_FUID = "fuid";
    private Long fuid;

    private HeadView headView;
    private ActionbarView actionbar;
    private RecyclerView topListView;
    private LinearLayout viewLog;
    private LinearLayout viewTop;
    private CheckBox ckTop;
    private LinearLayout viewDisturb;
    private CheckBox ckDisturb;
    private LinearLayout viewLogClean;
    private LinearLayout viewFeedback;
    //  private Session session;
    private UserInfo fUserInfo;
    private CheckBox ckRedDestroy;
    private LinearLayout viewExitDestroy;
    private CheckBox ckExitDestroy;
    private LinearLayout viewDestroyTime;
    private TextView tvDestroyTime;
    private SeekBar sbDestroyTime;
    private int destroyTime;
    private ReadDestroyUtil readDestroyUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);
        findViews();
        initEvent();
        initData();
        controlDestroyView();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ToastUtil.show(context, destroyTime + "---" + fuid);
        taskSurvivalTime(fuid, destroyTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setingReadDestroy(ReadDestroyBean bean) {

        if (bean.uid == fuid) {
            destroyTime = bean.survivaltime;
            if (destroyTime == -1) {
                ckRedDestroy.setChecked(true);
                ckExitDestroy.setChecked(true);
                viewExitDestroy.setVisibility(View.VISIBLE);
                viewDestroyTime.setVisibility(View.GONE);
            } else if (destroyTime == 0) {
                ckRedDestroy.setChecked(false);
                ckExitDestroy.setChecked(false);
                viewExitDestroy.setVisibility(View.GONE);
                viewDestroyTime.setVisibility(View.GONE);
            } else {
                ckRedDestroy.setChecked(true);
                ckExitDestroy.setChecked(false);
                viewExitDestroy.setVisibility(View.VISIBLE);
                viewDestroyTime.setVisibility(View.VISIBLE);
                readDestroyUtil.initSeekBarnProgress(sbDestroyTime, destroyTime);
                tvDestroyTime.setText(readDestroyUtil.formatDateTime(destroyTime));
            }
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
        viewFeedback = findViewById(R.id.view_feedback);
        ckRedDestroy = findViewById(R.id.ck_red_destroy);
        viewExitDestroy = findViewById(R.id.view_exit_destroy);
        ckExitDestroy = findViewById(R.id.ck_exit_destroy);
        viewDestroyTime = findViewById(R.id.view_destroy_time);
        tvDestroyTime = findViewById(R.id.tv_destroy_time);
        sbDestroyTime = findViewById(R.id.sb_destroy_time);
    }


    //自动生成的控件事件
    private void initEvent() {
        fuid = getIntent().getLongExtra(AGM_FUID, 0);
        taskGetInfo();

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

        ckTop.setChecked(fUserInfo.getIstop() == 1);
        ckTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fUserInfo.setIstop(isChecked ? 1 : 0);
                taskSaveInfo();
                taskUpSwitch(null, fUserInfo.getIstop());
            }
        });
        ckDisturb.setChecked(fUserInfo.getDisturb() == 1);
        ckDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fUserInfo.setDisturb(isChecked ? 1 : 0);
                taskSaveInfo();
                taskUpSwitch(fUserInfo.getDisturb(), null);
            }
        });
        viewLogClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNo alertYesNo = new AlertYesNo();
                alertYesNo.init(ChatInfoActivity.this, "删除", "确定清除聊天记录吗?", "确定", "取消", new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        taskDelMsg();
                    }
                });
                alertYesNo.show();

            }
        });

        viewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchMsgActivity.class)
                        .putExtra(SearchMsgActivity.AGM_FUID, fuid)
                );
            }
        });


        viewFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatInfoActivity.this, ComplaintActivity.class);
                intent.putExtra(ComplaintActivity.UID, fuid.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        setResult(ChatActivity.REQ_REFRESH);
    }


    private void initData() {
        readDestroyUtil = new ReadDestroyUtil();
        UserInfo userInfo = userDao.findUserInfo(fuid);
        destroyTime = userInfo.getDestroy();
        if (destroyTime == -1) {
            ckRedDestroy.setChecked(true);
            ckExitDestroy.setChecked(true);
            viewExitDestroy.setVisibility(View.VISIBLE);
            viewDestroyTime.setVisibility(View.GONE);
        } else if (destroyTime == 0) {
            ckRedDestroy.setChecked(false);
            ckExitDestroy.setChecked(false);
            viewExitDestroy.setVisibility(View.GONE);
            viewDestroyTime.setVisibility(View.GONE);
        } else {
            ckRedDestroy.setChecked(true);
            ckExitDestroy.setChecked(false);
            viewExitDestroy.setVisibility(View.VISIBLE);
            viewDestroyTime.setVisibility(View.VISIBLE);
            readDestroyUtil.initSeekBarnProgress(sbDestroyTime, destroyTime);
            tvDestroyTime.setText(readDestroyUtil.formatDateTime(destroyTime));
        }
    }


    private void controlDestroyView() {
        ckRedDestroy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewExitDestroy.setVisibility(View.VISIBLE);
                    viewDestroyTime.setVisibility(View.VISIBLE);
                    sbDestroyTime.setProgress(60);
                } else {
                    viewExitDestroy.setVisibility(View.GONE);
                    viewDestroyTime.setVisibility(View.GONE);
                    destroyTime = 0;
                }
            }
        });

        ckExitDestroy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewDestroyTime.setVisibility(View.GONE);
                    destroyTime = -1;
                } else {
                    viewDestroyTime.setVisibility(View.VISIBLE);
                    sbDestroyTime.setProgress(60);
                }
            }
        });

        sbDestroyTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                destroyTime = readDestroyUtil.setSeekBarnProgress(seekBar, progress, tvDestroyTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewTopAdapter extends RecyclerView.Adapter<RecyclerViewTopAdapter.RCViewTopHolder> {

        @Override
        public int getItemCount() {
            // return listDataTop == null ? 0 : listDataTop.size();
            return 2;
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
                    Glide.with(context).load(userInfo.getHead())
                            .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
        if (fUserInfo.getuType() != 2) {//非好友不能设置开关等
            ckDisturb.setEnabled(false);
            ckTop.setEnabled(false);

        } else {
            ckDisturb.setEnabled(true);
            ckTop.setEnabled(true);

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

    private void taskDelMsg() {
        msgDao.msgDel(fuid, null);
        EventBus.getDefault().post(new EventRefreshChat());
        ToastUtil.show(ChatInfoActivity.this, "删除成功");
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
                        msgDao.updateUserTop(fuid, istop.intValue());
                    } else if (isMute != null && istop == null) {
                        session = msgDao.updateUserSessionDisturb(fuid, isMute);
                        msgDao.updateUserDisturb(fuid, isMute.intValue());
                    }
                    MessageManager.getInstance().setMessageChange(true);
                    MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, fuid, "", CoreEnum.ESessionRefreshTag.SINGLE, session, true);
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
                    // ToastUtil.show(context,"设置成功");
                }
            }
        });
    }


}
