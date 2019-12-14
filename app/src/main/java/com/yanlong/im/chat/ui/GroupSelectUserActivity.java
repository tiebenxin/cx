package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.PySortView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 从通讯录中选择单个用户
 */
public class GroupSelectUserActivity extends AppActivity {
    public static final String GID = "gid";
    public static final int RET_CODE_SELECTUSR = 18245;
    public static final String TYPE = "type";
    public static final String UID = "uid";
    public static final String MEMBERNAME = "membername";
    private HeadView headView;
    private ActionbarView actionbar;
    private UserInfo mUserBean;

    private MultiListView mtListView;
    private List<UserInfo> listData = new ArrayList<>();
    private List<UserInfo> tempData = new ArrayList<>();
    private PySortView viewType;
    private String gid;
    private int type;// 0 表示转让群主选择联系人  1 @用戶
    private ClearEditText edtSearch;
    private RecyclerViewAdapter adapter;
    private LinearLayout llAtAll;
    private AlertYesNo alertYesNo;

    //自动寻找控件
    private void findViews() {
        gid = getIntent().getStringExtra(GID);
        type = getIntent().getIntExtra(TYPE, 0);
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mtListView);
        edtSearch = findViewById(R.id.edt_search);
        viewType = findViewById(R.id.view_type);
        llAtAll = findViewById(R.id.ll_at_all);
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
        actionbar.getTxtRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(mUserBean);
            }
        });
        adapter = new RecyclerViewAdapter();
        mtListView.init(adapter);
        mtListView.getLoadView().setStateNormal();
        //联动
        viewType.setLinearLayoutManager(mtListView.getLayoutManager());
        viewType.setListView(mtListView.getListView());
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                adapter.getFilter().filter(sequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        llAtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(UID, 0 + "");
                intent.putExtra(MEMBERNAME, "所有人");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        findViews();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (alertYesNo != null) {
            alertYesNo.dismiss();
            alertYesNo = null;
        }
        super.onDestroy();
    }

    private void initData() {
        taskGetInfo();
        if (type == 0) {
            actionbar.setTxtRight("完成");
            actionbar.setTitle("选择新群主");
            actionbar.getTxtRight().setVisibility(View.GONE);
        }
    }

    private void taskGetInfo() {
        new MsgAction().groupInfo(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response == null) {
                    return;
                }
                if (response.body().isOk()) {
                    if (type == 0) {
                        listData = delectMaster(response.body().getData());
                    } else {
                        listData = delectMyslfe(response.body().getData());
                    }
                    showAtAll(response.body().getData());
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

                    adapter.setList(listData);
                    mtListView.notifyDataSetChange();
                    for (int i = 0; i < listData.size(); i++) {
                        //UserInfo infoBean:
                        viewType.putTag(listData.get(i).getTag(), i);
                    }
                    // 添加存在用户的首字母列表
                    viewType.addItemView(UserUtil.userParseString(listData));
                }
            }
        });
    }

    private List<UserInfo> delectMaster(Group group) {
        List<MemberUser> list = group.getUsers();
        List<UserInfo> users = new ArrayList<>();
        if (list != null) {
            int len = list.size();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    MemberUser member = list.get(i);
                    if (group.getMaster().equals(member.getUid() + "")) {
//                        list.remove(i);
                    } else {
                        UserInfo info = MessageManager.getInstance().memberToUser(member);
                        users.add(info);
                    }
                }
            }
        }
        return users;
    }

    private List<UserInfo> delectMyslfe(Group group) {
        Long uid = UserAction.getMyId();
        List<MemberUser> list = group.getUsers();
        List<UserInfo> users = new ArrayList<>();
        if (list != null) {
            int len = list.size();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    MemberUser member = list.get(i);
                    if (uid.equals(member.getUid())) {
//                        list.remove(i);
                    } else {
                        UserInfo info = MessageManager.getInstance().memberToUser(member);
                        users.add(info);
                    }
                }
            }
        }
        return users;
    }


    private void showAtAll(Group group) {
        long uid = UserAction.getMyId();
        String master = group.getMaster();
        if (master.equals(uid + "") && type != 0) {
            llAtAll.setVisibility(View.VISIBLE);
        }

    }

    private void showDialog(UserInfo bean) {
        alertYesNo = new AlertYesNo();
        alertYesNo.init(GroupSelectUserActivity.this, "转让群", bean.getName() + "将成为该群群主，确定后你将立刻失去群主身份",
                "确定", "取消", new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        Intent intent = new Intent();
                        intent.putExtra(UID, bean.getUid() + "");
                        intent.putExtra(MEMBERNAME, bean.getName());
                        setResult(RET_CODE_SELECTUSR, intent);
                        finish();
                    }
                });
        alertYesNo.show();
    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> implements Filterable {
        private List<UserInfo> mFilterList = new ArrayList<>();
        private List<UserInfo> mSourceList = new ArrayList<>();

        public void setList(List<UserInfo> list) {
            mFilterList = list;
            mSourceList = list;
        }


        @Override
        public int getItemCount() {
            return mFilterList == null ? 0 : mFilterList.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(final RCViewHolder hd, int position) {

            final UserInfo bean = mFilterList.get(position);
            hd.txtType.setText(bean.getTag());
            // hd.imgHead.setImageURI(Uri.parse("" + bean.getHead()));
            Glide.with(context).load(bean.getHead())
                    .apply(GlideOptionsUtil.headImageOptions()).into(hd.imgHead);
            if (!TextUtils.isEmpty(bean.getMembername())) {
                hd.txtName.setText(bean.getMembername());
            } else {
                hd.txtName.setText(bean.getName4Show());
            }
            hd.viewType.setVisibility(View.VISIBLE);
            hd.viewLine.setVisibility(View.VISIBLE);
            if (position > 0) {
                UserInfo lastbean = mFilterList.get(position - 1);
                if (lastbean.getTag().equals(bean.getTag())) {
                    hd.viewType.setVisibility(View.GONE);
                }
            }
            if (position == getItemCount() - 1) {
                hd.viewLine.setVisibility(View.GONE);
            } else {
                UserInfo lastbean = mFilterList.get(position + 1);
                if (!lastbean.getTag().equals(bean.getTag())) {
                    hd.viewLine.setVisibility(View.GONE);
                }
            }
            if (bean.isChecked()) {
                hd.ckSelect.setChecked(true);
            } else {
                hd.ckSelect.setChecked(false);
            }

            hd.layoutRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUserBean = bean;
                    onItemClick(bean);
                }
            });
            hd.ckSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUserBean = bean;
                    onItemClick(bean);
                }
            });
        }

        private void onItemClick(UserInfo bean) {
            for (UserInfo info : mFilterList) {
                if (info.getUid().equals(bean.getUid())) {
                    info.setChecked(!bean.isChecked());
                } else {
                    info.setChecked(false);
                }
            }
            mtListView.notifyDataSetChange();
            if (type == 0) {
                actionbar.getTxtRight().setVisibility(bean.isChecked() ? View.VISIBLE : View.GONE);
            } else {
                Intent intent = new Intent();
                intent.putExtra(UID, bean.getUid() + "");
                intent.putExtra(MEMBERNAME, !TextUtils.isEmpty(bean.getMembername()) ? bean.getMembername() : bean.getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_group_create, view, false));
            return holder;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                //执行过滤操作
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        //没有过滤的内容，则使用源数据
                        mFilterList = mSourceList;
                    } else {
                        List<UserInfo> filteredList = new ArrayList<>();
                        for (UserInfo userInfo : mSourceList) {
                            //这里根据需求，添加匹配规则
                            if (userInfo.getName4Show().contains(charString)) {
                                filteredList.add(userInfo);
                            }
                        }

                        mFilterList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilterList;
                    return filterResults;
                }

                //把过滤后的值返回出来
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mFilterList = (List<UserInfo>) filterResults.values;
                    mtListView.notifyDataSetChange();
                }
            };
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout viewType;
            private TextView txtType;
            private ImageView imgHead;
            private TextView txtName;
            private CheckBox ckSelect;
            private View layoutRoot;
            private View viewLine;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewType = convertView.findViewById(R.id.view_type);
                txtType = convertView.findViewById(R.id.txt_type);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                ckSelect = convertView.findViewById(R.id.ck_select);
                layoutRoot = convertView.findViewById(R.id.layout_root);
                viewLine = convertView.findViewById(R.id.view_line);
            }
        }
    }

}
