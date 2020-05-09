package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.FriendViewModel;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.ui.SearchFriendGroupActivity;
import com.yanlong.im.repository.ApplicationRepository;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.PySortView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
    private FriendViewModel viewModel;
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
        MyAppLication.INSTANCE().addFriendChangeListener(friendChangeListener);
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
        viewModel = new FriendViewModel();
        viewModel.initFriend();
        //显示右侧字母
        if(viewModel.friends!=null)viewType.addItemView(userParseString());
        initEvent();
    }

    private ApplicationRepository.FriendChangeListener friendChangeListener = new ApplicationRepository.FriendChangeListener() {
        @Override
        public void init(RealmResults<UserInfo> friends) {
            viewModel.initFriend();
            //显示右侧字母
            updateViewType();
        }

        @Override
        public void delete(int[] positions) {
            updateViewType();
        }

        @Override
        public void insert(int[] positions) {
            updateViewType();
        }

        @Override
        public void update(int[] positions) {
            updateViewType();
        }
    };

    /**
     * 更新字母
     */
    private void updateViewType() {
        try {
            //显示右侧字母
            viewType.addItemView(userParseString());
            mtListView.getListView().getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    /**
     * 获取用户的首字母列表
     *
     * @return
     */
    public List<String> userParseString() {
        List<String> list = new ArrayList<>();
        try {
            //数据库中存储的是Z1，便于排序
            if (viewModel.friends != null) {
                for (int i = 0; i < viewModel.friends.size(); i++) {
                    String tag = viewModel.friends.get(i).getTag();
                    list.add(tag);
                    viewType.putTag(tag, i);
                }
            }
        } catch (Exception e) {
        }
        return list;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyAppLication.INSTANCE().removeFriendChangeListener(friendChangeListener);
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
        mtListView.setEvent(new MultiListView.Event() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                if(MyAppLication.INSTANCE().repository!=null)MyAppLication.INSTANCE().repository.loadMoreFriends();
            }

            @Override
            public void onLoadFail() {
            }
        });
        //必须在setEvent后调用
        mtListView.getSwipeLayout().setEnabled(false);
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
