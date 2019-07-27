package com.yanlong.im.user.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.GroupSaveActivity;
import com.yanlong.im.chat.ui.SearchFriendGroupActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PySortView;
import net.cb.cb.library.view.StrikeButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 首页通讯录
 */
public class FriendMainFragment extends Fragment {
    private View rootView;
    private View viewSearch;
    private net.cb.cb.library.view.MultiListView mtListView;
    private PySortView viewType;
    private ActionbarView actionbar;


    //自动寻找控件
    private void findViews(View rootView) {
        viewSearch = rootView.findViewById(R.id.view_search);
        mtListView =  rootView.findViewById(R.id.mtListView);
        viewType =  rootView.findViewById(R.id.view_type);
        actionbar = rootView.findViewById(R.id.action_bar);
    }

    //自动生成的控件事件
    private void initEvent() {
        mtListView.init(new RecyclerViewAdapter());

        //联动
        viewType.setListView(mtListView.getListView());

        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {

            }

            @Override
            public void onRight() {
                startActivity(new Intent(getContext(), FriendAddAcitvity.class));
            }
        });
        viewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchFriendGroupActivity.class));
            }
        });
    }

    private List<UserInfo> listData = new ArrayList<>();

    private void initData() {
        taskRefreshListData();


    }

    public FriendMainFragment() {
        // Required empty public constructor
    }


    public static FriendMainFragment newInstance() {
        FriendMainFragment fragment = new FriendMainFragment();
        Bundle args = new Bundle();
    /*    args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //taskListData();重新从书库刷新数据,还是只是刷新页面重新显示在线时间
        mtListView.notifyDataSetChange();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
/*            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgm_msg_friend, null);
        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layparm);
        findViews(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();
        initData();
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    /*
   private MainActivity getActivityMe() {
        return (MainActivity) getActivity();
    }

    */

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof RCViewFuncHolder) {
                final RCViewFuncHolder hd = (RCViewFuncHolder) holder;
                hd.viewAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ToastUtil.show(getContext(), "添加朋友");
                        taskApplyNumClean();
                        hd.sbApply.setNum(0);
                        startActivity(new Intent(getContext(), FriendApplyAcitvity.class));
                    }
                });
                hd.viewAddFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), FriendAddAcitvity.class));
                    }
                });
                hd.viewGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ToastUtil.show(getContext(), "群消息");
                        startActivity(new Intent(getContext(), GroupSaveActivity.class));
                    }
                });
                hd.viewMatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ToastUtil.show(getContext(), "匹配");
                        startActivity(new Intent(getContext(), FriendMatchActivity.class));
                    }
                });
                hd.sbApply.setNum(taskGetApplyNum());
            } else if (holder instanceof RCViewHolder) {

                final UserInfo bean = listData.get(position);
                RCViewHolder hd = (RCViewHolder) holder;
                hd.txtType.setText(bean.getTag());
                hd.imgHead.setImageURI(Uri.parse("" + bean.getHead()));
                hd.txtName.setText(bean.getName4Show());
                if(bean.getLastonline()>0){
                    hd.txtTime.setText(TimeToString.getTimeOline(bean.getLastonline()) );
                    hd.txtTime.setVisibility(View.VISIBLE);
                }else{
                    hd.txtTime.setVisibility(View.GONE);
                }



                UserInfo lastbean = listData.get(position - 1);
                if (lastbean.getTag().equals(bean.getTag())) {
                    hd.viewType.setVisibility(View.GONE);
                } else {
                    hd.viewType.setVisibility(View.VISIBLE);
                }
                hd.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, bean.getUid()));


                    }
                });
            }


        }


        @Override
        public int getItemViewType(int position) {

            return position == 0 ? 0 : 1;
        }

        //自动寻找ViewHold
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup view, int i) {
            if (i == 0) {
                RCViewFuncHolder holder = new RCViewFuncHolder(getLayoutInflater().inflate(R.layout.item_msg_friend_fun, view, false));
                return holder;
            } else {
                RCViewHolder holder = new RCViewHolder(getLayoutInflater().inflate(R.layout.item_msg_friend, view, false));
                return holder;
            }

        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private TextView txtType;
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private TextView txtName;
            private TextView txtTime;
            private View viewType;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                txtType =  convertView.findViewById(R.id.txt_type);
                imgHead =  convertView.findViewById(R.id.img_head);
                txtName =  convertView.findViewById(R.id.txt_name);
                txtTime =  convertView.findViewById(R.id.txt_time);
                viewType = convertView.findViewById(R.id.view_type);

            }

        }


        //自动生成ViewHold
        public class RCViewFuncHolder extends RecyclerView.ViewHolder {
            private LinearLayout viewAdd;
            private LinearLayout viewAddFriend;
            private LinearLayout viewMatch;
            private LinearLayout viewGroup;
            private StrikeButton sbApply;

            //自动寻找ViewHold
            public RCViewFuncHolder(View convertView) {
                super(convertView);
                viewAdd = convertView.findViewById(R.id.view_add);
                viewAddFriend = convertView.findViewById(R.id.view_add_friend);
                viewMatch = convertView.findViewById(R.id.view_match);
                viewGroup = convertView.findViewById(R.id.view_group);
                sbApply = convertView.findViewById(R.id.sb_apply);
            }
        }
    }

    private UserDao userDao = new UserDao();
    private UserAction userAction = new UserAction();

    private void taskListData() {


        listData = userDao.friendGetAll();


        UserInfo topBean = new UserInfo();
        topBean.setTag("↑");
        listData.add(0, topBean);
        //筛选
        Collections.sort(listData);

        for (int i = 0; i < listData.size(); i++) {
            //UserInfo infoBean:
            viewType.putTag(listData.get(i).getTag(), i);
        }

        mtListView.notifyDataSetChange();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFriend(EventRefreshFriend event) {
        if (event.isLocal()){
            taskListData();
        }else{
            taskRefreshListData();
        }


    }

    public void taskRefreshListData() {
        userAction.friendGet4Me(new CallBack<ReturnBean<List<UserInfo>>>() {
                                    @Override
                                    public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {
                                        taskListData();
                                    }

                                    @Override
                                    public void onFailure(Call<ReturnBean<List<UserInfo>>> call, Throwable t) {
                                        super.onFailure(call, t);
                                        taskListData();
                                    }
                                }

        );
    }



    private MsgDao msgDao = new MsgDao();

    /***
     * 申请数量
     * @return
     */
    private int taskGetApplyNum() {

        return msgDao.remidGet("friend_apply");


    }

    private void taskApplyNumClean() {
        msgDao.remidClear("friend_apply");

        EventBus.getDefault().post(new EventRefreshMainMsg());
    }


}
