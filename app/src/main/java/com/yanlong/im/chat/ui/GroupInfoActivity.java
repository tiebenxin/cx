package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.CommonSetingActivity;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.user.ui.ImageHeadActivity;
import com.yanlong.im.user.ui.MyselfQRCodeActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.GroupHeadImageUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CallBack4Btn;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Response;

public class GroupInfoActivity extends AppActivity {
    public static final int GROUP_NAME = 1000;
    public static final int GROUP_NICK = 2000;
    public static final int GROUP_NOTE = 3000;
    public static final String AGM_GID = "gid";
    private String gid;
    private static final int IMAGE_HEAD = 4000;
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private android.support.v7.widget.RecyclerView topListView;
    //  private ImageView btnAdd;
    //  private ImageView btnRm;
    private LinearLayout viewGroupName, viewGroupImg;
    private LinearLayout viewGroupMore;
    private TextView txtGroupName;
    private LinearLayout viewGroupNick;
    private TextView txtGroupNick;
    private LinearLayout viewGroupQr;
    private LinearLayout viewGroupNote;
    private TextView txtGroupNote, txtNote;
    private LinearLayout viewLog;
    private LinearLayout viewTop;
    private CheckBox ckTop;
    private LinearLayout viewDisturb;
    private CheckBox ckDisturb;
    private LinearLayout viewGroupSave;
    private LinearLayout viewGroupManage;
    private CheckBox ckGroupSave;
    private LinearLayout viewGroupVerif;
    private LinearLayout viewClearChatRecord;
    private LinearLayout viewComplaint;
    private CheckBox ckGroupVerif;
    private Button btnDel;
    private Gson gson = new Gson();
    private Group ginfo;

    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        topListView = findViewById(R.id.topListView);
        viewGroupName = findViewById(R.id.view_group_name);
        viewGroupMore = findViewById(R.id.view_group_more);
        txtGroupName = findViewById(R.id.txt_group_name);
        viewGroupNick = findViewById(R.id.view_group_nick);
        txtGroupNick = findViewById(R.id.txt_group_nick);
        viewGroupQr = findViewById(R.id.view_group_qr);
        viewGroupNote = findViewById(R.id.view_group_note);
        txtGroupNote = findViewById(R.id.txt_group_note);
        txtNote = findViewById(R.id.txt_note);
        viewLog = findViewById(R.id.view_log);
        viewTop = findViewById(R.id.view_top);
        ckTop = findViewById(R.id.ck_top);
        viewDisturb = findViewById(R.id.view_disturb);
        ckDisturb = findViewById(R.id.ck_disturb);
        viewGroupSave = findViewById(R.id.view_group_save);
        viewGroupManage = findViewById(R.id.view_group_manage);
        viewGroupImg = findViewById(R.id.view_group_img);

