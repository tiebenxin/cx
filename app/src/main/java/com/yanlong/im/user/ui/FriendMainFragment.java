package com.yanlong.im.user.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ui.ChatActivity;
import com.yanlong.im.chat.ui.GroupSaveActivity;
import com.yanlong.im.chat.ui.SearchFriendGroupActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PySortView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 首页通讯录
 */
public class FriendMainFragment extends Fragment {
    private View rootView;
 //   private net.cb.cb.library.view.ClearEditText edtSearch;
    private View viewSearch;
    private net.cb.cb.library.view.MultiListView mtListView;
    private PySortView viewType;
    private ActionbarView actionbar;


    //自动寻找控件
    private void findViews(View rootView) {
//        edtSearch = (net.cb.cb.library.view.ClearEditText) rootView.findViewById(R.id.edt_search);
        viewSearch =  rootView.findViewById(R.id.view_search);
        mtListView = (net.cb.cb.library.view.MultiListView) rootView.findViewById(R.id.mtListView);
        viewType = (PySortView) rootView.findViewById(R.id.view_type);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
/*            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
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
                RCViewFuncHolder hd = (RCViewFuncHolder) holder;
                hd.viewAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ToastUtil.show(getContext(), "添加朋友");
                        startActivity(new Intent(getContext(), FriendApplyAcitvity.class));
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
            } else if (holder instanceof RCViewHolder) {

                final UserInfo bean = listData.get(position);
                RCViewHolder hd = (RCViewHolder) holder;
                hd.txtType.setText(bean.getTag());
                hd.imgHead.setImageURI(Uri.parse("" + bean.getHead()));
                hd.txtName.setText(bean.getName4Show());

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
            private View viewType;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                txtType = (TextView) convertView.findViewById(R.id.txt_type);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
                txtName = (TextView) convertView.findViewById(R.id.txt_name);
                viewType = convertView.findViewById(R.id.view_type);

            }

        }


        //自动生成ViewHold
        public class RCViewFuncHolder extends RecyclerView.ViewHolder {
            private LinearLayout viewAdd;
            private LinearLayout viewMatch;
            private LinearLayout viewGroup;

            //自动寻找ViewHold
            public RCViewFuncHolder(View convertView) {
                super(convertView);
                viewAdd = (LinearLayout) convertView.findViewById(R.id.view_add);
                viewMatch = (LinearLayout) convertView.findViewById(R.id.view_match);
                viewGroup = (LinearLayout) convertView.findViewById(R.id.view_group);
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

        for (int i = 0; i < listData.size(); i++) {
            //UserInfo infoBean:
            viewType.putTag(listData.get(i).getTag(), i);
        }

        mtListView.notifyDataSetChange();




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


}
