package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.PySortView;

import java.util.ArrayList;
import java.util.Collections;
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

    private MultiListView mtListView;
    private List<UserInfo> listData = new ArrayList<>();
    private PySortView viewType;
    private String gid;
    private int type;
    private ClearEditText edtSearch;
    private RecyclerViewAdapter adapter;

    //自动寻找控件
    private void findViews() {
        gid = getIntent().getStringExtra(GID);
        type = getIntent().getIntExtra(TYPE,0);
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mtListView);
        edtSearch = findViewById(R.id.edt_search);
        viewType = findViewById(R.id.view_type);
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

        adapter = new RecyclerViewAdapter();
        mtListView.init(adapter);
        mtListView.getLoadView().setStateNormal();
        //联动
        viewType.setListView(mtListView.getListView());
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                ToastUtil.show(GroupSelectUserActivity.this,sequence.toString()+"");
                adapter.getFilter().filter(sequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    private void initData() {
        taskGetInfo();

    }

    private void taskGetInfo() {
        new MsgAction().groupInfo(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response == null) {
                    return;
                }
                if (response.body().isOk()) {
                    listData = delectMaster(response.body().getData());
                    Collections.sort(listData);
                    adapter.setList(listData);
                    mtListView.notifyDataSetChange();
                    for (int i = 0; i < listData.size(); i++) {
                        //UserInfo infoBean:
                        viewType.putTag(listData.get(i).getTag(), i);
                    }
                }
            }
        });
    }


    private List<UserInfo> delectMaster(Group group) {
        List<UserInfo> list = group.getUsers();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (group.getMaster().equals(list.get(i).getUid() + "")) {
                    list.remove(i);
                }
            }
            return list;
        }
        return null;
    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> implements Filterable {
        private List<UserInfo> mFilterList = new ArrayList<>();
        private List<UserInfo> mSourceList = new ArrayList<>();

        public void setList(List<UserInfo> list){
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
            hd.imgHead.setImageURI(Uri.parse("" + bean.getHead()));
            hd.txtName.setText(bean.getName4Show());
            hd.viewType.setVisibility(View.VISIBLE);
            if (position > 0) {
                UserInfo lastbean = mFilterList.get(position - 1);
                if (lastbean.getTag().equals(bean.getTag())) {
                    hd.viewType.setVisibility(View.GONE);
                }
            }

            hd.ckSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    hd.ckSelect.setChecked(false);


                    if(type == 0){
                        AlertYesNo alertYesNo = new AlertYesNo();
                        alertYesNo.init(GroupSelectUserActivity.this, "转让群", "确认转让群主吗?", "确定", "取消", new AlertYesNo.Event() {
                            @Override
                            public void onON() {

                            }

                            @Override
                            public void onYes() {
                                Intent intent = new Intent();
                                intent.putExtra(UID, bean.getUid() + "");
                                if (!TextUtils.isEmpty(bean.getMembername())) {
                                    intent.putExtra(MEMBERNAME, bean.getMembername());
                                } else {
                                    intent.putExtra(MEMBERNAME, bean.getName4Show());
                                }
                                setResult(RET_CODE_SELECTUSR, intent);
                                finish();
                            }
                        });
                        alertYesNo.show();
                    }else{
                        Intent intent = new Intent();
                        intent.putExtra(UID, bean.getUid() + "");
                        intent.putExtra(MEMBERNAME, bean.getName4Show());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
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
            private SimpleDraweeView imgHead;
            private TextView txtName;
            private CheckBox ckSelect;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewType = convertView.findViewById(R.id.view_type);
                txtType = convertView.findViewById(R.id.txt_type);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                ckSelect = convertView.findViewById(R.id.ck_select);
            }
        }
    }


}
