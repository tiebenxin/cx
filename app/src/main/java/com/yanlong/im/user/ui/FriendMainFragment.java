package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nim_lib.config.Preferences;
import com.google.gson.Gson;
import com.yanlong.im.FriendViewModel;
import com.yanlong.im.MainActivity;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.SearchFriendGroupActivity;
import com.yanlong.im.repository.ApplicationRepository;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.AddressBookMatchingBean;
import com.yanlong.im.user.bean.FriendInfoBean;
import com.yanlong.im.user.bean.PhoneBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.CommonUtils;
import com.yanlong.im.utils.PhoneListUtil;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventOnlineStatus;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.RxJavaUtil;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.PySortView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import io.realm.RealmResults;
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
    private FriendViewModel viewModel;
    private FriendMainFragmentAdapter adapter;
    private UserAction userAction = new UserAction();
    private int numberLimit = 500;//通讯录上传数量限制，手机联系人数量超过这个数，则分批次上传，显示等待框，加载完成后等待框消失
    private int needUploadTimes = 0;//批次上传-需要上传的次数
    private int hadUploadTimes = 0;//批次上传-已经上传的次数
    private List<List<PhoneBean>> subList;//批次上传-切割后的数据
    private boolean ifSub = false;//是否存在批次上传

    @Override
    public void onResume() {
        super.onResume();
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
        checkContactsPhone();
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
        //显示右侧字母
        if (viewModel.getFriends() != null && viewModel.getFriends().isLoaded())
            viewType.addItemView(userParseString());
        initEvent();
    }

    private ApplicationRepository.FriendChangeListener friendChangeListener = new ApplicationRepository.FriendChangeListener() {
        @Override
        public void init(RealmResults<UserInfo> friends) {
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
            RealmResults<UserInfo> friends = viewModel.getFriends();
            //数据库中存储的是Z1，便于排序
            if (friends != null) {
                for (int i = 0; i < friends.size(); i++) {
                    String tag = friends.get(i).getTag();
                    list.add(tag);
                    //默认第一项是头部，这里位置得+1
                    viewType.putTag(tag, i + 1);
                }
            }
        } catch (Exception e) {
        }
        return list;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
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
                if (MyAppLication.INSTANCE().repository != null)
                    MyAppLication.INSTANCE().repository.loadMoreFriends();
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
                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                    ToastUtil.show(getResources().getString(R.string.user_disable_message));
                    return;
                }
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
                case CoreEnum.ERosterAction.PHONE_MATCH:// 手机通讯录匹配
                    mtListView.getListView().getAdapter().notifyDataSetChanged();
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

    //是否tcp已经连接成功，避免抢占TCP网络资源
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshOnlineStatus(EventOnlineStatus event) {
        if (event.isOn()) {
            viewModel.requestUsersOnlineStatus();
        }
    }

    /**
     * 检查是否打开访问通讯录权限，打开了则上传通讯录，第一次全量上传后面增量上传
     */
    private void checkContactsPhone() {
        try {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                // 24小时检查一次
                long time = SpUtil.getSpUtil().getSPValue(Preferences.CHECK_FRIENDS_TIME, -1l);
                if (time != -1l && com.luck.picture.lib.tools.DateUtils.checkTimeDifferenceHour(time)) {
                    return;
                }
                MsgDao msgDao = new MsgDao();
                PhoneListUtil phoneListUtil = new PhoneListUtil();
                RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<List<PhoneBean>>() {

                    @Override
                    public List<PhoneBean> doInBackground() throws Throwable {
                        return phoneListUtil.getContacts(getContext());
                    }

                    @Override
                    public void onFinish(List<PhoneBean> newList) {
                        if (newList == null) {
                            return;
                        }
                        // 记录上传时间
                        SpUtil.getSpUtil().putSPValue(Preferences.CHECK_FRIENDS_TIME, System.currentTimeMillis());
                        UserDao userDao = new UserDao();
                        // 获取本地保存的记录，有则增量上传，没有全量上传
                        List<PhoneBean> oldList = userDao.getLocaPhones();
                        // 保存最新的通讯录
                        userDao.updateLocaPhones(newList);
                        if (oldList == null || oldList.size() == 0) {
                            boolean isFirstUpload = SpUtil.getSpUtil().getSPValue(Preferences.IS_FIRST_UPLOAD_PHONE, true);
                            //分批次上传请求
                            if (newList.size() > numberLimit) {
                                ifSub = true;
                                subList = new ArrayList<>();
                                subList.addAll(CommonUtils.subWithLen(newList, numberLimit));//拆分成多个List按批次上传
                                needUploadTimes = subList.size();
                                taskUserMatchPhone(subList.get(hadUploadTimes), isFirstUpload);//默认先传第一部分
                            } else {
                                ifSub = false;
                                taskUserMatchPhone(newList, isFirstUpload);
                            }
                        } else {
                            // 获取新增加的联系人
                            List<String> tempList = UserUtil.getNewContentsPhone(newList, oldList);
                            // 增量上传
                            if (tempList.size() > 0) {
                                // 手机通讯录匹配红点加1
                                msgDao.remidCount(Preferences.RECENT_FRIENDS_NEW, tempList.size(), true);
                                MessageManager.getInstance().notifyRefreshFriend(true, -1l, CoreEnum.ERosterAction.PHONE_MATCH);//刷新首页 通讯录底部小红点

                                for (String phone : tempList) {
                                    CommonUtils.saveFriendInfo(-1l, phone);
                                    // 用户记录红点 是那个手机号的红点
                                    CommonUtils.saveFriendInfo(phone);
                                }
                                incrementUpload(tempList);
                            }
                            // 是否有删除
                            int redCount = msgDao.remidGet(Preferences.RECENT_FRIENDS_NEW);
                            if (redCount > 0 && newList.size() != oldList.size()) {
                                int subIndex = 0;
                                // 删除手机号集合
                                List<String> deleteList = UserUtil.getDeleteContentsPhone(newList, oldList);
                                // 红点手机号集合
                                List<FriendInfoBean> redList = CommonUtils.getRedFriendInfo();
                                if (deleteList != null && redList != null) {
                                    // 计算删除的条数
                                    for (FriendInfoBean bean : redList) {
                                        for (String phone : deleteList) {
                                            if (!TextUtils.isEmpty(bean.getPhone()) && bean.getPhone().equals(phone)) {
                                                subIndex++;
                                                break;
                                            }
                                        }
                                    }
                                }
                                // 更新红点
                                if (subIndex > 0) {
                                    int newRedCount = redCount - subIndex;
                                    msgDao.remidCount(Preferences.RECENT_FRIENDS_NEW, newRedCount > 0 ? newRedCount : 0, false);
                                    MessageManager.getInstance().notifyRefreshFriend(true, -1l, CoreEnum.ERosterAction.PHONE_MATCH);//刷新首页 通讯录底部小红点
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.writeLog("=======获取通讯录失败了=========");
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    /**
     * 通讯录匹配 全量上传
     *
     * @param phoneList
     */
    private void taskUserMatchPhone(List<PhoneBean> phoneList, boolean isFirstUpload) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("phoneList", phoneList);
        params.put("isFirst", isFirstUpload ? CoreEnum.ECheckType.YES : CoreEnum.ECheckType.NO);
        userAction.getUserMatchPhone(params, new CallBack<ReturnBean<AddressBookMatchingBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<AddressBookMatchingBean>> call, Response<ReturnBean<AddressBookMatchingBean>> response) {
                super.onResponse(call, response);
                if (response.body() != null && response.body().isOk()) {
                    SpUtil.getSpUtil().putSPValue(Preferences.IS_FIRST_UPLOAD_PHONE, false);
                    LogUtil.writeLog("=======通讯录全量上传成功=========");
                    if (ifSub) {
                        hadUploadTimes++;
                        if (hadUploadTimes < needUploadTimes) {
                            taskUserMatchPhone(subList.get(hadUploadTimes), isFirstUpload);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<AddressBookMatchingBean>> call, Throwable t) {
                super.onFailure(call, t);
                LogUtil.writeLog("=======通讯录全量上传失败=========");
            }
        });
    }

    /**
     * 通讯录增量上传
     *
     * @param list
     */
    private void incrementUpload(List<String> list) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("phoneList", list);
        userAction.getIncrementContacts(params, new CallBack<ReturnBean<List<FriendInfoBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<FriendInfoBean>>> call, Response<ReturnBean<List<FriendInfoBean>>> response) {
                super.onResponse(call, response);
                if (response.body() != null && response.body().isOk()) {
                    LogUtil.writeLog("=======通讯录增量上传成功=========" + new Gson().toJson(list));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<FriendInfoBean>>> call, Throwable t) {
                super.onFailure(call, t);
                LogUtil.writeLog("=======通讯录增量上传失败=========" + new Gson().toJson(list));
            }
        });
    }
}
