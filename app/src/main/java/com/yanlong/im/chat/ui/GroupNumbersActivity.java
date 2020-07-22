package com.yanlong.im.chat.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.GroupJoinBean;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.TouchUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PySortView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 群成员操作
 * 1.邀请进群
 * 2.删除群成员
 */
public class GroupNumbersActivity extends AppActivity {
    public static final String AGM_GID = "gid";

    //成员列表
    public static final String AGM_NUMBERS_JSON = "number_json";
    //1:添加,2:删除
    public static final String AGM_TYPE = "type";
    public static final String NEED_VERIFICATION = "need_verification";//是否需要群验证
    public static final int TYPE_ADD = 1;
    public static final int TYPE_DEL = 2;

    private String gid;
    private List<UserInfo> listData;
    private List<UserInfo> tempData = new ArrayList<>();
    private Integer type;//TYPE_ADD 邀请进群  否则为移除群成员
    private int needVerification = 0;// 0 无需群验证 1 需要群验证

    private Gson gson = new Gson();

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private RecyclerView topListView;
    private net.cb.cb.library.view.MultiListView mtListView;
    private PySortView viewType;
    private int isClickble = 0;
    private RecyclerViewAdapter mAdapter;
    private CommonSelectDialog.Builder builder;
    private CommonSelectDialog dialogOne;//群验证弹框

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshBalance(EventFactory.UpdateGroupNumberEvent event) {
        try {
            // 更新好友封号状态
            if (listData != null) {
                for (int i = 0; i < listData.size(); i++) {
                    UserInfo userInfo = listData.get(i);
                    if (userInfo.getUid().longValue() == event.uid.longValue()) {
                        userInfo.setLockedstatus(event.lockedstatus);
                        mtListView.getListView().getAdapter().notifyItemChanged(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.activity_group_create);
        findViews();
        initEvent();
        initData();
    }

    private void initData() {
        taskListData();
    }

    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        topListView = findViewById(R.id.topListView);
        mtListView = findViewById(R.id.mtListView);
        viewType = findViewById(R.id.view_type);
        builder = new CommonSelectDialog.Builder(GroupNumbersActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    //自动生成的控件事件
    private void initEvent() {
        listData = gson.fromJson(getIntent().getStringExtra(AGM_NUMBERS_JSON), new TypeToken<List<UserInfo>>() {
        }.getType());
        // 处理NullPointerException
        if (listData == null) {
            listData = new ArrayList<>();
        } else {
            Collections.sort(listData);
        }
        type = getIntent().getIntExtra(AGM_TYPE, TYPE_ADD);
        gid = getIntent().getStringExtra(AGM_GID);
        needVerification = getIntent().getIntExtra(NEED_VERIFICATION,0);

        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (isClickble == 0) {
                    if(needVerification==0){
                        taskOption("");//邀请入群，不显示弹框
                    }else {
                        //TODO 若已开启群验证，改为弹框，入群通知移至聊天界面
                        if(type == TYPE_ADD){//邀请入群，需要显示弹框
                            showDialogOne();
                        }else {
                            taskOption("");//删除原有逻辑不变
                        }
                    }

                }
            }
        });
        actionbar.setTxtRight("确定");

        actionbar.setTitle(type == TYPE_ADD ? "选择联系人" : "删除成员");
        mAdapter = new RecyclerViewAdapter();
        mtListView.init(mAdapter);
        mtListView.getLoadView().setStateNormal();
        //联动
        viewType.setLinearLayoutManager(mtListView.getLayoutManager());
        viewType.setListView(mtListView.getListView());
        //顶部处理
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        topListView.setLayoutManager(linearLayoutManager);
        topListView.setAdapter(new RecyclerViewTopAdapter());
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder hd, @SuppressLint("RecyclerView") int position) {

            final UserInfo bean = listData.get(position);

            hd.txtType.setText(bean.getTag());
            // hd.imgHead.setImageURI(Uri.parse("" + bean.getHead()));
            Glide.with(context).load(bean.getHead())
                    .apply(GlideOptionsUtil.headImageOptions()).into(hd.imgHead);

            hd.txtName.setText(bean.getName4Show());

            hd.viewType.setVisibility(View.VISIBLE);
            hd.viewLine.setVisibility(View.VISIBLE);
            if (position > 0 && listData.size() > 1) {
                UserInfo lastbean = listData.get(position - 1);
                if (lastbean.getTag().equals(bean.getTag())) {
                    hd.viewType.setVisibility(View.GONE);
                }
            }
            if (position == getItemCount() - 1) {
                hd.viewLine.setVisibility(View.GONE);
            } else {
                UserInfo lastbean = listData.get(position + 1);
                if (!lastbean.getTag().equals(bean.getTag())) {
                    hd.viewLine.setVisibility(View.GONE);
                }
            }
            hd.ivSelect.setOnClickListener(o -> {
                if (UserUtil.getUserStatus(bean.getLockedstatus())) {
                    ToastUtil.show(getResources().getString(R.string.friend_disable_message));
                }
            });

            hd.ckSelect.setOnCheckedChangeListener(null);//清掉监听器
            hd.ckSelect.setChecked(bean.isChecked());

            hd.ckSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!UserUtil.getUserStatus(bean.getLockedstatus())) {
                        if (isChecked) {
                            listDataTop.add(bean);
                        } else {
                            listDataTop.remove(bean);
                        }
                        listData.get(position).setChecked(isChecked);
                        topListView.getAdapter().notifyDataSetChanged();
                    } else {
                        ToastUtil.show(getResources().getString(R.string.friend_disable_message));
                    }
                }
            });

            TouchUtil.expandTouch(hd.ckSelect);

            if (UserUtil.getUserStatus(bean.getLockedstatus())) {
                hd.ckSelect.setVisibility(View.GONE);
                hd.ivSelect.setVisibility(View.VISIBLE);
            } else {
                hd.ckSelect.setVisibility(View.VISIBLE);
                hd.ivSelect.setVisibility(View.GONE);
            }
        }

        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_group_create, view, false));
            return holder;
        }

        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout viewType;
            private TextView txtType;
            private ImageView imgHead;
            private TextView txtName;
            private CheckBox ckSelect;
            private View viewLine;
            private ImageView ivSelect;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewType = convertView.findViewById(R.id.view_type);
                txtType = convertView.findViewById(R.id.txt_type);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                ckSelect = convertView.findViewById(R.id.ck_select);
                viewLine = convertView.findViewById(R.id.view_line);
                ivSelect = convertView.findViewById(R.id.iv_select);
            }
        }
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
        public void onBindViewHolder(RCViewTopHolder holder, int position) {

            //  holder.imgHead.setImageURI(Uri.parse(listDataTop.get(position).getHead()));

            Glide.with(context).load(listDataTop.get(position).getHead())
                    .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
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


    private MsgAction msgACtion = new MsgAction();
    private UserDao userDao = new UserDao();

    private void taskListData() {

        // 升序
        Collections.sort(listData, new Comparator<UserInfo>() {
            @Override
            public int compare(UserInfo o1, UserInfo o2) {
                return o1.getTag().hashCode() - o2.getTag().hashCode();
            }
        });
        // 把#数据放到末尾
        tempData.clear();
        for (int i = listData.size() - 1; i >= 0; i--) {
            UserInfo bean = listData.get(i);
            if (bean.getTag().hashCode() == 35) {
                tempData.add(bean);
                listData.remove(i);
            }
        }
        listData.addAll(tempData);
        for (int i = 0; i < listData.size(); i++) {
            //UserInfo infoBean:
            viewType.putTag(listData.get(i).getTag(), i);
        }
        // 添加存在用户的首字母列表
        viewType.addItemView(UserUtil.userParseString(listData));

    }

    /***
     * 提交处理
     */
    private void taskOption(String remark) {
        if (listDataTop.size() < 1) {
            ToastUtil.show(getContext(), "请至少选择一个用户");
            return;
        }
        isClickble = 1;
        LogUtil.getLog().e("GroupNumbersActivity", "taskOption");
        alert.show();
        MsgDao dao = new MsgDao();
        List<MemberUser> list = new ArrayList<>();
        List<Long> listLong = new ArrayList<>();
        List<MemberUser> mem = dao.getGroup4Id(gid).getUsers();
        CallBack<ReturnBean<GroupJoinBean>> callback = new CallBack<ReturnBean<GroupJoinBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<GroupJoinBean>> call, Response<ReturnBean<GroupJoinBean>> response) {
                try {
                    Thread.sleep(1000);
                    alert.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.body() == null) {
                    isClickble = 0;
                    return;
                }
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    if (type == TYPE_DEL) {
                        dao.removeGroupMember(gid, listLong);
                    }
                    if (type == TYPE_ADD && response.body().getData() != null) {
                        if (response.body().getData().isPending()) {
                            ToastUtil.show("邀请成功,等待群主验证");
                            SocketData.invitePersonLocalNotice(gid);
                        } else {
                            SocketData.createMsgGroupOfNotice(gid, listDataTop);
                        }
                    }
                    MessageManager.getInstance().notifyGroupChange(true);
                    finish();
                } else {
                    isClickble = 0;
                }

            }

            @Override
            public void onFailure(Call<ReturnBean<GroupJoinBean>> call, Throwable t) {
                super.onFailure(call, t);
                isClickble = 0;
                alert.dismiss();
            }
        };

        for (int k = 0; k < listDataTop.size(); k++) {
            MessageManager msg = new MessageManager();
            list.add(msg.userToMember(listDataTop.get(k), gid));
        }

        for (int i = 0; i < listDataTop.size(); i++) {
//            dao.addGroupMember(gid,listDataTop)
            for (int j = 0; j < mem.size(); j++) {
                if (listDataTop.get(i).getUid() == mem.get(j).getUid()) {
//                    list.add(mem.get(j));
                    listLong.add(listDataTop.get(i).getUid());
                }
            }
        }

        if (type == TYPE_ADD) {
            msgACtion.groupAdd(gid, listDataTop, UserAction.getMyInfo().getName(), callback);
//            dao.addGroupMember(gid,list);
        } else {
            msgACtion.groupRemove(gid, listDataTop, callback);
        }


//        GroupHeadImageUtil.creatAndSaveImg(GroupNumbersActivity.this, gid);
    }


    private void showDialogOne(){
        if (dialogOne == null) {
            dialogOne = builder.setTitle("群聊已开启群验证，邀请朋友进群\n可向管理员描述原因。")
                    .setLeftText("取消")
                    .setRightText("确定")
                    .setType(1)
                    .setLeftOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogOne.dismiss();
                            finish();
                        }
                    })
                    .setRightOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogOne.dismiss();
                            taskOption(dialogOne.getEditContent());
                        }
                    })
                    .build();
        }
        dialogOne.show();
    }
}
