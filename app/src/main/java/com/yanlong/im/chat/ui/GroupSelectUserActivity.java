package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PySortView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 从通讯录中选择单个用户
 */
public class GroupSelectUserActivity extends AppActivity {
    public static final String GID = "gid";
    public static final int RET_CODE_SELECTUSR = 18245;
    public static final String UID = "uid";
    public static final String MEMBERNAME = "membername";
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;

    private net.cb.cb.library.view.MultiListView mtListView;
    private List<UserInfo> listData = new ArrayList<>();
    private PySortView viewType;
    private String gid;

    //自动寻找控件
    private void findViews() {
        gid = getIntent().getStringExtra(GID);
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mtListView);
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

        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
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
                if(response == null){
                    return;
                }
                if (response.body().isOk()) {
                    listData = response.body().getData().getUsers();
                    mtListView.notifyDataSetChange();
                }
            }
        });
    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder hd, int position) {

            final UserInfo bean = listData.get(position);
            hd.txtType.setText(bean.getTag());
            hd.imgHead.setImageURI(Uri.parse("" + bean.getHead()));
            hd.txtName.setText(bean.getName());
            hd.viewType.setVisibility(View.VISIBLE);
            if (position > 0) {
                UserInfo lastbean = listData.get(position - 1);
                if (lastbean.getTag().equals(bean.getTag())) {
                    hd.viewType.setVisibility(View.GONE);
                }
            }

            hd.ckSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Intent intent = new Intent();
                    intent.putExtra(UID,bean.getUid()+"");
                    if(!TextUtils.isEmpty(bean.getMembername())){
                        intent.putExtra(MEMBERNAME,bean.getMembername());
                    }else{
                        intent.putExtra(MEMBERNAME,bean.getName4Show());
                    }
                    setResult(RET_CODE_SELECTUSR, intent);
                    finish();
                }
            });
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
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
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
