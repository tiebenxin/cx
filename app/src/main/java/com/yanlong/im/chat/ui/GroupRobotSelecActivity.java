package com.yanlong.im.chat.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.RobotInfoBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupRobotSelecActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.ClearEditText edtSearch;
    private net.cb.cb.library.view.MultiListView mtListView;

    private List<RobotInfoBean> listData = new ArrayList<>();
private String gid;

    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        edtSearch = (net.cb.cb.library.view.ClearEditText) findViewById(R.id.edt_search);
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
    }


    //自动生成的控件事件
    private void initEvent() {
         gid = getIntent().getStringExtra(GroupRobotActivity.AGM_GID);
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

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    taskSearch();
                }
                return false;
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_robot_select);
        findViews();
        initEvent();
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            final RobotInfoBean infobean = listData.get(position);
            holder.txtInfoTitle.setText(infobean.getRname());
            holder.imgInfoIcon.setImageURI(Uri.parse(infobean.getAvatar()));
            holder.txtInfoMore.setText(infobean.getRobotDescription());

            holder.btnInfoAdd.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setResult(GroupRobotActivity.RET_SELECT,new Intent()
                            .putExtra(GroupRobotActivity.AGM_GID,gid)
                            .putExtra(GroupRobotActivity.AGM_RID,infobean.getRid()));
                    finish();

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), GroupRobotActivity.class).putExtra(GroupRobotActivity.AGM_SHOW_TYPE, GroupRobotActivity.AGM_SHOW_TYPE_ADD)
                            .putExtra(GroupRobotActivity.AGM_GID, gid)
                            .putExtra(GroupRobotActivity.AGM_RID, infobean.getRid())

                    );
                }
            });

        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_group_robot, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private com.facebook.drawee.view.SimpleDraweeView imgInfoIcon;
            private TextView txtInfoTitle;
            private TextView txtInfoMore;
            private Button btnInfoAdd;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgInfoIcon = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_info_icon);
                txtInfoTitle = (TextView) convertView.findViewById(R.id.txt_info_title);
                txtInfoMore = (TextView) convertView.findViewById(R.id.txt_info_more);
                btnInfoAdd = (Button) convertView.findViewById(R.id.btn_info_add);
            }

        }
    }


    private String key = "";

    private MsgAction msgAction=new MsgAction();
    private void taskSearch() {
        key = edtSearch.getText().toString();
        if (key.length() <= 0) {
            return;
        }
        msgAction.robotSearch(key, new CallBack<ReturnBean<List<RobotInfoBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<RobotInfoBean>>> call, Response<ReturnBean<List<RobotInfoBean>>> response) {
                if (response.body()==null)
                    return;

                listData=response.body().getData();
                mtListView.notifyDataSetChange();
            }
        });



    }


}