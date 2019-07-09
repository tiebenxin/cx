package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.server.UserServer;
import com.yanlong.im.user.ui.CommonSetingActivity;
import com.yanlong.im.user.ui.MyselfQRCodeActivity;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.TouchUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupInfoActivity extends AppActivity {
    public static final int GROUP_NAME = 1000;
    public static final int GROUP_NICK = 2000;
    public static final int GROUP_NOTE = 3000;
    public static final String AGM_GID = "gid";
    private String gid;

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private android.support.v7.widget.RecyclerView topListView;
    //  private ImageView btnAdd;
    //  private ImageView btnRm;
    private LinearLayout viewGroupName;
    private LinearLayout viewGroupMore;
    private TextView txtGroupName;
    private LinearLayout viewGroupNick;
    private TextView txtGroupNick;
    private LinearLayout viewGroupQr;
    private LinearLayout viewGroupNote;
    private TextView txtGroupNote;
    private LinearLayout viewLog;
    private LinearLayout viewTop;
    private CheckBox ckTop;
    private LinearLayout viewDisturb;
    private CheckBox ckDisturb;
    private LinearLayout viewGroupSave;
    private LinearLayout viewGroupManage;
    private CheckBox ckGroupSave;
    private LinearLayout viewGroupVerif;
    private CheckBox ckGroupVerif;
    private Button btnDel;
    private Gson gson = new Gson();
    private Group ginfo;

    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        topListView = (android.support.v7.widget.RecyclerView) findViewById(R.id.topListView);
        //  btnAdd = (ImageView) findViewById(R.id.btn_add);
        //  btnRm = (ImageView) findViewById(R.id.btn_rm);
        viewGroupName = (LinearLayout) findViewById(R.id.view_group_name);
        viewGroupMore = (LinearLayout) findViewById(R.id.view_group_more);
        txtGroupName = (TextView) findViewById(R.id.txt_group_name);
        viewGroupNick = (LinearLayout) findViewById(R.id.view_group_nick);
        txtGroupNick = (TextView) findViewById(R.id.txt_group_nick);
        viewGroupQr = (LinearLayout) findViewById(R.id.view_group_qr);
        viewGroupNote = (LinearLayout) findViewById(R.id.view_group_note);
        txtGroupNote = (TextView) findViewById(R.id.txt_group_note);
        viewLog = (LinearLayout) findViewById(R.id.view_log);
        viewTop = (LinearLayout) findViewById(R.id.view_top);
        ckTop = (CheckBox) findViewById(R.id.ck_top);
        viewDisturb = (LinearLayout) findViewById(R.id.view_disturb);
        ckDisturb = (CheckBox) findViewById(R.id.ck_disturb);
        viewGroupSave = (LinearLayout) findViewById(R.id.view_group_save);
        viewGroupManage = (LinearLayout) findViewById(R.id.view_group_manage);

        ckGroupVerif = (CheckBox) findViewById(R.id.ck_group_verif);
        viewGroupVerif = (LinearLayout) findViewById(R.id.view_group_verif);

        ckGroupSave = (CheckBox) findViewById(R.id.ck_group_save);
        btnDel = (Button) findViewById(R.id.btn_del);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    //自动生成的控件事件
    private void initEvent() {
        gid = getIntent().getStringExtra(AGM_GID);
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


        btnDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertYesNo alertYesNo = new AlertYesNo();
                alertYesNo.init(GroupInfoActivity.this, "退出群聊", "确定退出群聊?", "确定", "取消",
                        new AlertYesNo.Event() {
                            @Override
                            public void onON() {

                            }

                            @Override
                            public void onYes() {
                                taskExitGroup();
                            }
                        });
                alertYesNo.show();
            }
        });


        viewGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdmin()) {
                    ToastUtil.show(getContext(), "非群主无法修改");
                    return;
                }
                Intent intent = new Intent(GroupInfoActivity.this, CommonSetingActivity.class);
                intent.putExtra(CommonSetingActivity.TITLE, "群聊名称");
                intent.putExtra(CommonSetingActivity.REMMARK, "群聊名称");
                intent.putExtra(CommonSetingActivity.HINT, "群聊名称");
                intent.putExtra(CommonSetingActivity.SIZE, 16);
                intent.putExtra(CommonSetingActivity.SETING, ginfo.getName());
                startActivityForResult(intent, GROUP_NAME);
            }
        });

        viewGroupNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupInfoActivity.this, CommonSetingActivity.class);
                intent.putExtra(CommonSetingActivity.TITLE, "我在本群的信息");
                intent.putExtra(CommonSetingActivity.REMMARK, "设置我在这个群里面的昵称");
                intent.putExtra(CommonSetingActivity.HINT, "群昵称");
                intent.putExtra(CommonSetingActivity.SIZE, 16);
                intent.putExtra(CommonSetingActivity.SETING, ginfo.getMygroupName());

                startActivityForResult(intent, GROUP_NICK);
            }
        });

        viewGroupNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdmin()) {
                    ToastUtil.show(getContext(), "非群主无法修改");
                    return;
                }
                Intent intent = new Intent(GroupInfoActivity.this, CommonSetingActivity.class);
                intent.putExtra(CommonSetingActivity.TITLE, "修改群公告");
                intent.putExtra(CommonSetingActivity.REMMARK, "发布后将以通知全体群成员");
                intent.putExtra(CommonSetingActivity.HINT, "修改群公告");
                intent.putExtra(CommonSetingActivity.TYPE_LINE, 1);
                intent.putExtra(CommonSetingActivity.SETING,ginfo.getAnnouncement());
                startActivityForResult(intent, GROUP_NOTE);
            }
        });
        viewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchMsgActivity.class)
                        .putExtra(SearchMsgActivity.AGM_GID, gid)
                );
            }
        });

        viewGroupMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  ToastUtil.show(getContext(),"更多");
                startActivity(new Intent(getContext(), GroupInfoMumberActivity.class).putExtra(AGM_GID, gid));
            }
        });


        viewGroupManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GroupManageActivity.class).putExtra(GroupManageActivity.AGM_GID,gid));
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        findViews();


    }

    @Override
    protected void onResume() {
        super.onResume();
        initEvent();
    }

    private void initData() {
        //顶部处理
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);


        topListView.setLayoutManager(gridLayoutManager);
        topListView.setAdapter(new RecyclerViewTopAdapter());


        viewGroupVerif.setVisibility(View.GONE);
        // ginfo.getNotnotify()
        txtGroupName.setText(ginfo.getName());
        txtGroupNick.setText(ginfo.getMygroupName());
        //txtGroupNote.setText();
        //ckGroupVerif.setChecked(ginfo.getNeedVerification() == 1);
        ckDisturb.setChecked(ginfo.getNotNotify() == 1);
        ckGroupSave.setChecked(ginfo.getSaved() == 1);
        ckTop.setChecked(ginfo.getIsTop() == 1);


        ckTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetState(gid, isChecked ? 1 : 0, null, null, null);
            }
        });
        ckDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetState(gid, null, isChecked ? 1 : 0, null, null);
            }
        });
        ckGroupSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetState(gid, null, null, isChecked ? 1 : 0, null);
            }
        });
        ckGroupVerif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetState(gid, null, null, null, isChecked ? 1 : 0);
            }
        });

        viewGroupQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyselfQRCodeActivity.class)
                        .putExtra(MyselfQRCodeActivity.TYPE, 1)
                        .putExtra(MyselfQRCodeActivity.GROUP_NAME, ginfo.getName())
                        .putExtra(MyselfQRCodeActivity.GROUP_HEAD, ginfo.getAvatar())
                        .putExtra(MyselfQRCodeActivity.GROUP_ID, ginfo.getGid())
                );
            }
        });


    }

    private List<UserInfo> listDataTop = new ArrayList<>();

    //自动生成RecyclerViewAdapter
    class RecyclerViewTopAdapter extends RecyclerView.Adapter<RecyclerViewTopAdapter.RCViewTopHolder> {

        @Override
        public int getItemCount() {

            return listDataTop == null ? 0 : listDataTop.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(final RCViewTopHolder holder, int position) {

            //6.15加标识
            final UserInfo number = listDataTop.get(position);


            if (number != null) {



                holder.imgHead.setImageURI(Uri.parse("" + number.getHead()));
                holder.txtName.setText("" + number.getName4Show());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (number.getUid().longValue()==UserAction.getMyId().longValue()){
                            return;
                        }
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, number.getUid()));
                    }
                });
                if (ginfo.getMaster().equals(""+number.getUid().longValue())) {
                    holder.imgGroup.setVisibility(View.VISIBLE);

                } else {
                    holder.imgGroup.setVisibility(View.GONE);
                }
            } else {
                if (isAdmin() && position == listDataTop.size() - 1) {
                    holder.imgHead.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.ic_group_c)).build());
                    holder.txtName.setText("");
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            taskDel();
                        }
                    });
                } else {
                    holder.imgHead.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.ic_group_a)).build());
                    holder.txtName.setText("");
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            taskAdd();
                        }
                    });
                }

            }


        }


        //自动寻找ViewHold
        @Override
        public RCViewTopHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewTopHolder holder = new RCViewTopHolder(inflater.inflate(R.layout.item_group_create_top2, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewTopHolder extends RecyclerView.ViewHolder {
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private TextView txtName;
            private ImageView imgGroup;

            //自动寻找ViewHold
            public RCViewTopHolder(View convertView) {
                super(convertView);
                imgHead = convertView.findViewById(R.id.img_head);
                imgGroup = convertView.findViewById(R.id.img_group);
                txtName = convertView.findViewById(R.id.txt_name);

            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String content = data.getStringExtra(CommonSetingActivity.CONTENT);

            switch (requestCode) {
                case GROUP_NAME:
                    if (TextUtils.isEmpty(content)) {
                        return;
                    }
                    taskChangeGroupName(gid, content);
                    break;
                case GROUP_NICK:
                    taskChangeMemberName(gid, content);
                    break;
                case GROUP_NOTE:
                    changeGroupAnnouncement(gid,content);
                    break;
            }
        }
    }

    private UserDao userDao = new UserDao();
    private MsgAction msgAction = new MsgAction();

    /***
     * 获取群成员
     * @return
     */
    private List<UserInfo> taskGetNumbers() {
        //进入这个信息的时候会统一给的
        List<UserInfo> userInfos = ginfo.getUsers();


        userInfos = userInfos == null ? new ArrayList() : userInfos;


        return userInfos;
    }

    /***
     * 获取通讯录
     * @return
     */
    private List<UserInfo> taskGetFriends() {
        List<UserInfo> userInfos = userDao.friendGetAll();
        userInfos = userInfos == null ? new ArrayList() : userInfos;

        return userInfos;
    }

    /***
     * 退出群
     */
    private void taskExitGroup() {
        CallBack callBack = new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body().isOk()) {
                    EventBus.getDefault().post(new EventExitChat());
                    finish();
                } else {
                    ToastUtil.show(getContext(), response.body().getMsg());
                }
            }
        };

        if (isAdmin()) {//群主解散
            msgAction.groupDestroy(gid, callBack);
        } else {//成员退出
            msgAction.groupQuit(gid, callBack);
        }


    }

    private boolean isAdmin() {
        if (!StringUtil.isNotNull(ginfo.getMaster()))
            return false;
        return ginfo.getMaster().equals("" + UserAction.getMyId());
    }

    private void taskGetInfo() {
        msgAction.groupInfo(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    ginfo = response.body().getData();

                    actionbar.setTitle("群聊信息(" + ginfo.getUsers().size() + ")");
                    txtGroupNote.setText(ginfo.getAnnouncement());
                  /*  for (int i=0;i<50;i++){
                        UserInfo teuser=new UserInfo();
                        teuser.setHead(ginfo.getMembers().get(0).getHead());
                        teuser.setName(""+i);
                        teuser.setUid(4546l);
                        ginfo.getMembers().add(teuser);
                    }*/

                    listDataTop.clear();
                    if (isAdmin()) {
                        if (ginfo.getUsers().size() > 18) {
                            viewGroupMore.setVisibility(View.VISIBLE);
                            for (int i = 0; i < 18; i++) {
                                listDataTop.add(ginfo.getUsers().get(i));
                            }

                        } else {
                            listDataTop.addAll(ginfo.getUsers());
                            viewGroupMore.setVisibility(View.GONE);
                        }
                        listDataTop.add(null);
                        listDataTop.add(null);
                        viewGroupManage.setVisibility(View.VISIBLE);
                    } else {


                        if (ginfo.getUsers().size() > 19) {
                            viewGroupMore.setVisibility(View.VISIBLE);
                            for (int i = 0; i < 19; i++) {
                                listDataTop.add(ginfo.getUsers().get(i));
                            }

                        } else {
                            listDataTop.addAll(ginfo.getUsers());
                            viewGroupMore.setVisibility(View.GONE);
                        }

                        listDataTop.add(null);
                        viewGroupManage.setVisibility(View.GONE);

                    }

                    // viewGroupMore.setVisibility(View.VISIBLE);
                    initData();
                }
            }
        });
    }


    private void taskSetState(String gid, Integer isTop, Integer notNotify, Integer saved, Integer needVerification) {

        msgAction.groupSwitch(gid, isTop, notNotify, saved, needVerification, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {

                if (response.body() == null)
                    return;
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    initEvent();
                }

            }
        });
    }


    private void taskAdd() {
        List<UserInfo> userInfos = taskGetNumbers();

        List<UserInfo> friendsUser = taskGetFriends();

        List<UserInfo> temp = new ArrayList<>();

        for (UserInfo a : friendsUser) {
            boolean isEx = false;
            for (UserInfo u : userInfos) {
                if (u.getUid().longValue() == a.getUid().longValue()) {
                    isEx = true;
                }
            }
            if (!isEx) {
                temp.add(a);
            }

        }


        String json = gson.toJson(temp);
        startActivity(new Intent(getContext(), GroupNumbersActivity.class)
                .putExtra(GroupNumbersActivity.AGM_GID, gid)
                .putExtra(GroupNumbersActivity.AGM_TYPE, GroupNumbersActivity.TYPE_ADD)
                .putExtra(GroupNumbersActivity.AGM_NUMBERS_JSON, json)
        );
    }

    private void taskDel() {
        List<UserInfo> userInfos = taskGetNumbers();
        for (UserInfo u : userInfos) {
            if (u.getUid().longValue() == UserAction.getMyId().longValue()) {
                userInfos.remove(u);
                break;
            }
        }
        String json = gson.toJson(userInfos);
        startActivity(new Intent(getContext(), GroupNumbersActivity.class)
                .putExtra(GroupNumbersActivity.AGM_GID, gid)
                .putExtra(GroupNumbersActivity.AGM_TYPE, GroupNumbersActivity.TYPE_DEL)
                .putExtra(GroupNumbersActivity.AGM_NUMBERS_JSON, json)
        );
    }


    private void taskChangeGroupName(String gid, final String name) {
        msgAction.changeGroupName(gid, name, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    txtGroupName.setText(name);
                    initEvent();
                }
            }
        });
    }

    private void taskChangeMemberName(String gid, final String name) {
        msgAction.changeMemberName(gid, name, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    txtGroupNick.setText(name);
                    initEvent();
                }
            }
        });
    }


    private void changeGroupAnnouncement(String gid, final String announcement){
        msgAction.changeGroupAnnouncement(gid, announcement, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    txtGroupNote.setText(announcement);
                }
            }
        });
    }


}