        ckGroupVerif = findViewById(R.id.ck_group_verif);
        viewGroupVerif = findViewById(R.id.view_group_verif);
        viewComplaint = findViewById(R.id.view_complaint);
        ckGroupSave = findViewById(R.id.ck_group_save);
        btnDel = findViewById(R.id.btn_del);
        viewClearChatRecord = findViewById(R.id.view_clear_chat_record);
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
                alertYesNo.init(GroupInfoActivity.this, "提示", "删除群后会删除群会话与群聊数据", "确定", "取消",
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
//                if (!isAdmin()) {
//                    ToastUtil.show(getContext(), "非群主无法修改");
//                    return;
//                }
//                Intent intent = new Intent(GroupInfoActivity.this, CommonSetingActivity.class);
//                intent.putExtra(CommonSetingActivity.TITLE, "修改群公告");
//                intent.putExtra(CommonSetingActivity.REMMARK, "发布后将以通知全体群成员");
//                intent.putExtra(CommonSetingActivity.HINT, "修改群公告");
//                intent.putExtra(CommonSetingActivity.TYPE_LINE, 1);
//                intent.putExtra(CommonSetingActivity.SIZE, 500);
//                intent.putExtra(CommonSetingActivity.SETING, ginfo.getAnnouncement());
//                startActivityForResult(intent, GROUP_NOTE);
                ginfo.getMaster();
                if (isAdmin()) {
                    Intent intent = new Intent(GroupInfoActivity.this, GroupNoteDetailActivity.class);
                    intent.putExtra(GroupNoteDetailActivity.GID, gid);
                    intent.putExtra(GroupNoteDetailActivity.NOTE, ginfo.getAnnouncement());
                    intent.putExtra(GroupNoteDetailActivity.IS_OWNER, true);
                    intent.putExtra(GroupNoteDetailActivity.GROUP_NICK, ginfo.getMygroupName());
                    startActivityForResult(intent, GROUP_NOTE);
                } else {
                    String note = ginfo.getAnnouncement();
                    if (!TextUtils.isEmpty(note)) {
                        Intent intent = new Intent(GroupInfoActivity.this, GroupNoteDetailActivity.class);
                        intent.putExtra(GroupNoteDetailActivity.NOTE, ginfo.getAnnouncement());
                        intent.putExtra(GroupNoteDetailActivity.IS_OWNER, false);
                        startActivity(intent);
                    }
                }
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

        final RealmList<MemberUser> list = ginfo.getUsers();
        if (list.size() < 400) {
            isPercentage = false;
        }

        viewGroupManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GroupManageActivity.class)
                        .putExtra(GroupManageActivity.AGM_GID, gid).putExtra(GroupManageActivity.PERCENTAGE, isPercentage));
            }
        });

        viewGroupImg.setVisibility(View.VISIBLE);
        viewGroupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headIntent = new Intent(GroupInfoActivity.this, ImageHeadActivity.class);
                //todo 头像修改
                headIntent.putExtra(ImageHeadActivity.IMAGE_HEAD, ginfo.getAvatar());
                headIntent.putExtra("admin", isAdmin());
                headIntent.putExtra("groupSigle", true);
                headIntent.putExtra("gid", gid);
                startActivityForResult(headIntent, IMAGE_HEAD);
            }
        });

        viewClearChatRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertYesNo alertYesNo = new AlertYesNo();
                alertYesNo.init(GroupInfoActivity.this, "提示", "确定清空聊天记录？", "确定", "取消", new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        MsgDao msgDao = new MsgDao();
                        msgDao.msgDel(null, gid);
                        EventBus.getDefault().post(new EventRefreshChat());
                        MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, -1L, gid, CoreEnum.ESessionRefreshTag.SINGLE, null);
                        ToastUtil.show(GroupInfoActivity.this, "删除成功");
                    }
                });
                alertYesNo.show();

            }
        });

        viewComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupInfoActivity.this, ComplaintActivity.class);
                intent.putExtra(ComplaintActivity.GID, gid);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  setResult(ChatActivity.REQ_REFRESH);
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
        if (!isBackValue){
            initEvent();
            isBackValue=false;
        }

    }

    public boolean isPercentage = true;

    private void initData() {
        //顶部处理
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        ckTop.setOnCheckedChangeListener(null);
        ckDisturb.setOnCheckedChangeListener(null);
        ckGroupSave.setOnCheckedChangeListener(null);
        ckGroupVerif.setOnCheckedChangeListener(null);

        topListView.setLayoutManager(gridLayoutManager);
        topListView.setAdapter(new RecyclerViewTopAdapter());
        viewGroupVerif.setVisibility(View.GONE);
        txtGroupName.setText(TextUtils.isEmpty(ginfo.getName()) ? "未设置" : ginfo.getName());
        txtGroupNick.setText(ginfo.getMygroupName());
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
                taskSetStateDisturb(gid, null, isChecked ? 1 : 0, null, null);
            }
        });
        ckGroupSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetStateGroupSave(gid, null, null, isChecked ? 1 : 0, null);
            }
        });

        //开启群验证
        ckGroupVerif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetStateGroupVerif(gid, null, null, null, isChecked ? 1 : 0);
            }
        });

        viewGroupQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyselfQRCodeActivity.class)
                        .putExtra(MyselfQRCodeActivity.TYPE, 1)
                        .putExtra(MyselfQRCodeActivity.GROUP_NAME, /*ginfo.getName()*/msgDao.getGroupName(gid))
                        .putExtra(MyselfQRCodeActivity.GROUP_HEAD, ginfo.getAvatar())
                        .putExtra(MyselfQRCodeActivity.GROUP_ID, ginfo.getGid())
                );
            }
        });
    }

    private List<MemberUser> listDataTop = new ArrayList<>();

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
            final MemberUser number = listDataTop.get(position);
            if (number != null) {
                //holder.imgHead.setImageURI(Uri.parse("" + number.getHead()));
                Glide.with(context).load(number.getHead())
                        .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);

                holder.txtName.setText("" + number.getShowName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (number.getUid() == UserAction.getMyId().longValue()) {
                            return;
                        }
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, number.getUid())
                                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                                .putExtra(UserInfoActivity.GID, gid)
                                .putExtra(UserInfoActivity.MUC_NICK, number.getShowName()));

                    }
                });
                if (ginfo.getMaster().equals("" + number.getUid())) {
                    holder.imgGroup.setVisibility(View.VISIBLE);

                } else {
                    holder.imgGroup.setVisibility(View.GONE);
                }
            } else {
                if (isAdmin() && position == listDataTop.size() - 1) {
                    // holder.imgHead.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.ic_group_c)).build());
                    holder.imgHead.setImageResource(R.mipmap.ic_group_c);
                    holder.txtName.setText("");
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            taskDel();
                        }
                    });
                } else {
                    //holder.imgHead.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.ic_group_a)).build());
                    holder.imgHead.setImageResource(R.mipmap.ic_group_a);
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
            private ImageView imgHead;
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

    private boolean isBackValue=false;
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
                    String note = data.getStringExtra(GroupNoteDetailActivity.CONTENT);
                    ginfo.setAnnouncement(note);
//                    updateAndGetGroup();
                    setGroupNote(ginfo.getAnnouncement());
                    createAndSaveMsg();
                    isBackValue=true;
                    break;
            }
        }
    }

    private UserDao userDao = new UserDao();
    private MsgAction msgAction = new MsgAction();
    private MsgDao msgDao = new MsgDao();

    /***
     * 获取群成员
     * @return
     */
    private List<MemberUser> taskGetNumbers() {
        //进入这个信息的时候会统一给的
        List<MemberUser> userInfos = ginfo.getUsers();
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

        msgAction.groupQuit(gid, UserAction.getMyInfo().getName(), callBack);

//        if (isAdmin()) {//群主解散
//            msgAction.groupDestroy(gid, callBack);
//        } else {//成员退出
//
//        }


    }

    private boolean isAdmin() {
        if (!StringUtil.isNotNull(ginfo.getMaster()))
            return false;
        return ginfo.getMaster().equals("" + UserAction.getMyId());
    }

    private void taskGetInfo() {
        CallBack callBack = new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    ginfo = response.body().getData();
                    if (ginfo == null) {
                        return;
                    }

//                    Group goldinfo = msgDao.getGroup4Id(gid);
//                    if (!isChange(goldinfo, ginfo)) {
//                        doImgHeadChange(gid, ginfo);
//                    }
                    //8.8 如果是有群昵称显示自己群昵称
//                    for (MemberUser number : ginfo.getUsers()) {
//                        if (StringUtil.isNotNull(number.getMembername())) {
//                            number.setName(number.getMembername());
//                        }
//                    }
                    actionbar.setTitle("群聊信息(" + ginfo.getUsers().size() + ")");
                    setGroupNote(ginfo.getAnnouncement());
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
                    initData();
                }
            }
        };
        msgAction.groupInfo4Db(gid, callBack);
