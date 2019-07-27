package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.server.MsgServer;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChatInfoActivity extends AppActivity {
    public static final String AGM_FUID = "fuid";
    private Long fuid;

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private android.support.v7.widget.RecyclerView topListView;
    private LinearLayout viewLog;
    private LinearLayout viewTop;
    private CheckBox ckTop;
    private LinearLayout viewDisturb;
    private CheckBox ckDisturb;
    private LinearLayout viewLogClean;
    private LinearLayout viewFeedback;
  //  private Session session;
    private UserInfo fUserInfo;


    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        topListView = (android.support.v7.widget.RecyclerView) findViewById(R.id.topListView);
        viewLog = (LinearLayout) findViewById(R.id.view_log);
        viewTop = (LinearLayout) findViewById(R.id.view_top);
        ckTop = (CheckBox) findViewById(R.id.ck_top);
        viewDisturb = (LinearLayout) findViewById(R.id.view_disturb);
        ckDisturb = (CheckBox) findViewById(R.id.ck_disturb);
        viewLogClean = (LinearLayout) findViewById(R.id.view_log_clean);
        viewFeedback = (LinearLayout) findViewById(R.id.view_feedback);
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
                taskUpSwitch(null,fUserInfo.getIstop());
            }
        });
        ckDisturb.setChecked(fUserInfo.getDisturb() == 1);
        ckDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fUserInfo.setDisturb(isChecked ? 1 : 0);
                taskSaveInfo();
                taskUpSwitch(fUserInfo.getDisturb(),null);
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


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(ChatActivity.REQ_REFRESH);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);
        findViews();
        initEvent();
        initData();
    }

    private void initData() {


    }


    private List<String> listDataTop = new ArrayList<>();

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

                    holder.imgHead.setImageURI(Uri.parse("" + userInfo.getHead()));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getContext(), UserInfoActivity.class)
                                    .putExtra(UserInfoActivity.ID, fUserInfo.getUid()));
                        }
                    });
                    break;
                case 1:

                    holder.imgHead.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.ic_group_a)).build());

                    holder.imgHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            EventBus.getDefault().post(new EventExitChat());
                            startActivity(new Intent(getContext(),GroupCreateActivity.class));
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
            private com.facebook.drawee.view.SimpleDraweeView imgHead;

            //自动寻找ViewHold
            public RCViewTopHolder(View convertView) {
                super(convertView);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
            }

        }
    }

    private MsgDao msgDao = new MsgDao();
    private MsgAction msgAction = new MsgAction();

    //获取会话和对方信息
    private void taskGetInfo() {
       /* session = DaoUtil.findOne(Session.class, "from_uid", fuid);
        if (session == null) {
            session = msgDao.sessionCreate(null, fuid);
        }*/
        fUserInfo = DaoUtil.findOne(UserInfo.class, "uid", fuid);
    }

    //更新配置
    private void taskSaveInfo() {
        DaoUtil.update(fUserInfo);
    }

    private void taskDelMsg() {
        msgDao.msgDel(fuid, null);
    }


    private void taskUpSwitch(Integer isMute, Integer istop) {
        msgAction.sessionSwitch(fuid, isMute, istop, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if(response.body()==null)
                    return;
                if (response.body().isOk()){

                }else {
                    ToastUtil.show(getContext(),response.body().getMsg());

                }
            }
        });
    }


}
