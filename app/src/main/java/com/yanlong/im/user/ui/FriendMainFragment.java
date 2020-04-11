package com.yanlong.im.user.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.MainViewModel;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.ui.SearchFriendGroupActivity;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PySortView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/***
 * 首页通讯录
 */
public class FriendMainFragment extends Fragment {
    private View rootView;
    private View viewSearch;
    private net.cb.cb.library.view.MultiListView mtListView;
    private PySortView viewType;
    private ActionbarView actionbar;
    private MainViewModel viewModel;
    private FriendMainFragmentAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
//        mtListView.notifyDataSetChange();
        mtListView.getListView().getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MainActivity的viewModel
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //MainActivity的viewModel
        viewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(MyAppLication.getInstance())).get(MainViewModel.class);
        initEvent();
        initObserver();
    }

    private void initObserver() {
        viewModel.friends.addChangeListener(friendsChangeListener);
    }

    private OrderedRealmCollectionChangeListener<RealmResults<UserInfo>> friendsChangeListener = new OrderedRealmCollectionChangeListener<RealmResults<UserInfo>>() {
        @Override
        public void onChange(RealmResults<UserInfo> userInfos, OrderedCollectionChangeSet changeSet) {
            //显示右侧字母
            viewType.addItemView(userParseString());
            mtListView.getListView().getAdapter().notifyDataSetChanged();
        }
    };

    /**
     * 获取用户的首字母列表
     *
     * @return
     */
    public  List<String> userParseString() {
        List<String> list = new ArrayList<>();
        try {
            //数据库中存储的是a>Z，便于排序
            if (viewModel.friends != null) {
                String lastLetter = null;
                for (int i = 0; i < viewModel.friends.size(); i++) {
                    String tag=viewModel.friends.get(i).getTag();
                    if(tag.equals(UserInfo.FRIEND_NUMBER_TAG)){//FRIEND_TAG表示数字名，显示#放到最后
                        list.add("#");
                    }else if(!list.contains(tag)){
                        list.add(tag);
                    }
                }
            }
        } catch (Exception e) {
        }
        return list;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel.friends != null)
            viewModel.friends.removeChangeListener(friendsChangeListener);
        EventBus.getDefault().unregister(this);
    }


    //自动寻找控件
    private void findViews(View rootView) {
        viewSearch = rootView.findViewById(R.id.view_search);
        mtListView = rootView.findViewById(R.id.mtListView);
        viewType = rootView.findViewById(R.id.view_type);
        actionbar = rootView.findViewById(R.id.action_bar);
    }

    //自动生成的控件事件
    private void initEvent() {
        adapter = new FriendMainFragmentAdapter(requireContext(), viewModel);
        mtListView.init(adapter);
        mtListView.getLoadView().setStateNormal();
        //联动
        viewType.setLinearLayoutManager(mtListView.getLayoutManager());
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFriend(EventRefreshFriend event) {
        if (event.isLocal()) {
            long uid = event.getUid();
            @CoreEnum.ERosterAction int action = event.getRosterAction();
            switch (action) {
                case CoreEnum.ERosterAction.REMOVE_FRIEND:
                    viewModel.setToStranger(uid);
                    break;
                case CoreEnum.ERosterAction.BLACK://添加或者解除黑名单
                    break;
                case CoreEnum.ERosterAction.REQUEST_FRIEND://请求添加为好友
                    mtListView.getListView().getAdapter().notifyItemChanged(0, 0);
                    break;
                default:
                    if (uid > 0) {
                        mtListView.getListView().getAdapter().notifyDataSetChanged();
//           TODO             refreshPosition(uid);
                    } else if (event.getUser() != null && event.getUser() instanceof UserInfo) {
                        mtListView.getListView().getAdapter().notifyDataSetChanged();
//         TODO                refreshUser((UserInfo) event.getUser());
                    }
                    break;
            }
        } else {
            long uid = event.getUid();
            if (uid > 0) {
                @CoreEnum.ERosterAction int action = event.getRosterAction();
                switch (action) {
                    case CoreEnum.ERosterAction.REQUEST_FRIEND:
                    case CoreEnum.ERosterAction.ACCEPT_BE_FRIENDS:
                        viewModel.requestUserInfoAndSave(uid, action == CoreEnum.ERosterAction.ACCEPT_BE_FRIENDS ? ChatEnum.EUserType.FRIEND : ChatEnum.EUserType.STRANGE);
                        break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventOnlineChange(EventUserOnlineChange event) {
        if (event.getObject() instanceof UserInfo) {
            UserInfo info = (UserInfo) event.getObject();
            if (info != null) {
                mtListView.getListView().getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshOnlineStatus(EventRunState event) {
        if (event.getRun()) {
            viewModel.requestUsersOnlineStatus();
        }
    }


}
