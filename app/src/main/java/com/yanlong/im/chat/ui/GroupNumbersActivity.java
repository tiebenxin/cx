package com.yanlong.im.chat.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.TouchUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PySortView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 群成员操作
 */
public class GroupNumbersActivity extends AppActivity {
    public static final String AGM_GID = "gid";

    //成员列表
    public static final String AGM_NUMBERS_JSON = "number_json";
    //1:添加,2:删除
    public static final String AGM_TYPE = "type";
    public static final int TYPE_ADD =1 ;
    public static final int TYPE_DEL =2 ;

    private String gid;
    private List<UserInfo> listData;
    private Integer type;

    private Gson gson=new Gson();

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewSearch;
    private RecyclerView topListView;
    private net.cb.cb.library.view.MultiListView mtListView;
    private PySortView viewType;

    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewSearch = (LinearLayout) findViewById(R.id.view_search);
        topListView = (RecyclerView) findViewById(R.id.topListView);
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
        viewType = findViewById(R.id.view_type);
    }


    //自动生成的控件事件
    private void initEvent() {
        listData=gson.fromJson(getIntent().getStringExtra(AGM_NUMBERS_JSON),new TypeToken<List<UserInfo>>(){}.getType());
        type=getIntent().getIntExtra(AGM_TYPE,TYPE_ADD);
        gid=getIntent().getStringExtra(AGM_GID);


        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                taskOption();
            }
        });
        actionbar.setTxtRight("确定");

        actionbar.setTitle(type==TYPE_ADD?"加入群":"移出群");
        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();

        //顶部处理
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        topListView.setLayoutManager(linearLayoutManager);
        topListView.setAdapter(new RecyclerViewTopAdapter());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        findViews();
        initEvent();
        initData();
    }

    private void initData() {
        taskListData();


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
                    if (isChecked) {
                        listDataTop.add(bean);
                    } else {
                        listDataTop.remove(bean);
                    }
                    topListView.getAdapter().notifyDataSetChanged();
                }
            });

            TouchUtil.expandTouch(hd.ckSelect);


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
                viewType = (LinearLayout) convertView.findViewById(R.id.view_type);
                txtType = (TextView) convertView.findViewById(R.id.txt_type);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
                txtName = (TextView) convertView.findViewById(R.id.txt_name);
                ckSelect = (CheckBox) convertView.findViewById(R.id.ck_select);
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

            holder.imgHead.setImageURI(Uri.parse(listDataTop.get(position).getHead()));
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


    private MsgAction msgACtion = new MsgAction();
    private UserDao userDao = new UserDao();




    private void taskListData() {





        for (int i = 0; i < listData.size(); i++) {
            //UserInfo infoBean:
            viewType.putTag(listData.get(i).getTag(), i);
        }


    }

    /***
     * 提交处理
     */
    private void taskOption() {
        CallBack<ReturnBean> callback = new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if(response.body()==null)
                    return;
                ToastUtil.show(getContext(),response.body().getMsg());
                if(response.body().isOk()){
                    finish();
                }

            }
        };
        if(type==TYPE_ADD){
            msgACtion.groupAdd(gid, listDataTop,callback);
        }else{
            msgACtion.groupRemove(gid, listDataTop,callback);



        }

    }


}
