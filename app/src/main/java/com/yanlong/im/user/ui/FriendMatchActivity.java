package com.yanlong.im.user.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.common.utils.DateUtil;
import com.bumptech.glide.Glide;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.ui.BaseBindActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.ui.groupmanager.SetupSysManagerActivity;
import com.yanlong.im.databinding.ActivityFriendMatchBinding;
import com.yanlong.im.databinding.ItemFriendMatchBindBinding;
import com.yanlong.im.databinding.ItemSetupManagerBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.AddressBookMatchingBean;
import com.yanlong.im.user.bean.FriendInfoBean;
import com.yanlong.im.user.bean.PhoneBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.CommonUtils;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.PhoneListUtil;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertTouch;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.PySortView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 手机通讯录匹配
 */
public class FriendMatchActivity extends BaseBindActivity<ActivityFriendMatchBinding> {

    private UserAction userAction;
    private PhoneListUtil phoneListUtil = new PhoneListUtil();
    private List<FriendInfoBean> tempData = new ArrayList<>();
    private List<FriendInfoBean> listData = new ArrayList<>();
    private List<FriendInfoBean> seacchData = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private int numberLimit = 500;//通讯录上传数量限制，手机联系人数量超过这个数，则分批次上传，显示等待框，加载完成后等待框消失
    private int needUploadTimes = 0;//批次上传-需要上传的次数
    private int hadUploadTimes = 0;//批次上传-已经上传的次数
    private boolean ifSub = false;//是否存在批次上传
    private boolean isFirstUpload = true;// 是否第一次匹配
    private List<List<PhoneBean>> subList;//批次上传-切割后的数据
    private UserDao userDao = new UserDao();

    private List<FriendInfoBean> recentFriends = new ArrayList<>();// 最近7天数据