//        msgAction.groupInfo(gid, callBack);
    }

    //设置群公告
    private void setGroupNote(String note) {
        if (!TextUtils.isEmpty(note)) {
            txtGroupNote.setVisibility(View.VISIBLE);
            txtGroupNote.setText(note);
            txtNote.setVisibility(View.GONE);
        } else {
            txtGroupNote.setVisibility(View.GONE);
            txtNote.setVisibility(View.VISIBLE);
        }
    }

    private void taskGetInfoNetwork() {
        CallBack callBack = new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    ginfo = response.body().getData();
                    //8.8 如果是有群昵称显示自己群昵称
                    for (MemberUser number : ginfo.getUsers()) {
                        if (StringUtil.isNotNull(number.getMembername())) {
                            number.setName(number.getMembername());
                        }
                    }
                    actionbar.setTitle("群聊信息(" + ginfo.getUsers().size() + ")");
                    setGroupNote(ginfo.getAnnouncement());
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
                    initData();
                }
            }
        };
        msgAction.groupInfo(gid, callBack);
    }


    /*
     * 置顶
     * */
    private void taskSetState(String gid, Integer isTop, Integer notNotify, Integer saved, Integer needVerification) {

        msgAction.groupSwitch(gid, isTop, notNotify, saved, needVerification, new CallBack4Btn<ReturnBean>(ckTop) {
            @Override
            public void onResp(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    taskGetInfoNetwork();
                }

            }
        });
    }

    /*
     * 免打扰
     * */
    private void taskSetStateDisturb(String gid, Integer isTop, Integer notNotify, Integer saved, Integer needVerification) {

        msgAction.groupSwitch(gid, isTop, notNotify, saved, needVerification, new CallBack4Btn<ReturnBean>(ckDisturb) {
            @Override
            public void onResp(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    taskGetInfoNetwork();
                }

            }
        });
    }

    /*
     * 群保存
     * */
    private void taskSetStateGroupSave(String gid, Integer isTop, Integer notNotify, Integer saved, Integer needVerification) {

        msgAction.groupSwitch(gid, isTop, notNotify, saved, needVerification, new CallBack4Btn<ReturnBean>(ckGroupSave) {
            @Override
            public void onResp(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    msgDao.setSavedGroup(gid, saved);
                    //  taskGetInfoNetwork();
                }
            }
        });
    }

    private void taskSetStateGroupVerif(String gid, Integer isTop, Integer notNotify, Integer saved, Integer needVerification) {

        msgAction.groupSwitch(gid, isTop, notNotify, saved, needVerification, new CallBack4Btn<ReturnBean>(ckGroupVerif) {
            @Override
            public void onResp(Call<ReturnBean> call, Response<ReturnBean> response) {

                if (response.body() == null)
                    return;
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    taskGetInfoNetwork();
                }
            }
        });
    }


    private void taskAdd() {
        List<MemberUser> userInfos = taskGetNumbers();
        List<UserInfo> friendsUser = taskGetFriends();
        List<UserInfo> temp = new ArrayList<>();
        for (UserInfo a : friendsUser) {
            boolean isEx = false;
            for (MemberUser u : userInfos) {
                if (u.getUid() == a.getUid().longValue()) {
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
        List<MemberUser> userInfos = taskGetNumbers();
        for (MemberUser u : userInfos) {
            if (u.getUid() == UserAction.getMyId().longValue()) {
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
                    msgDao.updateGroupName(gid, name);
                    MessageManager.getInstance().setMessageChange(true);
                    MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.GROUP, -1L, gid, CoreEnum.ESessionRefreshTag.SINGLE, null);
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
                    msgDao.updateMyGroupName(gid, name);
                    initEvent();


                }
            }
        });
    }


    private void changeGroupAnnouncement(final String gid, final String announcement, String nick) {
        msgAction.changeGroupAnnouncement(gid, announcement, nick, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    setGroupNote(announcement);
                    ginfo.setAnnouncement(announcement);
                    updateAndGetGroup();
                }
            }
        });
    }

    private void updateAndGetGroup() {
        if (ginfo != null && !TextUtils.isEmpty(gid)) {
            MsgDao dao = new MsgDao();
            dao.groupNumberSave(ginfo);
            ginfo = dao.groupNumberGet(gid);
        }
    }

    private boolean isChange(Group goldinfo, Group ginfo) {
        int a = ginfo.getUsers().size();
        if (goldinfo == null || goldinfo.getUsers() == null) {
            return true;
        }
        int b = goldinfo.getUsers().size();
        if (a != b) {
            return true;
        }
        int c = a > 9 ? 9 : a;
        for (int i = 0; i < a; i++) {
            if (StringUtil.isNotNull(goldinfo.getUsers().get(i).getHead()) && StringUtil.isNotNull(ginfo.getUsers().get(i).getHead())) {
                if (!goldinfo.getUsers().get(i).getHead().equals(ginfo.getUsers().get(i).getHead())) {
                    return true;
                }
            }

        }
        return false;
    }

    private void doImgHeadChange(String gid, Group ginfo) {

        int i = ginfo.getUsers().size();
        i = i > 9 ? 9 : i;
        //头像地址
        String url[] = new String[i];
        for (int j = 0; j < i; j++) {
            MemberUser userInfo = ginfo.getUsers().get(j);
            url[j] = userInfo.getHead();
        }
        File file = GroupHeadImageUtil.synthesis(getContext(), url);
        MsgDao msgDao = new MsgDao();
        msgDao.groupHeadImgUpdate(gid, file.getAbsolutePath());
//                        msgDao.groupSave(ginfo);
    }

    private void createAndSaveMsg() {
        if (ginfo == null || TextUtils.isEmpty(gid)) {
            return;
        }
//        MsgAllBean bean = SocketData.createMessageBean(gid, "@所有人 \r\n" + ginfo.getAnnouncement(), ginfo);
        AtMessage atMessage = SocketData.createAtMessage(SocketData.getUUID(), "@所有人 \r\n" + ginfo.getAnnouncement(), ChatEnum.EAtType.ALL);
        MsgAllBean bean = SocketData.createMessageBean(null, gid, ChatEnum.EMessageType.AT, ChatEnum.ESendStatus.NORMAL, -1L, atMessage);
        if (bean != null) {
            SocketData.saveMessage(bean);
        }
    }


}