    @Override
    protected int setView() {
        return R.layout.activity_friend_match;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        bindingView.ceSearch.setHint("输入联系人昵称搜索");
    }

    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        //联动
        bindingView.viewType.setLinearLayoutManager(bindingView.mtListView.getLayoutManager());
        bindingView.viewType.setListView(bindingView.mtListView.getListView());
        adapter = new RecyclerViewAdapter();
        bindingView.mtListView.init(adapter);
        bindingView.mtListView.getLoadView().setStateNormal();
        bindingView.ceSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String name = bindingView.ceSearch.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            searchName(name);
                            return true;
                    }
                }
                return false;
            }
        });

        bindingView.ceSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString();
                if (TextUtils.isEmpty(content)) {
                    adapter.setList(listData);
                    bindingView.mtListView.notifyDataSetChange();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void loadData() {
        isFirstUpload = SpUtil.getSpUtil().getSPValue(Preferences.IS_FIRST_UPLOAD_PHONE + UserAction.getMyId(), true);
        SpUtil.getSpUtil().putSPValue(Preferences.RECENT_FRIENDS_RED_NUMBER + UserAction.getMyId(), "");

        userAction = new UserAction();
        phoneListUtil.getPhones(FriendMatchActivity.this, new PhoneListUtil.Event() {
            @Override
            public void onList(final List<PhoneBean> list) {
                if (list == null)
                    return;
                // 保存最新的通讯录
                userDao.updateLocaPhones(list);
                //分批次上传请求
                if (list.size() > numberLimit) {
                    ifSub = true;
                    bindingView.pbWait.setVisibility(View.VISIBLE);
                    subList = new ArrayList<>();
                    subList.addAll(CommonUtils.subWithLen(list, numberLimit));//拆分成多个List按批次上传
                    needUploadTimes = subList.size();
                    taskUserMatchPhone(subList.get(hadUploadTimes));//默认先传第一部分
                } else {
                    ifSub = false;
                    bindingView.pbWait.setVisibility(View.GONE);
                    taskUserMatchPhone(list);
                }
            }
        });
    }

    /**
     * 筛选最近7天的数据添加到recentFriends
     *
     * @param notFriendlist
     */
    private void filterData(List<FriendInfoBean> notFriendlist) {
        try {
            String friends = SpUtil.getSpUtil().getSPValue(Preferences.RECENT_FRIENDS_UIDS + UserAction.getMyId(), "");
            List<FriendInfoBean> list;
            recentFriends.clear();
            if (!TextUtils.isEmpty(friends)) {
                list = new Gson().fromJson(friends, new TypeToken<List<FriendInfoBean>>() {
                }.getType());
                if (list != null && list.size() > 0) {
                    int sum = list.size();
                    // 判断记录时间是否超过7天
                    for (int i = list.size() - 1; i >= 0; i--) {
                        if (!DateUtils.checkTimeDifferenceDay(list.get(i).getCreateTime())) {
                            list.remove(i);
                        }
                    }
                    // 更新缓存
                    if (sum != list.size()) {
                        SpUtil.getSpUtil().putSPValue(Preferences.RECENT_FRIENDS_UIDS + UserAction.getMyId(), new Gson().toJson(list));
                    }
                    // 筛选最近7天的数据添加到recentFriends
                    for (FriendInfoBean friendInfoBean : list) {
                        for (int i = notFriendlist.size() - 1; i >= 0; i--) {
                            // 用手机号判断
                            if ((friendInfoBean.getPhoneremark() != null) &&
                                    friendInfoBean.getPhoneremark().equals(notFriendlist.get(i).getPhone())) {
                                notFriendlist.get(i).setShowPinYin(true);
                                FriendInfoBean infoBean = notFriendlist.get(i);
                                infoBean.setCreateTime(friendInfoBean.getCreateTime());
                                recentFriends.add(infoBean);
                                notFriendlist.remove(i);
                                break;
                            }
                        }
                    }
                }
                if (recentFriends != null && recentFriends.size() > 0) {
                    // 按时间倒序
                    Collections.sort(recentFriends, new Comparator<FriendInfoBean>() {
                        @Override
                        public int compare(FriendInfoBean o1, FriendInfoBean o2) {
                            return (int) (o2.getCreateTime() - o1.getCreateTime());
                        }
                    });
                }
            }
        } catch (Exception e) {

        }
    }

    public void searchName(String name) {
        if (!TextUtils.isEmpty(name)) {
            seacchData.clear();
            for (FriendInfoBean bean : listData) {
                if (bean.getNickname().contains(name)) {
                    seacchData.add(bean);
                }
            }
            adapter.setList(seacchData);
            bindingView.mtListView.notifyDataSetChange();
        }
    }

    /***
     * 初始化
     */
    private void initViewTypeData() {
        //筛选
        for (int i = 0; i < listData.size(); i++) {
            bindingView.viewType.putTag(listData.get(i).getTag(), i);
        }
        // 添加存在用户的首字母列表
        bindingView.viewType.addItemView(UserUtil.friendParseString(listData));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        phoneListUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {
        private List<FriendInfoBean> list;


        public void setList(List<FriendInfoBean> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final FriendInfoBean bean = list.get(position);
            if (recentFriends != null && recentFriends.size() > 0 && position == 0) {
                holder.tvMessge.setVisibility(View.VISIBLE);
            } else {
                holder.tvMessge.setVisibility(View.GONE);
            }
            if (bean.isRegister()) {
                holder.btnAdd.setText("邀请");
                holder.btnAdd.setBackground(getResources().getDrawable(R.drawable.bg_btn_blue));
                holder.txtName.setText(bean.getPhoneremark());
            } else {
                holder.btnAdd.setText("添加");
                holder.btnAdd.setBackground(getResources().getDrawable(R.drawable.bg_btn_green));
                holder.txtName.setText(bean.getNickname() + "  (" + bean.getPhoneremark() + ")");
            }
            holder.txtType.setVisibility(bean.isShowPinYin() ? View.GONE : View.VISIBLE);
            holder.txtType.setText(bean.getTag());
            Glide.with(context).load(bean.getAvatar())
                    .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);

            holder.txtRemark.setText(bean.getPhone());
            if (!bean.isShowPinYin()) {
                if (position != 0) {
                    FriendInfoBean lastbean = list.get(position - 1);
                    if (lastbean.getTag().equals(bean.getTag())) {
                        holder.viewType.setVisibility(View.GONE);
                    } else {
                        holder.viewType.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.viewType.setVisibility(View.VISIBLE);
                }
            } else {
                holder.viewType.setVisibility(View.GONE);
            }
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!DoubleUtils.isFastDoubleClick()) {
                        if (bean.isRegister()) {
                            Uri smsToUri = Uri.parse("smsto:" + bean.getPhone());
                            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                            //短信内容
                            intent.putExtra("sms_body", getResources().getString(R.string.send_note_message));
                            startActivity(intent);
                        } else {
                            onAddFriend(bean, position);
                        }
                    }
                }
            });
            holder.viewRoot.setOnClickListener(o -> {
                if (!DoubleUtils.isFastDoubleClick()) {
                    if (!bean.isRegister()) {
                        startActivity(new Intent(context, UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, bean.getUid()));
                    }
                }
            });
        }

        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_friend_match, view, false));
            return holder;
        }

        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout viewType;
            private TextView txtType;
            private ImageView imgHead;
            private TextView txtName;
            private Button btnAdd;
            private TextView txtRemark;
            private TextView tvMessge;
            private View viewRoot;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewType = convertView.findViewById(R.id.view_type);
                txtType = convertView.findViewById(R.id.txt_type);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                btnAdd = convertView.findViewById(R.id.btn_add);
                txtRemark = convertView.findViewById(R.id.txt_remark);
                tvMessge = convertView.findViewById(R.id.tv_messge);
                viewRoot = convertView.findViewById(R.id.layout_root);
            }
        }
    }

    private void onAddFriend(FriendInfoBean bean, @SuppressLint("RecyclerView") int position) {
        AlertTouch alertTouch = new AlertTouch();
        alertTouch.init(FriendMatchActivity.this, "好友验证", "确定", 0, new AlertTouch.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes(String content) {
                taskFriendApply(bean.getUid(), content, bean.getPhoneremark(), position);
            }
        });
        alertTouch.show();
        alertTouch.setContent("我是" + UserAction.getMyInfo().getName());
        alertTouch.setEdHintOrSize(null, 60);
    }

    /**
     * 通讯录匹配
     *
     * @param phoneList
     */
    private void taskUserMatchPhone(List<PhoneBean> phoneList) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("phoneList", phoneList);
        params.put("isFirst", isFirstUpload ? CoreEnum.ECheckType.YES : CoreEnum.ECheckType.NO);
        userAction.getUserMatchPhone(params, new CallBack<ReturnBean<AddressBookMatchingBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<AddressBookMatchingBean>> call, Response<ReturnBean<AddressBookMatchingBean>> response) {
                if (response.body() == null) {
                    return;
                }
                try {
                    if (response.body().isOk()) {
                        AddressBookMatchingBean addressBookMatchingBean = response.body().getData();
                        List<FriendInfoBean> friendInfoBeans = addressBookMatchingBean.getMatchList();
                        listData.addAll(friendInfoBeans);
                        addNoRegisterUser(addressBookMatchingBean.getNotExistList());
                        // 不显示手机通讯录匹配红点标记
                        SpUtil.getSpUtil().putSPValue(Preferences.IS_FIRST_UPLOAD_PHONE + UserAction.getMyId(), false);

                        for (FriendInfoBean bean : listData) {
                            bean.toTag();
                        }
                        // 把最近的数据把到另外一个集合
                        filterData(listData);
                        //筛选
                        Collections.sort(listData, new Comparator<FriendInfoBean>() {
                            @Override
                            public int compare(FriendInfoBean o1, FriendInfoBean o2) {
                                return o1.getTag().hashCode() - o2.getTag().hashCode();
                            }
                        });
                        // 把#数据放到末尾
                        tempData.clear();
                        for (int i = listData.size() - 1; i >= 0; i--) {
                            FriendInfoBean bean = listData.get(i);
                            if (bean.getTag().hashCode() == 35) {
                                tempData.add(bean);
                                listData.remove(i);
                            }
                        }
                        listData.addAll(tempData);
                        initViewTypeData();
                        // 如果有数据就放到开始位置
                        if (recentFriends != null && recentFriends.size() > 0) {
                            listData.addAll(0, recentFriends);
                        }
                        adapter.setList(listData);
                        hadUploadTimes++;
                        if (ifSub) {
                            if (hadUploadTimes == needUploadTimes) {
                                if (listData == null || listData.size() == 0) {
                                    ToastUtil.show(context, "没有匹配的手机联系人");
                                }
                                bindingView.mtListView.notifyDataSetChange();
                                bindingView.pbWait.setVisibility(View.GONE);
                            } else {
                                taskUserMatchPhone(subList.get(hadUploadTimes));
                            }
                        } else {
                            if (listData == null || listData.size() == 0) {
                                ToastUtil.show(context, "没有匹配的手机联系人");
                            }
                            bindingView.mtListView.notifyDataSetChange();
                        }
                    } else {
                        bindingView.mtListView.notifyDataSetChange();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ReturnBean<AddressBookMatchingBean>> call, Throwable t) {
                super.onFailure(call, t);
                hadUploadTimes++;
                if (ifSub) {
                    if (hadUploadTimes == needUploadTimes) {
                        bindingView.pbWait.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void addNoRegisterUser(List<AddressBookMatchingBean.NotExistListBean> list) {
        if (list != null && listData != null) {
            for (AddressBookMatchingBean.NotExistListBean bean : list) {
                FriendInfoBean friendInfoBean = new FriendInfoBean();
                friendInfoBean.setNickname(bean.getPhoneremark());
                friendInfoBean.setPhone(bean.getPhone());
                friendInfoBean.setPhoneremark(bean.getPhoneremark());
                friendInfoBean.setRegister(true);
                listData.add(friendInfoBean);
            }
        }
    }

    private void onDelete(Long uid) {
        if (recentFriends != null && recentFriends.size() > 0) {
            for (int i = recentFriends.size() - 1; i >= 0; i--) {
                if (uid != null && uid.longValue() == recentFriends.get(i).getUid().longValue()) {
                    recentFriends.remove(i);
                    break;
                }
            }
        }
    }

    private void taskFriendApply(final Long uid, String sayHi, String contactName, final int position) {
        userAction.friendApply(uid, sayHi, contactName, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    onDelete(uid);
                    listData.remove(position);
                    bindingView.mtListView.notifyDataSetChange();
                }
                ToastUtil.show(FriendMatchActivity.this, response.body().getMsg());
            }
        });
    }

}
